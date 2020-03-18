package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.Order;

public interface Shelf {

    void add(Order order);

    void remove();

    void display();

    boolean isFull();
}
