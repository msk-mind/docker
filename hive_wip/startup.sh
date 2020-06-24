echo "Initalizing folders in HDFS"
hadoop fs -ls          /
hadoop fs -mkdir       /tmp
hadoop fs -mkdir       /tez
hadoop fs -mkdir -p    /user/hive/warehhouse
hadoop fs -chmod g+w   /tmp
hadoop fs -chmod g+w   /tez
hadoop fs -chmod g+w   /user/hive/warehhouse
hadoop fs -put $HIVE_HOME/lib/hive-exec-$HIVE_VERSION.jar /home/hadoop/tez/
hadoop fs -ls          /
echo "DONE"
