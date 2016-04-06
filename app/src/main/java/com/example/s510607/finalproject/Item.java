package com.example.s510607.finalproject;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by darionhiggins on 3/4/16.
 */
public class Item extends GenericJson{
    @Key
    private String name;

    @Key
    private String description;

    @Key
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
