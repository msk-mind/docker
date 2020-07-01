FROM openjdk:8u212-jre

USER root

ENV PATH $JAVA_HOME/bin:$PATH

COPY startup.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/startup.sh

USER root

CMD startup.sh
