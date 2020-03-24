package com.cloud.kitchen.food.order.emulator.utils;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KitchenUtils {

    /**
     *  Driver will pick up a random order
     *
     * @return
     */
    public static synchronized Order getRandomOrder() {
        // Generate a random shelf which has orders on it
        List<String> shelfList = new ArrayList<>();
        for(String s : KitchenConsts.SHELF_LIST) {
            if(!Kitchen.getInstance().getShelfMap().get(s).isEmpty()) {
                shelfList.add(s);
            }
        }
        if(shelfList.isEmpty()) {
            return null;
        }
        String shelfName = shelfList.get(ThreadLocalRandom.current().nextInt(shelfList.size()));
        Shelf randomShelf = Kitchen.getInstance().getShelfMap().get(shelfName);

        // Generate a random order
        int randomIndex = ThreadLocalRandom.current().nextInt(randomShelf.getOrders().size());
        int i = 0;
        Iterator<Order> it = randomShelf.getOrders().iterator();

        while(it.hasNext()) {
            if(randomIndex == i) {
                return it.next();
            }
            i++;
        }
        return null;
    }
}
