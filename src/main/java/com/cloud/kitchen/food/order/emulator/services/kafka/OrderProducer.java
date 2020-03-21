package com.cloud.kitchen.food.order.emulator.services.kafka;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class OrderProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);

    @Value("${kafka.topic.json}")
    private String jsonTopic;

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    @Async
    public void send(Order order) {
        ListenableFuture<SendResult<String, Order>> future = kafkaTemplate.send(jsonTopic, order);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Order>>() {
            @Override
            public void onSuccess(SendResult<String, Order> result) {
                logger.info("Sending order='{}'", order.toString());
            }
            @Override
            public void onFailure(Throwable ex) {
                logger.error(ex.getMessage());
            }
        });
    }

}
