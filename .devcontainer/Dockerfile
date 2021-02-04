FROM mcr.microsoft.com/vscode/devcontainers/universal:linux

USER root

# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>
   
# Install Gauge
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -SsL https://downloads.gauge.org/stable | sh \
    && su codespace -c "gauge install python" \
    && su codespace -c "gauge install html-report"

USER codespace

RUN npm install -g @stoplight/prism-cli \
    && npm install -g @openapitools/openapi-generator-cli

