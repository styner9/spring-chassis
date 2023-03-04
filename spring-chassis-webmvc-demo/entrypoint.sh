#!/bin/bash

########################################
# Env
########################################
if [ -z $APP_NAME ]; then
    echo "can't find 'APP_NAME'"
    exit 1
fi

if [ -z $APP_PROFILE ]; then
    echo "can't find 'APP_PROFILE'"
    exit 1
fi

export APP_PHASE=$(echo "$APP_PROFILE" | tr ',' '\n' | sed 's/ //g' | grep -E '^(dev|staging|prod)$')
if [ -z $APP_PHASE ]; then
    echo "can't resolve 'APP_PHASE'"
    exit 1
fi

echo "Resolved app info"
echo "- name    : ${APP_NAME}"
echo "- phase   : ${APP_PHASE}"
echo "- profile : ${APP_PROFILE}"


########################################
# JVM Options
########################################

#---------------------------------------
# JVM Options: default
#---------------------------------------
export JAVA_OPTS="-server \
                  -XX:+AlwaysPreTouch \
                  -Djava.security.egd=file:/dev/./urandom \
                  -Dspring.profiles.active=${APP_PROFILE}"

if [[ ! -z "$HEAP_DUMP_PATH" ]]; then
    mkdir -p ${HEAP_DUMP_PATH}/${APP_NAME}
    export JAVA_OPTS="${JAVA_OPTS} \
                      -XX:+HeapDumpOnOutOfMemoryError \
                      -XX:HeapDumpPath=${HEAP_DUMP_PATH}/${APP_NAME}"
fi

if [[ ! -z "$GC_LOG_PATH" ]]; then
    mkdir -p ${GC_LOG_PATH}/${APP_NAME}
    export JAVA_OPTS="${JAVA_OPTS} \
                      -Xlog:gc*:${GC_LOG_PATH}/${APP_NAME}/gc.log:time,level,tags:filecount=20,filesize=10m"
fi

if [[ ! -z "$REMOTE_DEBUG_PORT" && ${APP_PHASE} != "prod" ]]; then
    export JAVA_OPTS="${JAVA_OPTS} \
                      -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:${REMOTE_DEBUG_PORT}"
fi

#---------------------------------------
# JVM Options: custom
#---------------------------------------


# `jvmopt_preset` in Dockerfile.yml

# undefined -> default, g1
export JAVA_OPTS="${JAVA_OPTS} \
                  -XX:+UseG1GC \
                  -XX:+ParallelRefProcEnabled \
                  -XX:-ResizePLAB"



########################################
# Run
########################################
exec java ${JAVA_OPTS} -jar ${APP_JAR_FILE}
