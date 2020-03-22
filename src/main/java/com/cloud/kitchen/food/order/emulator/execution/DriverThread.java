package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class DriverThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DriverThread.class);

    AtomicInteger totalPicked = new AtomicInteger(0);

    @Override
    public void run() {
        if(Kitchen.getInstance().isAllShelvesEmpty()) {
            logger.info("All the shelves are empty, total picked up orders is {}, total wasted order is {}",
                    totalPicked, Kitchen.getInstance().getTotalRemoved().get() + ScheduledTasks.getTotalZero());
            return;
        }

        int time = ThreadLocalRandom.current().nextInt(2, 11);
        logger.info("Driver {} needs {} seconds to arrive to pick up order", Thread.currentThread().getId(), time);
        try {
            Thread.sleep(time * 1000L );
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

        pickUp();
    }

    /**
     *  Drive Pick up a random order
     *
     * @param orders
     * @return
     */
    private Order getRandomOrder(PriorityBlockingQueue<Order> orders) {
        if(orders.isEmpty()) {
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

    private synchronized void pickUp() {
        Shelf shelf = Kitchen.getInstance().getShelfMap().get(getRandomShelf());
        Order order = getRandomOrder(shelf.getOrders());
        if(order != null) {
            shelf.remove(order);
            totalPicked.getAndIncrement();
            logger.info("Driver {} picked up the order {}", Thread.currentThread().getId(), order);
            logger.info("Total picked up order is {}", totalPicked);
        }
    }

}
