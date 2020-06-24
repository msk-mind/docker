# Hive

Basic Hive docker.

- Hive 2.3.7
- Hadoop 3.2.0
- Postgres 9.6

## Build docker
```
docker build -t hive .
```
or
```
make
```

## Run docker
```
docker run --name hive hive:latest
```
or
```
docker run -p 8020:8020 msk-mind/hive:latest
```

## Interfacing with outside hadoop nodes

Currently configured to run hadoop commands to hdfs://host.docker.internal:8020. The current user is 'root', so run:
```
hdfs dfs -chown root /
```
If you want to give permission to write.
