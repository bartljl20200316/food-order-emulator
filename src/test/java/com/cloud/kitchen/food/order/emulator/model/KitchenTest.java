package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;
import com.cloud.kitchen.food.order.emulator.utils.KitchenMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class KitchenTest {

    private Kitchen mockKitchen;
    private Map<String, Shelf> originShelfMap;
    private Shelf originOverflowShelf;

    List<Order> orders;

    @Before
    public void setup() throws IllegalArgumentException {
        mockKitchen = KitchenMock.initKitchen();
        originShelfMap = KitchenMock.getOriginShelfMap();
        originOverflowShelf = KitchenMock.getOriginOverflowShelf();

        initOrders();
    }

    @After
    public void reset() {
        mockKitchen.setOverFlowShelf(originOverflowShelf);
        mockKitchen.setShelfMap(originShelfMap);
        mockKitchen = null;
        orders = new ArrayList<>();
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
        orders.forEach(order -> mockKitchen.dispatch(order));

        for(String type: KitchenConsts.SHELF_LIST) {
            Shelf shelf = mockKitchen.getShelfMap().get(type);
            if(shelf.getType().equals(TempEnum.OVERFLOW.toString())) {
                assertThat(shelf.getOrders().size()).isEqualTo(0);
            }else {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek().getTemp().toString()).isEqualTo(shelf.getType());
            }
        }
    }

    @Test
    public void testDispatchOrder_WhenShelfFull() {
        Order order = new Order();
        order.setName("McFlury");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(375);
        order.setDecayRate(0.4f);

        orders.add(order);
        orders.forEach(ord -> mockKitchen.dispatch(ord));

        for(String type: KitchenConsts.SHELF_LIST) {
            Shelf shelf = mockKitchen.getShelfMap().get(type);
            if(shelf.getType().equals(TempEnum.OVERFLOW.toString())) {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek()).isEqualTo(order);
            }else {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek().getTemp().toString()).isEqualTo(shelf.getType());
            }
        }
    }

    @Test
    public void testRemoveMinOrder_WhenAllShelvesFull() {
        Order order = new Order();
        order.setName("McFlury");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(375);
        order.setDecayRate(0.4f);

        Order nextOrder = new Order();
        nextOrder.setName("Coke");
        nextOrder.setTemp(TempEnum.COLD);
        nextOrder.setShelfLife(240);
        nextOrder.setDecayRate(0.25f);

        orders.add(order);
        orders.add(nextOrder);
        orders.forEach(ord -> mockKitchen.dispatch(ord));

        for(String type: KitchenConsts.SHELF_LIST) {
            Shelf shelf = mockKitchen.getShelfMap().get(type);
            if(shelf.getType().equals(TempEnum.OVERFLOW.toString())) {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek()).isEqualTo(nextOrder);
            }else {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek().getTemp().toString()).isEqualTo(shelf.getType());
                if(shelf.getType().equals(TempEnum.FROZEN.toString())) {
                    assertThat(shelf.getOrders().peek()).isEqualTo(order);
                }
            }
        }
    }

    @Test
    public void testRemoveMinOrder_IfRemovedOrderSameTypeAsNewOrder() {
        Order order = new Order();
        order.setName("McFlury");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(375);
        order.setDecayRate(0.4f);

        Order nextOrder = new Order();
        nextOrder.setName("Banana Split");
        nextOrder.setTemp(TempEnum.FROZEN);
        nextOrder.setShelfLife(200);
        nextOrder.setDecayRate(0.63f);

        orders.add(order);
        orders.add(nextOrder);
        orders.forEach(ord -> mockKitchen.dispatch(ord));

        for(String type: KitchenConsts.SHELF_LIST) {
            Shelf shelf = mockKitchen.getShelfMap().get(type);
            if(shelf.getType().equals(TempEnum.OVERFLOW.toString())) {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek()).isEqualTo(order);
            }else {
                assertThat(shelf.getOrders().size()).isEqualTo(1);
                assertThat(shelf.getOrders().peek().getTemp().toString()).isEqualTo(shelf.getType());
                if(shelf.getType().equals(TempEnum.FROZEN.toString())) {
                    assertThat(shelf.getOrders().peek()).isEqualTo(nextOrder);
                }
            }
        }
    }

    @Test
    public void testMoveOrder_WhenNotAllShelvesAreFull() {
        Order order = new Order();
        order.setName("McFlury");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(375);
        order.setDecayRate(0.4f);

        orders.add(order);
        orders.forEach(ord -> mockKitchen.dispatch(ord));

        mockKitchen.getShelfMap().get(TempEnum.FROZEN.toString()).getOrders().poll();

        Order nextOrder = new Order();
        nextOrder.setName("Coke");
        nextOrder.setTemp(TempEnum.COLD);
        nextOrder.setShelfLife(240);
        nextOrder.setDecayRate(0.25f);

        mockKitchen.dispatch(nextOrder);

        assertThat(mockKitchen.getShelfMap().get(TempEnum.FROZEN.toString()).getOrders().peek()).isEqualTo(order);
        assertThat(mockKitchen.getShelfMap().get(TempEnum.OVERFLOW.toString()).getOrders().peek()).isEqualTo(nextOrder);
    }

    @Test
    public void testDispatchOrder_WhenCanNotMoveOrder() {
        Order order = new Order();
        order.setName("McFlury");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(375);
        order.setDecayRate(0.4f);

        Order nextOrder = new Order();
        nextOrder.setName("Coke");
        nextOrder.setTemp(TempEnum.COLD);
        nextOrder.setShelfLife(240);
        nextOrder.setDecayRate(0.25f);

        orders.add(order);
        orders.add(nextOrder);
        orders.forEach(ord -> mockKitchen.dispatch(ord));

        Order frozenOrder = new Order();
        frozenOrder.setName("Pistachio Ice Cream");
        frozenOrder.setTemp(TempEnum.FROZEN);
        frozenOrder.setShelfLife(175);
        frozenOrder.setDecayRate(0.4f);

        mockKitchen.dispatch(frozenOrder);

        assertThat(mockKitchen.getShelfMap().get(TempEnum.FROZEN.toString()).getOrders().peek()).isEqualTo(frozenOrder);
        assertThat(mockKitchen.getShelfMap().get(TempEnum.OVERFLOW.toString()).getOrders().peek()).isEqualTo(nextOrder);
    }
}
