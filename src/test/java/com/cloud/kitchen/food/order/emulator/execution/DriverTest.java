package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
public class DriverTest {

    private Kitchen mockKitchen;
    private Map<String, Shelf> originShelfMap;
    private Shelf originOverflowShelf;

    List<Order> orders;

    @Before
    public void setup() throws IllegalArgumentException {
        originShelfMap = Kitchen.getInstance().getShelfMap();
        originOverflowShelf = originShelfMap.get(TempEnum.OVERFLOW.toString());

        mockKitchen = Kitchen.getInstance();

        Map<String, Shelf> mockMap = new ConcurrentHashMap<>();
        for(String type: KitchenConsts.SHELF_LIST) {// Set shelf capacity to 1
            mockMap.put(type, new Shelf(type, 1));
        }
        mockKitchen.setShelfMap(mockMap);
        mockKitchen.setOverFlowShelf(mockMap.get(TempEnum.OVERFLOW.toString()));

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

        Order order = new Order();

        order.setName("Banana Split");
        order.setTemp(TempEnum.FROZEN);
        order.setShelfLife(20);
        order.setDecayRate(0.63f);

        orders.add(order);
        return orders;
    }

    @Test
    public void testDriverPickUpOrder() {
        orders.forEach(ord -> mockKitchen.dispatch(ord));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        for(int i = 0; i < orders.size() + 1; i++) {
            executor.submit(new DriverThread());
        }
        await().atMost(Duration.ofSeconds(12 * 1000L)).until(() -> mockKitchen.isAllShelvesEmpty());
        executor.shutdown();

        assertThat(mockKitchen.isAllShelvesEmpty()).isTrue();
    }
}
