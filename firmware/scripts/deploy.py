import os
import paramiko
from dotenv import load_dotenv
import select
import argparse
import hashlib

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


def calculate_file_hash(file_path):
    """Calculate SHA-256 hash of a file."""
    hasher = hashlib.sha256()
    with open(file_path, 'rb') as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hasher.update(chunk)
    return hasher.hexdigest()


def sync_files(sftp, local_dir, remote_dir):
    """Sync local directory to remote directory over SSH."""
    ensure_remote_dir_exists(sftp, remote_dir)
    for root, dirs, files in os.walk(local_dir):
        # Skip __pycache__ directories
        dirs[:] = [d for d in dirs if d not in {"__pycache__", ".idea"}]
        relative_path = os.path.relpath(root, local_dir)
        remote_path = os.path.join(remote_dir, relative_path).replace("\\", "/")

        # Ensure the remote directory exists
        try:
            sftp.stat(remote_path)
        except FileNotFoundError:
            sftp.mkdir(remote_path)

        for file_name in files:
            local_file = os.path.join(root, file_name)
            remote_file = f"{remote_path}/{file_name}"

            # Calculate local file hash
            local_hash = calculate_file_hash(local_file)

            # Check remote file hash
            try:
                with sftp.open(remote_file, 'rb') as f:
                    remote_hash = hashlib.sha256(f.read()).hexdigest()
            except FileNotFoundError:
                remote_hash = None

            # If hashes differ or remote file doesn't exist, upload
            if local_hash != remote_hash:
                print(f"Syncing: {local_file} -> {remote_file}")
                sftp.put(local_file, remote_file)


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

    # Copy the controller code (only changed files)
    copy_chapter_appendix = "..." if skip_env else " & install dependencies..."
    chapter(f"Copying code{copy_chapter_appendix}")
    sftp = ssh.open_sftp()
    sync_files(sftp, '.', f'/tmp/firmware')

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
