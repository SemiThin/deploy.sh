## arch: x86_64
ARG IMAGE_NAME=ubuntu

## arch: arm64
# ARG IMAGE_NAME=arm64v8/ubuntu

FROM ${IMAGE_NAME}:22.04

ARG IN_CHINA=false
ARG CHANGE_SOURCE=false
ARG PHP_VERSION=8.1

ENV PHP_VERSION=${PHP_VERSION}

EXPOSE 80 443 9000
# WORKDIR /var/www/html
WORKDIR /app
CMD ["bash", "/opt/run.sh"]

# SHELL ["/bin/bash", "-o", "pipefail", "-c"]
COPY ./root/ /
RUN set -xe; bash /opt/build.sh

ONBUILD COPY ./root/ /
ONBUILD RUN if [ -f /opt/onbuild.sh ]; then bash /opt/onbuild.sh; else :; fi

# podman build --force-rm --format=docker -t deploy/base:php8.1 -f Dockerfile.node.base .
# docker build --force-rm -t deploy/base:php8.1 -f Dockerfile.node.base .