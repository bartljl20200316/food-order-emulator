package com.cloud.kitchen.food.order.emulator.services.kafka;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.utils.KitchenNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.cloud.kitchen.food.order.emulator.dto.Order;

@Service
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private CountDownLatch latch;

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @KafkaListener(topics = "${kafka.topic.json}")
    public void receive(List<Order> orders) {
        logger.info("received order='{}'", orders);
        orders.forEach(order-> {
            KitchenNumber.getOrderCount().getAndIncrement();
            Kitchen.getInstance().dispatch(order);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
