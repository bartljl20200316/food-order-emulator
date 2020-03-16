package com.cloud.kitchen.food.order.emulator.dto;

public class Order {

    private String name;
    private ETemperature temp;
    private int shelfLife;
    private float decayRate;

    private int onShelfTime;
    private int age;
    private int value;

    public Order() {}

    public long getAge( ) {
        return System.currentTimeMillis() - onShelfTime;
    }

    public int getValue() {
        int age = (int)(System.currentTimeMillis() - onShelfTime);
        return (shelfLife - age) - (int)(decayRate * age);
    }

    public float getNormalizedValue() {
        return (float) getValue() / shelfLife;
    }

    public String getName() {
        return name;
    }

    public ETemperature getTemp() {
        return temp;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public float getDecayRate() {
        return decayRate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(ETemperature temp) {
        this.temp = temp;
    }

    public void setShelfLife(int shelfLife) {
        this.shelfLife = shelfLife;
    }

    public void setDecayRate(float decayRate) {
        this.decayRate = decayRate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", temp=" + temp +
                ", shelfLife=" + shelfLife +
                ", decayRate=" + decayRate +
                '}';
    }
}
