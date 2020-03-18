package com.cloud.kitchen.food.order.emulator;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.cloud.kitchen.food.order.emulator.execution.DriverThread;
import com.cloud.kitchen.food.order.emulator.kafka.consumer.OrderConsumer;
import com.cloud.kitchen.food.order.emulator.kafka.producer.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.Random;
import java.util.concurrent.*;


import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Component
public class OrderStreaming implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OrderStreaming.class);

    @Value("${orders.json.file.name}")
    private String jsonFile;

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

            logger.info("Start streaming orders...");
            jsonReader.beginArray();
            while (jsonReader.hasNext()){
                Order order = gson.fromJson(jsonReader, Order.class);
                orderProducer.send(order);
            }

            jsonReader.endArray();
            //orderConsumer.setLatch(new CountDownLatch(count));
            //orderConsumer.getLatch().await(10000, TimeUnit.MILLISECONDS);

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
            int driveTime = new Random().ints(2, 11).limit(1).findFirst().getAsInt();
            ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(new Thread(new DriverThread()), 5, driveTime, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
