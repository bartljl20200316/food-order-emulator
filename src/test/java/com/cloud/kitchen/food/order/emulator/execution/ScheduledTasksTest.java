package com.cloud.kitchen.food.order.emulator.execution;

import com.cloud.kitchen.food.order.emulator.config.scheduled.ScheduledTasksConfig;
import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import com.cloud.kitchen.food.order.emulator.utils.KitchenMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringJUnitConfig(ScheduledTasksConfig.class)
public class ScheduledTasksTest {

    private Kitchen mockKitchen;
    private Map<String, Shelf> originShelfMap;
    private Shelf originOverflowShelf;

    List<Order> orders;

    @SpyBean
    private ScheduledTasks scheduler;

    @Before
    public void setup() {
        mockKitchen = KitchenMock.initKitchen();
        originShelfMap = KitchenMock.getOriginShelfMap();
        originOverflowShelf = KitchenMock.getOriginOverflowShelf();

        initZeroValueOrders();
    }

    @After
    public void reset() {
        mockKitchen.setShelfMap(originShelfMap);
        mockKitchen.setOverFlowShelf(originOverflowShelf);
        mockKitchen = null;
        orders = new ArrayList<>();
    }

    private List<Order> initZeroValueOrders() {
        orders = new ArrayList<>();

        Order order1 = new Order();
        Order order2 = new Order();
        Order order3 = new Order();
        Order order4 = new Order();

        order1.setName("Banana Split");
        order1.setTemp(TempEnum.FROZEN);
        order1.setShelfLife(1);
        order1.setDecayRate(1.1f);

        order2.setName("Acai Bowl");
        order2.setTemp(TempEnum.COLD);
        order2.setShelfLife(1);
        order2.setDecayRate(1.1f);

        order3.setName("Pad See Ew");
        order3.setTemp(TempEnum.HOT);
        order3.setShelfLife(1);
        order3.setDecayRate(1.1f);

        order4.setName("Pad See Ew");
        order4.setTemp(TempEnum.HOT);
        order4.setShelfLife(1);
        order4.setDecayRate(1.1f);

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);
        return orders;
    }

    @Test
    public void testScheduledTaskWasteZeroValueOrder() {
        orders.forEach(order -> mockKitchen.dispatch(order));

        assertThat(mockKitchen.isAllShelvesFull()).isTrue();
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(mockKitchen.isAllShelvesEmpty()).isTrue());
    }
}
