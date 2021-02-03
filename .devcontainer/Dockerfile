FROM mcr.microsoft.com/vscode/devcontainers/universal:linux

USER root

# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>

ARG SWAGGER_CODEGEN_CLI_VERSION="3.0.24"
RUN wget "https://repo1.maven.org/maven2/io/swagger/codegen/v3/swagger-codegen-cli/${SWAGGER_CODEGEN_CLI_VERSION}/swagger-codegen-cli-${SWAGGER_CODEGEN_CLI_VERSION}.jar" -O /usr/local/bin/swagger-codegen-cli.jar
    
# Install Gauge
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -SsL https://downloads.gauge.org/stable | sh \
    && su codespace -c "gauge install python" \
    && su codespace -c "gauge install html-report"

USER codespace

RUN npm install -g @stoplight/prism-cli
