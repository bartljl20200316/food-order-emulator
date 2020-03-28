package com.cloud.kitchen.food.order.emulator;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.dto.TempEnum;
import com.cloud.kitchen.food.order.emulator.execution.DriverThread;
import com.cloud.kitchen.food.order.emulator.model.Kitchen;
import com.cloud.kitchen.food.order.emulator.model.Shelf;
import com.cloud.kitchen.food.order.emulator.services.kafka.OrderConsumer;
import com.cloud.kitchen.food.order.emulator.services.kafka.OrderProducer;
import com.cloud.kitchen.food.order.emulator.utils.KitchenConsts;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class KitchenIntegrationTest {

    private List<Order> orderList;
    private String jsonFile = "Orders.json";

    private Map<String, Shelf> originShelfMap;
    private Shelf originOverflowShelf;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderConsumer orderConsumer;

    @Before
    public void setup() throws IOException  {
        originShelfMap = Kitchen.getInstance().getShelfMap();
        originOverflowShelf = originShelfMap.get(TempEnum.OVERFLOW.toString());

        Map<String, Shelf> mockMap = new ConcurrentHashMap<>();
        for(String type: KitchenConsts.SHELF_LIST) {// Set shelf capacity to 1
            mockMap.put(type, new Shelf(type, 1));
        }
        Kitchen.getInstance().setShelfMap(mockMap);
        Kitchen.getInstance().setOverFlowShelf(mockMap.get(TempEnum.OVERFLOW.toString()));

        loadOrderFromFile();
    }

    @After
    public void reset()  {
        Kitchen.getInstance().setShelfMap(originShelfMap);
        Kitchen.getInstance().setOverFlowShelf(originOverflowShelf);
    }

    private void loadOrderFromFile() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + jsonFile);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(resource.getInputStream()));
        Gson gson = new GsonBuilder().create();
        orderList = gson.fromJson(jsonReader, new TypeToken<List<Order>>() {}.getType());
    }

    @Test
    public void testTenOrder() {
        int orderNum = 10;
        int driverNum = 5;

        for(int i = 0; i < orderNum; i++) {
            orderProducer.send(orderList.get(i));
        }

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(driverNum);
        for(int i = 0; i < driverNum; i++) {
            executorService.scheduleWithFixedDelay(new DriverThread(), 1, 1, TimeUnit.SECONDS);
        }

        await().atMost(Duration.ofSeconds(100))
                .untilAsserted(() -> assertThat(Kitchen.getInstance().isAllShelvesEmpty()).isTrue());
    }
}
