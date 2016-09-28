FROM openjdk:8-alpine

MAINTAINER roi@logz.io

RUN apk add --no-cache bash

ADD go.sh /root

RUN chmod a+x /root/go.sh

CMD /root/go.sh

ADD packages/ /root