package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HotShelf implements Shelf {

    private static Map<String, Order> orders;
    private static HotShelf hotShelf;

    private HotShelf() {
        orders = new ConcurrentHashMap<>();
    }

    public static HotShelf getInstance() {
        if (hotShelf == null) {
            hotShelf = new HotShelf();
        }
        return hotShelf;
    }

    @Override
    public void add() {

    }

    @Override
    public void remove() {

    }

    @Override
    public boolean isFull() {
        return orders.size() == CAPACITY;
    }
}
