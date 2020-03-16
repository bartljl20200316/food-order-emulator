package com.cloud.kitchen.food.order.emulator.kafka.consumer;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import com.cloud.kitchen.food.order.emulator.dto.Order;

public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(topics = "${kakfa.topic.json}")
    public void receive(Order order) {
        LOGGER.info("received order='{}'", order.toString());
        latch.countDown();
    }
}
