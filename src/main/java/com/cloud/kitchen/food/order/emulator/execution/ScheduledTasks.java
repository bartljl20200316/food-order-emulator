package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import com.cloud.kitchen.food.order.emulator.utils.KitchenNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloud.kitchen.food.order.emulator.dto.Order;

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);


    /**
     * Orders that have reached a value of zero are considered waste and should be removed from the shelves.
     */
    @Scheduled(fixedRate = 500, initialDelay = 2000)
    public void checkOrderValue() {
        for(Shelf s : Kitchen.getInstance().getShelfMap().values()) {
            checkZeroValue(s.getOrders());
        }
    }

    /**
     * Print order number details.
     */
    @Scheduled(fixedRate = 5000, initialDelay = 5000)
    public void printKitchenStatus() {
        logger.info("Total received order is {}, picked up order is {}, wasted order is {}",
                KitchenNumber.getOrderCount().get(),
                KitchenNumber.getPickCount().get(),
                KitchenNumber.getWasteCount().get());
    }

    private void checkZeroValue(PriorityBlockingQueue<Order> orders) {
        Iterator<Order> it = orders.iterator();
        while(it.hasNext()) {
            Order order = it.next();
            if(order.getValue() <= 0) {
                KitchenNumber.getWasteCount().getAndIncrement();
                logger.info("Order {} value reached 0, considered waste and removed from shelf", order);
                Shelf shelf = order.getShelf();
                shelf.remove(order);
                logger.info("Total removed order is {}", KitchenNumber.getWasteCount().get());
            }
        }
    }
}
