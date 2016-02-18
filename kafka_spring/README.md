# Spring Boot - Kafka - Spark Stream

based on http://msvaljek.blogspot.de/2015/12/stream-processing-with-spring-kafka_80.html

## initialisation
### kafka
```
$ cd your_kafka_installation_dir
$ cp config/server.properties config/server0.properties
$ cp config/server.properties config/server1.properties
```

config node 0
```
$ vi config/server0.properties
```
```
broker.id=0
listeners=PLAINTEXT://:9092
num.partitions=2
log.dirs=/var/tmp/kafka-logs-0
```

config node 1
```
$ vi config/server1.properties
```
```
broker.id=1
listeners=PLAINTEXT://:9093
num.partitions=2
log.dirs=/var/tmp/kafka-logs-1
```

create topic
```
$ bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic votes --partitions 2 --replication-factor 2
```

### cassandra
```
CREATE KEYSPACE voting
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
    };
 
USE voting;
 
CREATE TABLE votes (name text PRIMARY KEY, votes int);
```

## startup
### Kafka
zookeeper
```
$ bin/zookeeper-server-start.sh config/zookeeper.properties &
```
node 1
```
$ bin/kafka-server-start.sh config/server0.properties &
```
node 2
```
$ bin/kafka-server-start.sh config/server1.properties &
```

#### optional
producing messages
```
$ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic votes
```
consuming messages
```
$ bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic votes
```
