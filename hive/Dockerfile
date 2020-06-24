FROM openjdk:8u212-jre

USER root

# env variables
ENV HIVE_VERSION 2.3.7
ENV HIVE_HOME /home/hadoop/hive
ENV HIVE_INSTALL_DIR=$HIVE_HOME

# incompatible guava version with 3.2.1
ENV HADOOP_VERSION 3.2.0
ENV HADOOP_HOME=/home/hadoop/hadoop
ENV HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop

ENV PATH $HIVE_HOME/bin:$JAVA_HOME/bin:$HADOOP_HOME/bin:$PATH

# add user
RUN useradd -m -s /bin/bash hadoop

# download source
RUN apt-get update \
    && apt-get install -y wget vim postgresql libpostgresql-jdbc-java

RUN wget https://archive.apache.org/dist/hadoop/core/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz -P /home/hadoop/ \
    && tar -xzf /home/hadoop/hadoop-$HADOOP_VERSION.tar.gz -C /home/hadoop/ \
    && mv /home/hadoop/hadoop-$HADOOP_VERSION /home/hadoop/hadoop \
    && rm -rf /home/hadoop/hadoop-$HADOOP_VERSION*

RUN wget https://archive.apache.org/dist/hive/hive-$HIVE_VERSION/apache-hive-$HIVE_VERSION-bin.tar.gz -P /home/hadoop/ \
    && tar -xzf /home/hadoop/apache-hive-$HIVE_VERSION-bin.tar.gz -C /home/hadoop/ \
    && mv /home/hadoop/apache-hive-$HIVE_VERSION-bin /home/hadoop/hive \
    && rm -rf /home/hadoop/apache-hive-$HIVE_VERSION*

RUN mkdir -p /home/hadoop/data \
    && mkdir -p /home/hadoop/hadoop/logs

# permissions
RUN chown hadoop -R /home/hadoop/data \
    && chown hadoop -R /home/hadoop/hadoop

# copy resources
RUN cp /usr/share/java/postgresql-jdbc4.jar /home/hadoop/hive/lib/
ADD conf/hive-site.xml $HIVE_HOME/conf/hive-site.xml
ADD conf/core-site.xml $HADOOP_CONF_DIR/core-site.xml

RUN echo "export HADOOP_HOME=/home/hadoop/hadoop" >> /home/hadoop/hive/bin/hive-config.sh
RUN chown hadoop -R /home/hadoop/hive
RUN export LANGUAGE=en_US.UTF-8

COPY startup.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/startup.sh

EXPOSE 8020
EXPOSE 9000
EXPOSE 10000
EXPOSE 10002
EXPOSE 50111

# start postgres and create hive user and db
USER postgres
RUN export PATH=$PATH:/etc
RUN service postgresql start \
    && psql --command "CREATE USER hive WITH SUPERUSER PASSWORD 'hive';" \
    && psql --command "CREATE DATABASE metastore"

# enable remote access to postgres
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.6/main/pg_hba.conf

USER root

CMD startup.sh
