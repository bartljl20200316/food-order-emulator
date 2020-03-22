package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Kitchen {

    private static final Logger logger = LoggerFactory.getLogger(Kitchen.class);

    private static Kitchen instance;
    private Shelf overFlowShelf;

    private Map<String, Shelf> shelfMap;
    public static final List<String> SHELF_TYPE_LIST = Arrays.asList(new String[] {"HOT", "COLD", "FROZEN", "OVERFLOW"});

    private AtomicInteger totalRemoved = new AtomicInteger(0);

    private Kitchen() {
        shelfMap = new ConcurrentHashMap<>();
        overFlowShelf = new Shelf(TempEnum.OVERFLOW.toString(), KitchenConsts.OVERFLOW_SHELF_CAPACITY);

        for(String type: SHELF_TYPE_LIST) {
            if(type.equals(TempEnum.OVERFLOW.toString())) {
                shelfMap.put(type, overFlowShelf);
            }else {
                shelfMap.put(type, new Shelf(type, KitchenConsts.NORMAL_SHELF_CAPACITY));
            }
        }
    }

    public static synchronized Kitchen getInstance() {
        if (instance == null) {
            instance = new Kitchen();
        }
        return instance;
    }

    public Map<String, Shelf> getShelfMap() {
        return shelfMap;
    }

    public AtomicInteger getTotalRemoved() { return totalRemoved;}

    /**
     * Dispatch an order according to the following rule:
     *
     * 1. Order is placed on their corresponding shelf according to their temp if the shelf is not full.
     * 2. If any of the shelf (hot, cold, frozen) is full, when an order comes, it will be put on overflow shelf.
     * 3. If all the shelves including overflow shelf are full, remove an order which has smallest value among all shelves.
     * 4. If the discarded order above is from overflow shelf, then put the upcoming order in overflow shelf.
     * If the discarded order'stemp is same as upcoming order, put upcoming order on the same shelf.
     * 5. Otherwise, move an order from overflow shelf to the shelf which discarded order at step 3.Then put the upcoming order to overflow shelf.
     *
     * @param order
     */
    public synchronized void dispatch(Order order) {
        TempEnum temp = order.getTemp();
        Shelf shelf = shelfMap.get(temp.toString());

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
                    logger.info("Total removed order is {}", totalRemoved.get());
                    if(s.getType().equals(TempEnum.OVERFLOW.toString()) || s.getType().equals(temp.toString())) {
                        s.add(order);
                        return;
                    }
                }
            }
            for (String t : shelfMap.keySet()) {
                Shelf s = shelfMap.get(t);
                if (!s.isFull() && moveOrder(overFlowShelf, s, t)) {
                    overFlowShelf.add(order);
                    return;
                }
            }
        }
    }

    /**
     * If all shelves are full, remove order which value is smallest among all shelves
     * @return
     *         A discarded order
     */
    private Order removeShelfOrder() {
        Order minOrder = null;
        Shelf shelf;

        for(Shelf s : shelfMap.values()) {
            if((s.getMinValueOrder() != null) &&
                    (minOrder == null || (minOrder.getValue() > s.getMinValueOrder().getValue()))) {
                minOrder = s.getMinValueOrder();
            }
        }

        if(minOrder != null) {
            shelf = minOrder.getShelf();
            if(shelf.remove(minOrder)) {
                totalRemoved.getAndIncrement();
                return minOrder;
            }
        }

        return minOrder;
    }

    private boolean moveOrder(Shelf from, Shelf to, String type) {
        for(Order order: from.getOrders() ) {
            if(type.equals(order.getTemp().toString()) && from.remove(order)) {
                to.add(order);
                if(from.getType().equals(TempEnum.OVERFLOW.toString())) {
                    order.setDecayRate(order.getDecayRate() / 2);
                }

                logger.info("Move an order {} from {} Shelf to {} Shelf", order, from.getType(), to.getType());
                return true;
            }
        }
        return false;
    }

    public boolean isAllShelvesFull() {
        for (Shelf shelf : shelfMap.values()) {
            if(!shelf.isFull()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllShelvesEmpty() {
        for (Shelf shelf : shelfMap.values()) {
            if(!shelf.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
