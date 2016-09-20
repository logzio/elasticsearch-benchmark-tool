FROM openjdk:8-alpine

MAINTAINER roi@logz.io

RUN apk add --no-cache bash

ADD packages/jmx2graphite-1.1.0-javaagent.jar /root
ADD go.sh /root

RUN chmod a+x /root/go.sh

CMD /root/go.sh