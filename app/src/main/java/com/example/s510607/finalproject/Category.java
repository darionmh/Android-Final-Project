package com.example.s510607.finalproject;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by S510607 on 3/28/2016.
 */
public class Category extends GenericJson {
    @Key("_id")
    private String id;

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
            if(items.containsValue(item)){
                ArrayList<Item> values = new ArrayList(items.values());
                ArrayList<String> keys = new ArrayList(items.keySet());

                int itemIndex = values.indexOf(item);
                String itemKey = keys.get(itemIndex);
                items.remove(itemKey);
                return true;
            }
            return false;
        }
        items.remove(item.getName());
        return true;
    }

    public boolean updateItem(Item item){
        if(!items.containsKey(item.getName())){
            return false;
        }
        items.remove(item.getName());
        items.put(item.getName(),item);
        return true;
    }

    public Item getItem(String name){
        return items.get(name);
    }

    public ArrayList<Item> getItems(){
        return new ArrayList<Item>(items.values());
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
