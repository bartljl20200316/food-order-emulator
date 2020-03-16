package com.cloud.kitchen.food.order.emulator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KitchenApplication implements CommandLineRunner {

    public static void main(String[] args)  {
        SpringApplication.run(KitchenApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
