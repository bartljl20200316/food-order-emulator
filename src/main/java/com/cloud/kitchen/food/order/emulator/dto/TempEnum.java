package com.cloud.kitchen.food.order.emulator.dto;

import com.google.gson.annotations.SerializedName;

public enum TempEnum {

    @SerializedName("hot")
    HOT ("HOT"),

    @SerializedName("cold")
    COLD ("COLD"),

    @SerializedName("frozen")
    FROZEN ("FROZEN"),

    OVERFLOW ("OVERFLOW");

    private String name;

    TempEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

}
