package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class Kitchen {

    private static final Logger logger = LoggerFactory.getLogger(Kitchen.class);

    private static Kitchen instance;
    private Shelf hotShelf, coldShelf, frozenShelf, overFlowShelf;
    public static Map<TempEnum, Shelf> shelfMap = new ConcurrentHashMap<>();

    private Kitchen() {
        hotShelf = new Shelf(TempEnum.HOT, KitchenConsts.NORMAL_SHELF_CAPACITY);
        coldShelf = new Shelf(TempEnum.COLD, KitchenConsts.NORMAL_SHELF_CAPACITY);
        frozenShelf = new Shelf(TempEnum.FROZEN, KitchenConsts.NORMAL_SHELF_CAPACITY);
        overFlowShelf = new Shelf(TempEnum.OVERFLOW, KitchenConsts.OVERFLOW_SHELF_CAPACITY);

        shelfMap.put(TempEnum.HOT, hotShelf);
        shelfMap.put(TempEnum.COLD, coldShelf);
        shelfMap.put(TempEnum.FROZEN, frozenShelf);
        shelfMap.put(TempEnum.OVERFLOW, overFlowShelf);
    }

    public static synchronized Kitchen getInstance() {
        if (instance == null) {
            instance = new Kitchen();
        }
        return instance;
    }

    public void dispatch(Order order) {
        TempEnum temp = order.getTemp();
        Shelf shelf = shelfMap.get(temp);

        if(!shelf.isFull()){
            shelf.add(order);
            return;
        }

        if (!overFlowShelf.isFull()) {
            overFlowShelf.add(order);
        } else { // Overflow shelf is full
            if (isAllShelvesFull()) {
                Order removed = removeShelfOrder();
                if (removed != null) {
                    Shelf s = removed.getShelf();
                    logger.info("All shelves are full, remove an order {} whose value is {} from {} shelf",
                            removed, removed.getValue(), s.getType());
                    if(s.getType().equals(TempEnum.OVERFLOW) || s.getType().equals(temp)) {
                        s.add(order);
                        return;
                    }
                }
            }
            for (TempEnum t : shelfMap.keySet()) {
                Shelf s = shelfMap.get(t);
                if (!s.isFull() && moveOrder(overFlowShelf, s, t)) {
                    overFlowShelf.add(order);
                    return;
                }
            }
        }
    }

    // If all shelves are full, remove order which value is smallest among all shelves
    private Order removeShelfOrder() {
        Order order;
        Shelf shelf;

        PriorityBlockingQueue<Order> queue = new PriorityBlockingQueue<>();
        queue.add(hotShelf.getMinValueOrder());
        queue.add(coldShelf.getMinValueOrder());
        queue.add(frozenShelf.getMinValueOrder());
        queue.add(overFlowShelf.getMinValueOrder());

        order = queue.peek();
        if(order != null) {
            shelf = order.getShelf();
            if(shelf.remove(order)) {
                return order;
            }
        }

        return order;
    }

    private boolean moveOrder(Shelf from, Shelf to, TempEnum type) {
        for(Order order: from.getOrders() ) {
            if(type.equals(order.getTemp()) && from.remove(order)) {
                to.add(order);
                if(from.getType().equals(TempEnum.OVERFLOW)) {
                    order.setDecayRate(order.getDecayRate() / 2);
                }

                logger.info("Move an order {} from {} Shelf to {} Shelf", order, from.getType(), to.getType());
                return true;
            }
        }
        return false;
    }

    private boolean isAllShelvesFull() {
        return hotShelf.isFull() && coldShelf.isFull()
                && frozenShelf.isFull() && overFlowShelf.isFull();
    }
}
