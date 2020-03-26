# Kitchen Emulator

A system that emulates the fulfillment of food orders in kitchen

## Author
Jialong Li

## Prerequisites
* JDK 1.8 (with lambda feature)
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
> mvn clean install -DskipTests
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
## Run unit test
```$xslt
> mvn clean test
```

The test coverage report will be in:
```$xslt
target/jacoco/index.html
```

## Order Moving Strategy
1. Order is placed on their corresponding shelf according to their temp if the shelf is not full. 
2. If any of the shelf (hot, cold, frozen) is full, when a new order comes, it will be put on overflow shelf.
3. If all the shelves including overflow shelf are full, remove an order which has smallest value among all shelves.
4. If the discarded order above is from overflow shelf, then put the new order in overflow shelf. If the discarded order's 
temp is same as upcoming order, put upcoming order on the same shelf. 
5. Otherwise, move an order from overflow shelf to the shelf which discarded order at step 3.
Then put the new order to overflow shelf.
6. Driver will pick up orders randomly.

## How to test the program
* Driver numbers can be configured in source folder application.properties, "driver.number". 
It will decide how many drivers are picking up orders.
* Program will display a log message every 5 seconds about the order numbers.
"_**Total received order is xxx, picked up order is xxx, total wasted order is xxx**_".
The sum of pickup numbers and wasted numbers will be equal to total received number of orders.
* The program will not terminate itself and will wait for new orders coming.

## Extra Credit
The accurate decay formula should be unique per order and depends on its age on overflow shelf.
```$xslt
value = ([shelf life] - [order age]) - ([decay rate] * ([order age] + [order age on overflow]))
```


