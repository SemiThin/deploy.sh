#!/usr/bin/env bash

if [[ -f $gitlab_project_dir/build.gradle ]]; then
    echo_msg step "java build [gradle]..."
    gradle -q
elif [[ "${ENV_BUILD_JAVA_USE_MVN:-1}" = 1 ]]; then
    echo_msg step "java build [maven]..."
    # docker run -i --rm -v "$gitlab_project_dir":/usr/src/mymaven -w /usr/src/mymaven maven:3.6-jdk-8 mvn clean -U package -DskipTests
    docker run -i --rm -v "$gitlab_project_dir":/usr/src/mymaven -w /usr/src/mymaven maven:3.6-jdk-8 mvn clean -U package -DskipTests -P"$MVN_PROFILE"
fi
