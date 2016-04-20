package com.example.s510607.finalproject;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by S510607 on 3/28/2016.
 */
public class Store extends GenericJson {
    @Key("_id")
    private String id;

    @Key
    private String name;

    @Key
    private HashMap<String, Category> categories;

    public Store(String name){
        this.name = name;
        this.categories = new HashMap<>();
    }

    public Store(){}

    public String getName(){
        return name;
    }

    public boolean addCategory(Category category){
        if(categories.containsKey(category.getName())){
            return false;
        }
        categories.put(category.getName(), category);
        return true;
    }

    public boolean removeCategory(Category category){
        if(!categories.containsKey(category.getName())){
            return false;
        }
        categories.remove(category.getName());
        return true;
    }

    public Category getCategory(String name){
        return categories.get(name);
    }

    public int renameCategory(String oldName, String newName){
        if(!categories.containsKey(oldName)){
            return 0;
        }else if(categories.containsKey(newName)){
            return -1;
        }
        Category category = categories.remove(oldName);
        category.setName(newName);
        categories.put(newName, category);
        return 1;
    }

    public ArrayList<String> getAllCategories(){
        ArrayList<String> categoryNames = new ArrayList();
        categoryNames.addAll(categories.keySet());
        return categoryNames;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
