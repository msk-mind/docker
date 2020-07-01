echo "HELLO MSK"

# prepare hdfs
hadoop fs -mkdir       /tmp
hadoop fs -mkdir -p    /user/hive/warehouse
hadoop fs -chmod g+w   /tmp
hadoop fs -chmod g+w   /user/hive/warehouse
hadoop fs -ls          /

# start db and initialize hive schema
service postgresql start
schematool -dbType postgres -initSchema  
schematool -dbType postgres -info

# start yarn
yarn resourcemanager &
sleep 10s
yarn nodemanager &
sleep 10s

# start metastore and hiveserver2
hive --service metastore &
sleep 10s
hiveserver2
