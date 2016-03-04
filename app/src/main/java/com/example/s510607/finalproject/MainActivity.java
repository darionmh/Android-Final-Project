package com.example.s510607.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CartFragment.CartListener{

    Map<Item, Integer> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cart = new HashMap<Item, Integer>();
    }

    public Map<Item, Integer> getCart(){
        return cart;
    }

    public void cartCancelPress(View view){

    }

    public void cartCheckOutPress(View view){

    }
}
