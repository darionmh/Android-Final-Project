package com.example.s510607.finalproject;

/**
 * Created by darionhiggins on 3/4/16.
 */
public class Item {
    private String name;
    private String description;
    private double price;
    private int quantity;

    public Item(){
        name = "";
        description = "";
        price = 0;
        quantity = 0;
    }

    public Item(String name, String description, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
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

    public int getQuantity(){return quantity;}
}
