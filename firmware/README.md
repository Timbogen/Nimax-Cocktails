# Firmware

This is the new version of the firmware (written in python)

## Prerequisites

### Install pyenv && python

```bash
    brew install pyenv && pyenv install 3.11.11
```

Edit .zsh profile

```bash
    echo 'export PYENV_ROOT="$HOME/.pyenv"' >> ~/.zshrc
    echo '[[ -d $PYENV_ROOT/bin ]] && export PATH="$PYENV_ROOT/bin:$PATH"' >> ~/.zshrc
    echo 'eval "$(pyenv init -)"' >> ~/.zshrc
```

See here: https://github.com/pyenv/pyenv

### Install dependencies with poetry

1. Install PipX https://pipx.pypa.io/stable/installation/
2. Install Poetry https://python-poetry.org/docs/
3. Install dependencies

```bash
    poetry env use python3.11 && poetry install
```

## Running the firmware

```bash
    poetry run firmware
```