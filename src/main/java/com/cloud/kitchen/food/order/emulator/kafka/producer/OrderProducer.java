package com.cloud.kitchen.food.order.emulator.kafka.producer;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

public class OrderProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    @Value("${kafka.topic.json}")
    private String jsonTopic;

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    @Async
    public void send(Order order) {
        LOGGER.info("Sending order='{}'", order.toString());
        kafkaTemplate.send(jsonTopic, order);
    }

}
