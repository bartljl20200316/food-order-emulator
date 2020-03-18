package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.Order;

public class ColdShelf implements Shelf {

    private final int CAPACITY = 15;

    private static ColdShelf coldShelf;

    public static ColdShelf getInstance() {
        if (coldShelf == null) {
            coldShelf = new ColdShelf();
        }
        return coldShelf;
    }

    @Override
    public void add(Order order) {

    }

    @Override
    public void remove() {

    }

    @Override
    public void display() {

    }

    @Override
    public boolean isFull() {
        return false;
    }

}
