package com.cloud.kitchen.food.order.emulator.model;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
//@SpringBootTest
public class ShelfTest {

    Order order;

    @Before
    public void setup() {
        order = new Order();
        order.setName("Banana Split");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(20);
        order.setDecayRate(0.5f);
    }

    @After
    public void reset() {
        resetOrder();
    }

    private void resetOrder() {
        order.setShelf(null);
        order.setDecayRate(0.5f);
        order.setOnShelfTime(0L);
    }

    @Test
    public void testAddOrder() {
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), 1);
        shelf.add(order);

        assertThat(order.getOnShelfTime()).isGreaterThan(0);
        assertThat(order.getValue()).isGreaterThan(0);
        assertThat(order.getShelf().getType()).isEqualTo(TempEnum.FROZEN.toString());
        assertThat(shelf.getOrders().size()).isEqualTo(1);
    }

    @Test
    public void testAddOrderToOverFlowShelf() {
        Shelf shelf = new Shelf(TempEnum.OVERFLOW.toString(), 1);
        float decayRate = 0.5f;

        order.setDecayRate(decayRate);
        shelf.add(order);

        assertThat(order.getOnShelfTime()).isGreaterThan(0);
        assertThat(order.getValue()).isGreaterThan(0);
        assertThat(order.getShelf().getType()).isEqualTo(TempEnum.OVERFLOW.toString());
        assertThat(shelf.getOrders().size()).isEqualTo(1);
        assertThat(order.getDecayRate()).isEqualTo(decayRate * 2);
    }

    @Test
    public void testRemoveOrder() {
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), 1);
        shelf.add(order);

        boolean result = shelf.remove(order);

        assertThat(result).isTrue();
        assertThat(shelf.isEmpty()).isTrue();
    }

    @Test
    public void testRemoveNonExistingOrder() {
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), 1);
        shelf.add(order);

        Order nonExist = new Order();
        order.setName("Non exist order");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(20);
        order.setDecayRate(0.5f);

        boolean result = shelf.remove(nonExist);

        assertThat(result).isFalse();
    }

    @Test
    public void testOrderQueueSequenceAfterAdd() {
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), 2);

        Order order1, order2;
        order1 = new Order();
        order2 = new Order();

        order1.setName("Sherbet");
        order1.setTemp(TempEnum.FROZEN);
        order1.setShelfLife(175);
        order1.setDecayRate(0.6f);

        order2.setName("Ice");
        order2.setTemp(TempEnum.FROZEN);
        order2.setShelfLife(100);
        order2.setDecayRate(0.9f);

        shelf.add(order1);
        shelf.add(order2);

        assertThat(shelf.getMinValueOrder()).isEqualTo(order2);
        shelf.remove(order2);
        assertThat(shelf.getMinValueOrder()).isEqualTo(order1);
    }
}
