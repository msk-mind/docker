
hadoop fs -mkdir       /tmp
hadoop fs -mkdir       /tez
hadoop fs -mkdir -p    /user/hive/warehhouse
hadoop fs -chmod g+w   /tmp
hadoop fs -chmod g+w   /tez
hadoop fs -chmod g+w   /user/hive/warehhouse
hadoop fs -put $HIVE_HOME/lib/hive-exec-$HIVE_VERSION.jar /home/hadoop/tez/

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
