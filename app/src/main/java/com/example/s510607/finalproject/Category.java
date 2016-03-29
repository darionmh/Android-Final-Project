package com.example.s510607.finalproject;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by S510607 on 3/28/2016.
 */
public class Category extends GenericJson {

    @Key
    private String name;

    @Key
    private HashMap<String, Item> items;

    public Category(String name){
        this.name = name;
        this.items = new HashMap<>();
    }

    public Category(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addItem(Item item){
        if(items.containsKey(item.getName())){
            return false;
        }
        items.put(item.getName(), item);
        return true;
    }

    public boolean removeItem(Item item){
        if(!items.containsKey(item.getName())){
            return false;
        }
        items.remove(item.getName());
        return true;
    }

    public Item getItem(String name){
        return items.get(name);
    }

    public int renameItem(String oldName, String newName){
        if(!items.containsKey(oldName)){
            return 0;
        }else if(items.containsKey(newName)){
            return -1;
        }
        Item item = items.remove(oldName);
        item.setName(newName);
        items.put(newName, item);
        return 1;
    }
}
