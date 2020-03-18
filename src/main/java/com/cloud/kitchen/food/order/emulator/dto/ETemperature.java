package com.cloud.kitchen.food.order.emulator.dto;

import com.google.gson.annotations.SerializedName;

public enum ETemperature {

    @SerializedName("hot")
    HOT,

    @SerializedName("cold")
    COLD,

    @SerializedName("frozen")
    FROZEN
}
