package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;


public class Shelf {

    private static final Logger logger = LoggerFactory.getLogger(Shelf.class);

    private TempEnum type;
    private int capacity;
    private PriorityBlockingQueue<Order> orders;

    public Shelf(TempEnum type, int capacity) {
        this.type = type;
        this.capacity = capacity;
    }

    public TempEnum getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public PriorityBlockingQueue<Order> getOrders() {
        return orders;
    }

    public void add(Order order) {
        if(type == TempEnum.OVERFLOW) {
            order.setDecayRate(order.getDecayRate() * 2);
        }

        order.setOnShelfTime(System.currentTimeMillis());
        order.setShelf(this);
        orders.put(order);

        logger.info("Add an order {} to {} shelf", order, type);
        display();
    }

    public boolean remove(Order order) {
        if(orders.remove(order)) {
            logger.info("Remove an order {} from {} shelf", order, type);
            display();
            return true;
        }

        return false;
    }

    public Order getMinValueOrder() {
        return orders.peek();
    }

    public boolean isFull() {
        if(type == TempEnum.OVERFLOW) {
            return orders.size() == KitchenConsts.OVERFLOW_SHELF_CAPACITY;
        }else {
            return orders.size() == KitchenConsts.NORMAL_SHELF_CAPACITY;
        }
    }

    public void display() {
        logger.info("--------------Shelf Content--------------");
        orders.forEach(order ->
                logger.info("Order is {}, normalized value is {}", order, order.getNormalizedValue())
        );
        logger.info("--------------Shelf Content--------------");
    }
}
