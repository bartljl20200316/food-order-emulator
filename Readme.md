# Kitchen Emulator

A system that emulates the fulfillment of food orders in kitchen

## Prerequisites
* Maven
* Kafka

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
> cd food-order-emulator
> mvn spring-boot:run
```

## Run unit tests
```
> mvn clean test
```
Test coverage (Jacoco) report will be in:
```

```

## Order Moving Strategy
1. Order is placed on their corresponding shelf according to their temp if the shelf is not full. 
2. If any of the shelf (hot, cold, frozen) is full, when an order comes, it will be put on overflow shelf.
3. If all the shelves including overflow shelf are full, remove an order which has smallest value among all shelves.
4. If the discarded order above is from overflow shelf, then put the upcoming order in overflow shelf. If the discarded order's 
temp is same as upcoming order, put upcoming order on the same shelf. 
5. Otherwise, move an order from overflow shelf to the shelf which discarded order at step 3.
Then put the upcoming order to overflow shelf.


