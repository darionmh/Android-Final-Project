package com.example.s510607.finalproject;

/**
 * Created by darionhiggins on 3/4/16.
 */
public class Item {
    private String name;
    private String description;
    private double price;

    public Item(){
        name = "";
        description = "";
        price = 0;
    }

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}
