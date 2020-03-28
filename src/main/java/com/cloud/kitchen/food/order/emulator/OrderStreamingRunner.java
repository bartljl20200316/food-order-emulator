package com.cloud.kitchen.food.order.emulator;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.execution.DriverThread;
import com.cloud.kitchen.food.order.emulator.services.kafka.OrderConsumer;
import com.cloud.kitchen.food.order.emulator.services.kafka.OrderProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.concurrent.*;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Component
@Profile("!test")
public class OrderStreamingRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OrderStreamingRunner.class);

    @Value("${orders.json.file.name}")
    private String jsonFile;

    @Value("${driver.number}")
    private int driverNum;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderConsumer orderConsumer;

    @Override
    public void run(String... strings) throws Exception {

        try {
            Resource resource = resourceLoader.getResource("classpath:" + jsonFile);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(resource.getInputStream()));
            Gson gson = new GsonBuilder().create();

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(driverNum);
            for(int i = 0; i < driverNum; i++) {
                executorService.scheduleWithFixedDelay(new DriverThread(), 5, 1, TimeUnit.SECONDS);
            }

            logger.info("Start streaming orders...");
            jsonReader.beginArray();

            while (jsonReader.hasNext()){
                Order order = gson.fromJson(jsonReader, Order.class);
                orderProducer.send(order);

                Thread.sleep(300);
            }

            jsonReader.endArray();

        } catch (Exception e) {
           logger.error(e.getMessage());
        }
    }
}
