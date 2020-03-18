package com.cloud.kitchen.food.order.emulator.utils;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.ColdShelf;
import com.cloud.kitchen.food.order.emulator.model.HotShelf;
import com.cloud.kitchen.food.order.emulator.model.Shelf;

public class KitchenUtils {

    public static Shelf dispatch(Order order)  {
        switch (order.getTemp()) {
            case HOT:
                return HotShelf.getInstance();
            case COLD:
                return ColdShelf.getInstance();
            case FROZEN:
                break;
            default:
                break;
        }
        return null;
    }

}
