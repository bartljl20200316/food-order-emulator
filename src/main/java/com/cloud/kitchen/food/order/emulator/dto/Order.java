package com.cloud.kitchen.food.order.emulator.dto;

import com.cloud.kitchen.food.order.emulator.model.Shelf;
import java.util.Objects;

public class Order implements Comparable<Order> {

    private String name;
    private TempEnum temp;
    private int shelfLife;
    private float decayRate;

    private long onShelfTime;
    private Shelf shelf;

    public Order() {}

    public float getValue() {
        float age = (System.currentTimeMillis() - onShelfTime) / 1000;
        return (shelfLife - age) - decayRate * age;
    }

    public float getNormalizedValue() {
        return getValue() / shelfLife;
    }

    public String getName() {
        return name;
    }

    public TempEnum getTemp() {
        return temp;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public float getDecayRate() {
        return decayRate;
    }

    public Shelf getShelf() { return shelf; }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(TempEnum temp) {
        this.temp = temp;
    }

    public void setShelfLife(int shelfLife) {
        this.shelfLife = shelfLife;
    }

    public void setDecayRate(float decayRate) {
        this.decayRate = decayRate;
    }

    public void setOnShelfTime(long time) { this.onShelfTime = time; }

    public void setShelf(Shelf shelf) { this.shelf = shelf; }

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", temp=" + temp +
                ", shelfLife=" + shelfLife +
                ", decayRate=" + decayRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Order)) {
            return false;
        }
        Order order = (Order) o;
        return order.getTemp().equals(temp) && order.getName().equals(name)
                && order.getDecayRate() == decayRate && order.getShelfLife() == shelfLife;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, temp, shelfLife, decayRate);
    }

    @Override
    public int compareTo(Order o) {
        return Float.compare(o.getValue(), this.getValue());
    }
}
