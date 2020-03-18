package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private BlockingQueue<Order> queue;

    public Consumer(BlockingQueue<Order> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            //synchronized (this) {
                for (int i = 0; i < 3; i++) {
                    Order order = queue.take();
                    logger.info("Get order: " + order.toString());
                    dispatch(order);
                }
                logger.info("Get order finished.");

                Thread.sleep(1000);
            //}

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void dispatch(Order order) {

    }
}
