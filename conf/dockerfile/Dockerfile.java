##############################
#    docker build stage 1    #
##############################

## arch: x86_64
# ARG IMAGE_MVN=maven:3.6-jdk-11
ARG IMAGE_MVN=maven:3.6-jdk-8
ARG IMAGE_JDK=openjdk:8

## arch: arm64
# ARG IMAGE_MVN=arm64v8/maven:3.6-jdk-8
# ARG IMAGE_JDK=arm64v8/openjdk:8

FROM ${IMAGE_MVN} AS builder

ARG IN_CHINA=false
ARG MVN_PROFILE=main
ARG MVN_DEBUG=-q
ARG MVN_COPY_YAML=false
ARG BUILD_URL=https://gitee.com/xiagw/deploy.sh/raw/main/conf/dockerfile/root/opt/build.sh

WORKDIR /src
COPY . .
COPY ./root/ /
# SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN set -xe; \
    if [ -f /opt/build.sh ]; then \
    echo "found /opt/build.sh"; \
    else \
    curl -fLo /opt/build.sh $BUILD_URL; \
    fi
RUN bash /opt/build.sh
# https://blog.frankel.ch/faster-maven-builds/2/
# RUN --mount=type=cache,target=/root/.m2 curl -fL https://gitee.com/xiagw/deploy.sh/raw/main/conf/dockerfile/root/build.sh | bash



##############################
#    docker build stage 2    #
##############################
FROM ${IMAGE_JDK}

ARG IN_CHINA=false
## set startup profile
ARG MVN_PROFILE=main
ARG TZ=Asia/Shanghai
ARG INSTALL_FONTS=false
ARG INSTALL_FFMPEG=false
ARG BUILD_URL=https://gitee.com/xiagw/deploy.sh/raw/main/conf/dockerfile/root/opt/build.sh

ENV TZ=$TZ

WORKDIR /app
COPY --from=builder /jars/ .
COPY ./root/ /
RUN set -xe; \
    if [ -f /opt/build.sh ]; then \
    echo "found /opt/build.sh"; \
    else \
    curl -fLo /opt/build.sh $BUILD_URL; \
    fi; \
    bash /opt/build.sh

USER 1000
EXPOSE 8080 8081 8082
# volume /data

CMD ["bash", "/opt/run.sh"]
