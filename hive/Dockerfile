FROM openjdk:8u212-jre
  
WORKDIR /opt

ENV HIVE_HOME=/opt/apache-hive-3.1.2-bin

RUN curl -L https://www-us.apache.org/dist/hive/hive-3.1.2/apache-hive-3.1.2-bin.tar.gz | tar zxf -

COPY conf/hive-site.xml ${HIVE_HOME}/conf

RUN groupadd -r hive --gid=1000 && \
    useradd -r -g hive --uid=1000 -d ${HIVE_HOME} hive && \
    chown hive:hive -R ${HIVE_HOME}

USER hive
WORKDIR $HIVE_HOME
EXPOSE 9083

ENTRYPOINT ["bin/hive"]
CMD ["--service", "metastore"]
