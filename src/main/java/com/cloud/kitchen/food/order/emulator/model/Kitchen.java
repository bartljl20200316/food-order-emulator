package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;

public class Kitchen {

    private static final Logger logger = LoggerFactory.getLogger(Kitchen.class);

    private static volatile Kitchen instance;
    private Shelf hotShelf, coldShelf, frozenShelf, overFlowShelf;

    private Kitchen() {
        hotShelf = new Shelf(TempEnum.HOT, KitchenConsts.NORMAL_SHELF_CAPACITY);
        coldShelf = new Shelf(TempEnum.COLD, KitchenConsts.NORMAL_SHELF_CAPACITY);
        frozenShelf = new Shelf(TempEnum.FROZEN, KitchenConsts.NORMAL_SHELF_CAPACITY);
        overFlowShelf = new Shelf(TempEnum.OVERFLOW, KitchenConsts.OVERFLOW_SHELF_CAPACITY);
    }

    public static synchronized Kitchen getInstance() {
        if (instance == null) {
            instance = new Kitchen();
        }
        return instance;
    }

    public void dispatch(Order order) {
        boolean isDispatched = false;

        switch (order.getTemp()) {
            case HOT:
                if(hotShelf.getOrders().size() < KitchenConsts.NORMAL_SHELF_CAPACITY) {
                    hotShelf.add(order);
                    isDispatched = true;
                }
                break;
            case COLD:
                if(coldShelf.getOrders().size() < KitchenConsts.NORMAL_SHELF_CAPACITY) {
                    coldShelf.add(order);
                    isDispatched = true;
                }
                break;
            case FROZEN:
                if(frozenShelf.getOrders().size() < KitchenConsts.NORMAL_SHELF_CAPACITY) {
                    frozenShelf.add(order);
                    isDispatched = true;
                }
                break;
        }

        if(!isDispatched) {
            if(overFlowShelf.getOrders().size() < KitchenConsts.OVERFLOW_SHELF_CAPACITY) {
                overFlowShelf.add(order);
            } else if(isAllShelvesFull()) {
                Order removed = removeOrder(order);
                removed.getShelf().add(order);
            } else {
                //TODO If shelves free up, you may move an order back from the overflow shelf.

            }
        }
    }

    // If all shelves are full, remove order which value is smallest among that type shelf
    private Order removeOrder(Order order) {
        Order removedOrder = null;

        try {
            switch (order.getTemp()) {
                case HOT:
                    removedOrder = hotShelf.getOrders().take();
                    break;
                case COLD:
                    removedOrder = coldShelf.getOrders().take();
                    break;
                case FROZEN:
                    removedOrder = frozenShelf.getOrders().take();
                    break;
            }
        }catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        removedOrder.getShelf().display();
        logger.info("All shelves are full, remove an order {} which value is {} from {} shelf",
                removedOrder, removedOrder.getValue(), removedOrder.getShelf().getType());

        return removedOrder;
    }

    private void moveOrder(PriorityBlockingQueue<Order> from, PriorityBlockingQueue<Order> to) {
        try {
            to.put(from.take());
        }catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

    }

    private boolean isAllShelvesFull() {
        return hotShelf.isFull() && coldShelf.isFull()
                && frozenShelf.isFull() && overFlowShelf.isFull();
    }
}
