package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class DriverThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(DriverThread.class);

    @Override
    public void run() {
        Shelf shelf = Kitchen.getInstance().getShelfMap().get(getRandomShelf());
        Order order = pickUp(shelf.getOrders());
        if(order != null) {
            logger.info("Drive {} picked up the order {}", Thread.currentThread().getName(), order);
        }
    }

    /**
     *  Drive Pick up a random order
     *
     * @param orders
     * @return
     */
    private synchronized Order pickUp(PriorityBlockingQueue<Order> orders) {
        if(orders.size() == 0) {
            return null;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(orders.size());
        int i = 0;
        Iterator<Order> it = orders.iterator();

        while(it.hasNext()) {
            if(randomIndex == i) {
                return it.next();
            }
            i++;
        }
        return null;
    }

    private String getRandomShelf() {
        return Kitchen.SHELF_TYPE_LIST.get(ThreadLocalRandom.current().nextInt(Kitchen.SHELF_TYPE_LIST.size()));
    }
}
