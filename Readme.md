# Kitchen Emulator

A system that emulates the fulfillment of food orders in kitchen

## Author
Jialong Li

## Prerequisites
* JDK 1.8
* Maven
* Kafka

### Install
Download [Kafka](https://kafka.apache.org/quickstart) if local env don't have.

### Start Kafka
Kafka server should be started before running program or unit test.

For windows, run following command in two terminals to start zookeeper and kafka server:
```
> cd kafka_2.12-2.4.1\
> bin\windows\zookeeper-server-start.bat config\zookeeper.properties
> bin\windows\kafka-server-start.bat config\server.properties
```

## Build
```
> cd food-order-emulator
> mvn clean install
```

## Run program
with maven plugin
```
> mvn spring-boot:run
```
or
```
> java -jar target/food-order-emulator-0.0.1-SNAPSHOT.jar
```

## Run unit tests
```
> mvn clean test
```
Generate test coverage (Jacoco) report:
```
> mvn clean verify
```
The report will be in target/site/jacoco/index.html

## Order Moving Strategy
1. Order is placed on their corresponding shelf according to their temp if the shelf is not full. 
2. If any of the shelf (hot, cold, frozen) is full, when an order comes, it will be put on overflow shelf.
3. If all the shelves including overflow shelf are full, remove an order which has smallest value among all shelves.
4. If the discarded order above is from overflow shelf, then put the upcoming order in overflow shelf. If the discarded order's 
temp is same as upcoming order, put upcoming order on the same shelf. 
5. Otherwise, move an order from overflow shelf to the shelf which discarded order at step 3.
Then put the upcoming order to overflow shelf.

## Result
When my program finished after a while, it will display a log at the end of the terminal, the sum of two numbers will be 
equal to total number of orders.\
"_**All the shelves are empty, total picked up orders is xxx, total wasted order is xxx**_"


