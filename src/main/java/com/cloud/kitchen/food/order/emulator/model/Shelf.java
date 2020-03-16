package com.cloud.kitchen.food.order.emulator.model;

public interface Shelf {

    int CAPACITY = 15;

    void add();

    void remove();

    boolean isFull();
}
