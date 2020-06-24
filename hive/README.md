# Hive

Basic Hive docker.

- Hive 2.3.7
- Hadoop 3.2.0
- Postgres 9.6

## Build & run docker container
```
make run
```

## Stop & remove docker container
```
make clean
```

## Interfacing with outside hadoop nodes

Currently configured to run hadoop commands to hdfs://host.docker.internal:8020. The current user is 'root', so run:
```
hadoop fs -mkdir /user/hive
hdfs  dfs -chown root /user/hive
```
If you want to give permission to write.
NOTE: A better way to managing permissions is needed!
