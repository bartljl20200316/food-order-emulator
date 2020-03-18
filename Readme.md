# Kitchen Emulator

A system that emulates the fulfillment of food orders in kitchen

## Getting Started

### Prerequisites
```
Kafka
```

### Install
Download [Kafka](https://kafka.apache.org/quickstart) if local env don't have.

### Start Kafka
For windows, run following command to start zookeeper and kafka server:
```
> cd kafka_2.12-2.4.1\
> bin\windows\zookeeper-server-start.bat config\zookeeper.properties
> bin\windows\kafka-server-start.bat config\server.properties
```

## Run program
```
> mvn spring-boot:run
```

## Run unit tests
```
> mvn test
```


