package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
//@SpringBootTest
public class KitchenTest {

    private Kitchen kitchen;
    List<Order> orders;

    @Before
    public void setup() throws IllegalArgumentException {
        kitchen = mock(Kitchen.class);
        setMock(kitchen);

        initOrders();
    }

    @After
    public void reset() throws NoSuchFieldException, IllegalAccessException {
        Field instance = Kitchen.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, null);

        /*Field overflow = Kitchen.class.getDeclaredField("overFlowShelf");
        overflow.setAccessible(true);
        overflow.set(overflow, null);*/

        Field shelfMap = Kitchen.class.getDeclaredField("shelfMap");
        shelfMap.setAccessible(true);
        shelfMap.set(shelfMap, null);

        orders = new ArrayList<>();
    }

    private void setMock(Kitchen kitchen) throws IllegalArgumentException {
        try {
            Field instance = Kitchen.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, kitchen);

           /* Field overflow = Kitchen.class.getDeclaredField("overFlowShelf");
            overflow.setAccessible(true);
            overflow.set(overflow, new Shelf(TempEnum.OVERFLOW.toString(), 1));*/

            Field shelfMap = Kitchen.class.getDeclaredField("shelfMap");
            shelfMap.setAccessible(true);
            Map<String, Shelf> map = new ConcurrentHashMap<>();
            for(String type: KitchenConsts.SHELF_ARR) {// Set shelf capacity to 1
                map.put(type, new Shelf(type, 1));
            }
            shelfMap.set(shelfMap, map);

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private List<Order> initOrders() {
        orders = new ArrayList<>();

        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();

        order1.setName("Banana Split");
        order1.setTemp(TempEnum.FROZEN);
        order1.setShelfLife(20);
        order1.setDecayRate(0.63f);

        order2.setName("Acai Bowl");
        order2.setTemp(TempEnum.COLD);
        order2.setShelfLife(249);
        order2.setDecayRate(0.3f);

        order3.setName("Pad See Ew");
        order3.setTemp(TempEnum.HOT);
        order3.setShelfLife(210);
        order3.setDecayRate(0.72f);

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        return orders;
    }

    @Test
    public void testDispatchOrder() {
        orders.forEach(order -> kitchen.dispatch(order));

        for(String type: KitchenConsts.SHELF_ARR) {
            Shelf shelf = kitchen.getShelfMap().get(type);
            if(shelf.getType().equals(TempEnum.OVERFLOW.toString())) {
                assertThat(shelf.getOrders().size()).isEqualTo(0);
            }else {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek().getTemp()).isEqualTo(shelf.getType());
            }
        }
    }

    @Test
    public void testDispatchOrderOneShelfFull() {

    }

    @Test
    public void testMoveOrder() {

    }

    @Test
    public void testRemoveOrderWhenAllShelvesFull() {

    }

}
