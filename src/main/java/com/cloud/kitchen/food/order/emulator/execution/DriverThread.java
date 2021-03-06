package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import com.cloud.kitchen.food.order.emulator.utils.KitchenNumber;
import com.cloud.kitchen.food.order.emulator.utils.KitchenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class DriverThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DriverThread.class);
    private Object waitObj = new Object();

    @Override
    public void run() {
        synchronized (waitObj) {
            while (Kitchen.getInstance().isAllShelvesEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        int time = ThreadLocalRandom.current().nextInt(2, 11);
        logger.info("Driver {} needs {} seconds to arrive to pick up order", Thread.currentThread().getId(), time);
        try {
            Thread.sleep(time * 1000L);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

        pickUp();
    }

    private synchronized void pickUp() {
        Order order = KitchenUtils.getRandomOrder();
        if(order != null) {
            Shelf shelf = order.getShelf();
            shelf.remove(order);
            KitchenNumber.getPickCount().getAndIncrement();
            logger.info("Driver {} picked up the order {}", Thread.currentThread().getId(), order);
            logger.info("Total picked up order is {}", KitchenNumber.getPickCount().get());
        }
    }

}
