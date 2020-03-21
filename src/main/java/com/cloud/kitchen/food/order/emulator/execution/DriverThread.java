package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(DriverThread.class);

    @Override
    public void run() {
        // Drive Pick up the order
        Order order = Kitchen.getInstance().removeShelfOrder();
        logger.info("Drive {} picked up the order {}", Thread.currentThread().getName(), order);
    }
}
