package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.PriorityBlockingQueue;

public class Kitchen {

    private static final Logger logger = LoggerFactory.getLogger(Kitchen.class);

    private static Kitchen instance;
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
        TempEnum temp = order.getTemp();

        switch (temp) {
            case HOT:
                if(!hotShelf.isFull()) {
                    hotShelf.add(order);
                    isDispatched = true;
                }
                break;
            case COLD:
                if(!coldShelf.isFull()) {
                    coldShelf.add(order);
                    isDispatched = true;
                }
                break;
            case FROZEN:
                if(!frozenShelf.isFull()) {
                    frozenShelf.add(order);
                    isDispatched = true;
                }
                break;
        }

        if(!isDispatched) {
            if(!overFlowShelf.isFull()) {
                overFlowShelf.add(order);
            } else {
                if(isAllShelvesFull()) {
                    Order removed = removeOrder();
                    if(removed != null) {
                        Shelf removedFromShelf = removed.getShelf();
                        if(removedFromShelf.equals(TempEnum.OVERFLOW) || removedFromShelf.getType().equals(temp)) {
                            removedFromShelf.add(order);
                        }else {
                            moveOrder(overFlowShelf, removedFromShelf, removedFromShelf.getType());
                            overFlowShelf.add(order);
                        }
                    }
                } else {
                    if(temp != TempEnum.HOT && !hotShelf.isFull()) {
                        moveOrder(overFlowShelf, hotShelf, temp);
                    }
                }
            }
        }
    }

    // If all shelves are full, remove order which value is smallest among all shelves
    private Order removeOrder() {
        Order order = null;
        Shelf shelf = null;

        PriorityBlockingQueue<Order> queue = new PriorityBlockingQueue<>();
        queue.add(hotShelf.getMinValueOrder());
        queue.add(coldShelf.getMinValueOrder());
        queue.add(frozenShelf.getMinValueOrder());
        queue.add(overFlowShelf.getMinValueOrder());

        order = queue.peek();
        if(order != null) {
            shelf = order.getShelf();
            if(shelf.remove(order)) {
                logger.info("All shelves are full, remove an order {} which value is {} from {} shelf",
                        order, order.getValue(), shelf.getType());
            }
        }

        return order;
    }

    private void moveOrder(Shelf from, Shelf to, TempEnum type) {
        from.getOrders().forEach(order -> {
            if(type.equals(order.getTemp()) && from.remove(order)) {
                to.add(order);
                if(from.getType().equals(TempEnum.OVERFLOW)) {
                    order.setDecayRate(order.getDecayRate() / 2);
                }

                logger.info("Move an order {} from {} Shelf to {} Shelf", order, from.getType(), to.getType());
                return;
            }
        });

        //TODO If not find same temp type of order

    }

    private boolean isAllShelvesFull() {
        return hotShelf.isFull() && coldShelf.isFull()
                && frozenShelf.isFull() && overFlowShelf.isFull();
    }
}
