#!/usr/bin/env bash

if [ "$GRAPHITE_SERVER" == "" ]; then

    echo "GRAPHITE_SERVER must be passed as environment variable!"
    exit 1
fi

if [ "$GRAPHITE_PREFIX" == "" ]; then

    echo "GRAPHITE_PREFIX must be passed as environment variable!"
    exit 1
fi

if [ "$SERVICE_HOST" == "" ]; then

    echo "SERVICE_HOST must be passed as environment variable!"
    exit 1
fi

if [ "$INTERVAL_IN_SEC" == "" ]; then
    INTERVAL_IN_SEC=10
fi

if [ "$CONFIG_FILE" == "" ]; then
    CONFIG_FILE="/config.conf"
fi

# Find the jar
JAR=`ls /root/ | grep elasticsearch`
JAVA_AGENT="-javaagent:/root/jmx2graphite-1.2.4-javaagent.jar=GRAPHITE_HOSTNAME=$GRAPHITE_SERVER;SERVICE_NAME=$GRAPHITE_PREFIX;SERVICE_HOST=$SERVICE_HOST;INTERVAL_IN_SEC=$INTERVAL_IN_SEC"

java $JAVA_AGENT -jar /root/$JAR --test-config $CONFIG_FILE