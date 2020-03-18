package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {

    private BlockingQueue<Order> queue;
    private List<Order> orders;

    public Producer(BlockingQueue<Order> queue, List<Order> orders) {
        this.queue = queue;
        this.orders = orders;
    }

    @Override
    public void run() {
        orders.forEach( order -> {
            try {
                queue.put(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

}
