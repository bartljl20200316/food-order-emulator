package com.cloud.kitchen.food.order.emulator.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class KitchenNumber {

    private static volatile AtomicInteger pickCount = new AtomicInteger(0);
    private static volatile AtomicInteger wasteCount = new AtomicInteger(0);

    public static AtomicInteger getPickCount() {
        return pickCount;
    }

    public static AtomicInteger getWasteCount() { return wasteCount; }

}
