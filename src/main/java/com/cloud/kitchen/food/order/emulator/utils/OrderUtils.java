package com.cloud.kitchen.food.order.emulator.utils;

import com.cloud.kitchen.food.order.emulator.dto.Order;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class OrderUtils {

    public static final String JSON_FILE_NAME = "Orders.json";

    public static void readJsonFile() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(JSON_FILE_NAME));

        } catch (IOException e) {

        }
    }
}
