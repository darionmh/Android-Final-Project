package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    Map<Item, Integer> cart;
    double total = 0.0;

    CartListener cartListener;
    public interface CartListener{
        Map<Item, Integer> getCart();
    }

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ListView cartLV = (ListView) view.findViewById(R.id.cartLV);

        TextView totalTV = (TextView) view.findViewById(R.id.cartTotalCostTV);
        cart = cartListener.getCart();

        Set keys = cart.keySet();
        ArrayList<Item> cartList = new ArrayList<>();
        cartList.addAll(keys);
        ArrayList<Integer> quantities = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < cartList.size(); i++)
        {
            quantities.add(cart.get(cartList.get(i)));

            names.add(cartList.get(i).getName());
        }
        CartArrayAdapter cartAdapter = new CartArrayAdapter(getContext(),R.layout.cart_list_item,R.id.cartItemNameTV,cartList,quantities);
        cartLV.setAdapter(cartAdapter);
        for(int i = 0; i < cartList.size();i++)
        {
            total += cartList.get(i).getPrice() * quantities.get(i);
        }
        totalTV.setText(String.format("$%.2f", total));

        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        cartListener = (CartListener) context;
    }

}

class CartArrayAdapter extends ArrayAdapter<Item> {
    List<Integer> itemQuantities;
    public CartArrayAdapter(Context context, int resource, int textViewResourceId, List<Item> objects, List<Integer> itemQuantities) {
        super(context, resource, textViewResourceId, objects);
        this.itemQuantities = itemQuantities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        TextView name = (TextView) view.findViewById(R.id.cartItemNameTV);
        TextView quantity = (TextView) view.findViewById(R.id.cartItemQuantityTV);
        TextView price = (TextView) view.findViewById(R.id.cartItemPriceTV);

        Item currentItem = getItem(position);

        name.setText(currentItem.getName());
        quantity.setText(itemQuantities.get(position));
        price.setText(String.format("$%.2f", currentItem.getPrice()));

        Log.d("Test", String.format("%s\n%d\n%0.2f\n\n", currentItem.getName(), itemQuantities.get(position), currentItem.getPrice()));
        return view;
    }

}
