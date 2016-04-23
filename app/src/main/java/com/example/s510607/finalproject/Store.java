package com.example.s510607.finalproject;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.HashMap;

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

    //Adds a new category to the list if one with the same name doesn't exist
    public boolean addCategory(Category category){
        if(categories.containsKey(category.getName())){
            return false;
        }
        categories.put(category.getName(), category);
        return true;
    }

    //Removes the category from the list if it exists
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

    //Attempts to rename the category
    //Returns 1 if successful, 0 if the original name didn't exist, and -1 if it already has one with the new name
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

    //Returns a list of all of the category names
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
