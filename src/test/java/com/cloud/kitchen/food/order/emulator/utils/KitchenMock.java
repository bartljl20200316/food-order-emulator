package com.cloud.kitchen.food.order.emulator.utils;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitchenMock {

    private static Kitchen mockKitchen;
    private static Map<String, Shelf> originShelfMap;
    private static Shelf originOverflowShelf;

    public static Kitchen initKitchen() {
        originShelfMap = Kitchen.getInstance().getShelfMap();
        originOverflowShelf = originShelfMap.get(TempEnum.OVERFLOW.toString());

        mockKitchen = Kitchen.getInstance();

        Map<String, Shelf> mockMap = new ConcurrentHashMap<>();
        for(String type: KitchenConsts.SHELF_LIST) {// Set shelf capacity to 1
            mockMap.put(type, new Shelf(type, 1));
        }
        mockKitchen.setShelfMap(mockMap);
        mockKitchen.setOverFlowShelf(mockMap.get(TempEnum.OVERFLOW.toString()));

        return mockKitchen;
    }

    public static Map<String, Shelf> getOriginShelfMap() {
        return originShelfMap;
    }

    public static Shelf getOriginOverflowShelf() {
        return originOverflowShelf;
    }

}
