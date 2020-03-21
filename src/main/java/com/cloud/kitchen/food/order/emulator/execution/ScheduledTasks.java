package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    /*
    * Orders that have reached a value of zero are considered waste and should be removed from the shelves.
    */
    @Scheduled(fixedRate = 1000, initialDelay = 2000)
    public void checkOrderValue() {
        for(Shelf s : Kitchen.getInstance().shelfMap.values()) {
            checkZeroValue(s.getOrders());
        }
    }

    private void checkZeroValue(PriorityBlockingQueue<Order> orders) {
        orders.forEach(order -> {
            if(order.getValue() == 0) {
                logger.info("Order {} value reached 0, considered waste and removed from shelf", order);
                Shelf shelf = order.getShelf();
                shelf.remove(order);
            }
        });
    }
}
