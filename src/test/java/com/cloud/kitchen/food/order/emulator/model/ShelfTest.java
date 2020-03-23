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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShelfTest {

    Order order;

    @Before
    public void setup() {
        order = new Order();
        order.setName("Banana Split 1");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(20);
        order.setDecayRate(0.5f);
    }

    @After
    public void tearDown() {
        resetOrder();
    }

    private void resetOrder() {
        order.setShelf(null);
        order.setDecayRate(0.5f);
        order.setOnShelfTime(0L);
    }

    @Test
    public void testAddOrder() {
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), KitchenConsts.NORMAL_SHELF_CAPACITY);
        shelf.add(order);

        assertThat(order.getOnShelfTime()).isGreaterThan(0);
        assertThat(order.getValue()).isGreaterThan(0);
        assertThat(order.getShelf().getType()).isEqualTo(TempEnum.FROZEN.toString());
        assertThat(shelf.getOrders().size()).isEqualTo(1);
    }

    @Test
    public void testAddOrderToOverFlowShelf() {
        Shelf shelf = new Shelf(TempEnum.OVERFLOW.toString(), KitchenConsts.OVERFLOW_SHELF_CAPACITY);
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
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), KitchenConsts.NORMAL_SHELF_CAPACITY);
        shelf.add(order);

        boolean result = shelf.remove(order);

        assertThat(result).isTrue();
        assertThat(shelf.isEmpty()).isTrue();
    }

    @Test
    public void testRemoveOrderEmptyShelf() {
        Shelf shelf = new Shelf(TempEnum.FROZEN.toString(), KitchenConsts.NORMAL_SHELF_CAPACITY);
        boolean result = shelf.remove(order);

        assertThat(result).isFalse();
        assertThat(shelf.isEmpty()).isTrue();
    }
}
