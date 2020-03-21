package com.cloud.kitchen.food.order.emulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KitchenApplication {

    public static void main(String[] args)  {
        SpringApplication.run(KitchenApplication.class, args);
    }

}
