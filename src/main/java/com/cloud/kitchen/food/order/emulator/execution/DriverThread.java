package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Shelf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(DriverThread.class);

    public DriverThread() {

    }

    @Override
    public void run() {
        // Pick up the order which has smallest value
        Shelf.getInstance().removeMinValueOrder();
    }
}