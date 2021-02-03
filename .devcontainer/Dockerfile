FROM mcr.microsoft.com/vscode/devcontainers/universal:linux

USER root

# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>

USER codespace

RUN npm install -g @stoplight/prism-cli
