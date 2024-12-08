import os
import paramiko
from dotenv import load_dotenv
import select
import argparse
import stat

# Load the .env file
load_dotenv()

# Load pyenv
init_pyenv_command = 'export PYENV_ROOT="$HOME/.pyenv" && command -v pyenv >/dev/null || export PATH="$PYENV_ROOT/bin:$PATH" && eval "$(pyenv init -)"'

# Check if config was loaded successfully
if not os.getenv('RASPBERRY_HOST'):
    raise ValueError(
        "Configuration could not be loaded! Make sure that there is a valid .env file in the root directory!")


def chapter(chapter_title):
    """Print a section to the console"""

    print()
    print("=" * 90)
    print("‖ " + chapter_title)
    print("=" * 90)


def run_ssh_command(ssh, command):
    """Run an ssh command and handle logging"""
    print(f'\n> {command}')
    stdin, stdout, stderr = ssh.exec_command(f"bash -l -c '{command}'", get_pty=True)

    # We’ll monitor both stdout and stderr for incoming data
    channels = [stdout.channel, stderr.channel]

    while True:
        # Use 'select' to wait until data is ready
        readable, _, _ = select.select(channels, [], [], 1.0)
        if stdout.channel in readable:
            for line in stdout:
                print(line.strip())
        if stderr.channel in readable:
            for line in stderr:
                print("ERR:", line.strip())
        if stdout.channel.exit_status_ready() and stderr.channel.exit_status_ready():
            break

    # Get the exit code and raise an error if non-zero
    exit_code = stdout.channel.recv_exit_status()
    if exit_code != 0:
        raise RuntimeError(f"Command \"{command}\" failed with exit code {exit_code}")


def install_python_env(ssh):
    """Install python env if necessary"""
    # Check whether python 3.11 is already installed
    try:
        run_ssh_command(ssh, "python3.11 --version")
    except RuntimeError:
        # Otherwise start installation
        print("Starting installation of Python 3.11...")
        run_ssh_command(ssh, 'sudo apt-get update -y && sudo apt-get upgrade -y')
        run_ssh_command(ssh, 'sudo apt install python3.11')

    # Check whether pipx is installed
    try:
        run_ssh_command(ssh, "pipx --version")
    except RuntimeError:
        # Otherwise start installation
        print("Starting installation of pipx")
        run_ssh_command(ssh, 'sudo apt install pipx -y')
        run_ssh_command(ssh, 'pipx ensurepath')
        run_ssh_command(ssh, 'pipx install poetry')

    # Install poetry dependencies
    run_ssh_command(ssh,
                    'cd /tmp/firmware && poetry env use python3.11 && poetry config keyring.enabled false && poetry install')


def remove_remote_directory(sftp, remote_dir):
    """Recursively remove a directory from remote"""
    try:
        for entry in sftp.listdir_attr(remote_dir):
            entry_path = f"{remote_dir}/{entry.filename}"
            if stat.S_ISDIR(entry.st_mode):
                # If it's a directory, recursively remove it
                remove_remote_directory(sftp, entry_path)
            else:
                # If it's a file, delete it
                sftp.remove(entry_path)
        # Remove the now-empty directory
        sftp.rmdir(remote_dir)
    except FileNotFoundError:
        pass  # Directory doesn't exist, no action needed
    except Exception as e:
        print(f"Error removing directory {remote_dir}: {e}")
        raise


def ensure_remote_dir_exists(sftp, remote_dir):
    """Create directory structure on remote"""

    dirs_ = []
    while len(remote_dir) > 1:
        try:
            sftp.stat(remote_dir)
            break
        except FileNotFoundError:
            dirs_.append(remote_dir)
            remote_dir = os.path.dirname(remote_dir)
    while len(dirs_):
        remote_dir = dirs_.pop()
        sftp.mkdir(remote_dir)


def copy_directory(sftp, local_dir, remote_dir):
    """Copy a local directory to a remote directory"""
    local_dir_abs = os.path.abspath(local_dir)
    print(f'Copy "{local_dir_abs}" to "{remote_dir}"...')

    ensure_remote_dir_exists(sftp, remote_dir)

    for root, dirs, files in os.walk(local_dir_abs):
        root_abs = os.path.abspath(root)
        for dirname in dirs:
            local_path = os.path.join(root_abs, dirname)
            relative_path = os.path.relpath(local_path, local_dir_abs)
            remote_path = os.path.join(remote_dir, relative_path)
            ensure_remote_dir_exists(sftp, remote_path)

        for filename in files:
            local_path = os.path.join(root_abs, filename)
            relative_path = os.path.relpath(local_path, local_dir_abs)
            remote_path = os.path.join(remote_dir, relative_path)
            ensure_remote_dir_exists(sftp, os.path.dirname(remote_path))
            sftp.put(local_path, remote_path)


def start():
    """The actual deployment function"""

    # Parse args
    parser = argparse.ArgumentParser(description="Deploy firmware to Raspberry Pi.")
    parser.add_argument(
        "--skip-env",
        action="store_true",
        help="Skip the Python environment installation step."
    )
    args = parser.parse_args()
    skip_env = args.skip_env

    # Create the connection to the rpi
    chapter("Establishing ssh connection...")
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(
        hostname=os.getenv('RASPBERRY_HOST'),
        username=os.getenv('RASPBERRY_USER'),
        password=os.getenv('RASPBERRY_PW')
    )
    print("Connected.")

    # Copy the controller code
    copy_chapter_appendix = "..." if skip_env else " & install dependencies..."
    chapter(f"Copying code{copy_chapter_appendix}")
    sftp = ssh.open_sftp()
    remove_remote_directory(sftp, '/tmp/firmware')
    copy_directory(sftp, '.', f'/tmp/firmware')

    # Install env
    if not skip_env:
        install_python_env(ssh)

    # Run the code
    chapter("Run code...")
    run_ssh_command(ssh, 'cd /tmp/firmware && sudo -E ~/.local/bin/poetry run firmware')

    # Close the connection to the rpi
    chapter("Closing connection...")
    sftp.close()
    ssh.close()
