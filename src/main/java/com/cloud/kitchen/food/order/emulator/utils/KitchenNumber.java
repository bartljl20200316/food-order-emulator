package com.cloud.kitchen.food.order.emulator.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class KitchenNumber {

    private static AtomicInteger orderCount = new AtomicInteger(0);
    private static AtomicInteger pickCount = new AtomicInteger(0);
    private static AtomicInteger wasteCount = new AtomicInteger(0);

    public static AtomicInteger getPickCount() {
        return pickCount;
    }

    public static AtomicInteger getWasteCount() { return wasteCount; }

    public static AtomicInteger getOrderCount() { return orderCount; }
}
