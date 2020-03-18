package com.cloud.kitchen.food.order.emulator.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;


public class Shelf {

    private static final Logger logger = LoggerFactory.getLogger(Shelf.class);

    private final int SHELF_CAPACITY = 15;
    private final int OVERFLOW_SHELF_CAPACITY = 20;

    private static Shelf shelf;
    private PriorityBlockingQueue<Order> hotShelf, coldShelf, frozenShelf, overFlowShelf;

    private Shelf() {
        hotShelf = new PriorityBlockingQueue<>();
        coldShelf = new PriorityBlockingQueue<>();
        frozenShelf = new PriorityBlockingQueue<>();
        overFlowShelf = new PriorityBlockingQueue<>();
    }

    public static Shelf getInstance() {
        if (shelf == null) {
            shelf = new Shelf();
        }
        return shelf;
    }

    public void dispatch(Order order) {
        switch (order.getTemp()) {
            case HOT:
                add(hotShelf, order);
                break;
            case COLD:
                add(coldShelf, order);
                break;
            case FROZEN:
                add(frozenShelf, order);
                break;
        }
    }

    private void add(PriorityBlockingQueue<Order> shelf, Order order) {
        if(shelf.size() < SHELF_CAPACITY) {
            order.setOnShelfTime(System.currentTimeMillis());
            shelf.put(order);
        } else {
            if(overFlowShelf.size() < OVERFLOW_SHELF_CAPACITY) {
                order.setOnShelfTime(System.currentTimeMillis());
                order.setDecayRate(order.getDecayRate() * 2);
                overFlowShelf.put(order);
            }else {
                if(allFull()) {
                    Order removed = removeMinValueOrder();
                    logger.info("Remove an order " + removed);
                }else {
                    //TODO If not all shelves are full, cache the order and insert at later time
                }
            }
        }
    }

    private boolean allFull() {
        return (hotShelf.size() == SHELF_CAPACITY) && (coldShelf.size() == SHELF_CAPACITY)
                && (frozenShelf.size() == SHELF_CAPACITY) && (overFlowShelf.size() == SHELF_CAPACITY);
    }

    public static Order removeMinValueOrder() {
        //TODO remove order which value is smallest
        return null;
    }
}
