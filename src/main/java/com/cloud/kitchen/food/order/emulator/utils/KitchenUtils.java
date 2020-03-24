package com.cloud.kitchen.food.order.emulator.utils;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class KitchenUtils {

    /**
     *  Driver will pick up a random order
     *
     * @return
     */
    public static synchronized Order getRandomOrder() {

        List<String> shelfList = Arrays.asList(KitchenConsts.SHELF_ARR);
        Shelf randomShelf;
        // Generate a random shelf
        String shelfName = shelfList.get(ThreadLocalRandom.current().nextInt(4));
        randomShelf = Kitchen.getInstance().getShelfMap().get(shelfName);

        if(randomShelf.isEmpty()) {
            return null;
        }

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
