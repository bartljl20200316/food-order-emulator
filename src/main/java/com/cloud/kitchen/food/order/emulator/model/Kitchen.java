package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;

import com.cloud.kitchen.food.order.emulator.utils.KitchenNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Kitchen {

    private static final Logger logger = LoggerFactory.getLogger(Kitchen.class);

    private static Kitchen instance;
    private Shelf overFlowShelf;
    private Map<String, Shelf> shelfMap;

    private Kitchen() {
        shelfMap = new ConcurrentHashMap<>();

        for(String type: KitchenConsts.SHELF_LIST) {
            if(type.equals(TempEnum.OVERFLOW.toString())) {
                shelfMap.put(type, new Shelf(TempEnum.OVERFLOW.toString(), KitchenConsts.OVERFLOW_SHELF_CAPACITY));
            }else {
                shelfMap.put(type, new Shelf(type, KitchenConsts.NORMAL_SHELF_CAPACITY));
            }
        }

        overFlowShelf = shelfMap.get(TempEnum.OVERFLOW.toString());
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

    public void setShelfMap(Map<String, Shelf> shelfMap) {
        this.shelfMap = shelfMap;
    }

    public void setOverFlowShelf(Shelf overFlowShelf) {
        this.overFlowShelf = overFlowShelf;
    }

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
    public void dispatch(Order order) {
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
                    logger.info("Total removed order is {}", KitchenNumber.getWasteCount().get());
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
                KitchenNumber.getWasteCount().getAndIncrement();
                return minOrder;
            }
        }

        return minOrder;
    }

    /**
     *  Move order from one shelf to another shelf according to order type.
     *
     * @param from
     *        From which shelf
     * @param to
     *        Destination shelf
     * @param type
     *        Order type
     * @return
     */
    private boolean moveOrder(Shelf from, Shelf to, String type) {
        for(Order order: from.getOrders() ) {
            if(type.equals(order.getTemp().toString()) && from.remove(order)) {
                if(from.getType().equals(TempEnum.OVERFLOW.toString())) {
                    order.setDecayRate(order.getDecayRate() / 2);
                    // New shelf life should be the value of order
                    order.setShelfLife((int)order.getValue());
                }
                to.add(order);

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
