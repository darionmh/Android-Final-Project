package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    Map<Item, Integer> cart;

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

        return view;
    }

    @Override
    public void onAttach(Context context){
        cartListener = (CartListener) context;
    }

}

class cartArrayAdapter extends ArrayAdapter<Item> {
    List<Integer> itemQuantities;
    public cartArrayAdapter(Context context, int resource, int textViewResourceId, List<Item> objects, List<Integer> itemQuantities) {
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

        return view;
    }

}
