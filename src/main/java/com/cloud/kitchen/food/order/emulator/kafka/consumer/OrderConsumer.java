package com.cloud.kitchen.food.order.emulator.kafka.consumer;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.cloud.kitchen.food.order.emulator.model.Shelf;
import com.cloud.kitchen.food.order.emulator.utils.KitchenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.cloud.kitchen.food.order.emulator.dto.Order;

@Service
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private CountDownLatch latch = new CountDownLatch(3);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(topics = "${kafka.topic.json}")
    public void receive(List<Order> orders) {
        logger.info("received order='{}'", orders);
        orders.forEach(
                order -> System.out.println(order)
        );

        latch.countDown();
        /*Shelf shelf = KitchenUtils.dispatch(order);
        shelf.add(order);*/
    }
}
