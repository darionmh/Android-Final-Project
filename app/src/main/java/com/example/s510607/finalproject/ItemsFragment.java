package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {
    Category category;
    ArrayList<Item> items;
    ListView itemsLV;
    ItemsArrayAdapter itemAdapter;

    public ItemsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        itemsLV = (ListView) view.findViewById(R.id.itemsLVCust);
        items = category.getItems();
        if(items.isEmpty()){
            items.add(new Item("There are no items.", "", 0));
        }
        itemAdapter = new ItemsArrayAdapter(getActivity(),R.layout.item_list_item,R.id.itemNameTV,items);
        itemsLV.setAdapter(itemAdapter);

        return view;


    }

    public void setCategory(Category category){
        this.category = category;
    }

}

class ItemsArrayAdapter extends ArrayAdapter<Item> {
    public ItemsArrayAdapter(Context context, int resource, int textViewResourceId, List<Item> items) {
        super(context, resource, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        TextView name = (TextView) view.findViewById(R.id.itemNameTV);
        TextView description = (TextView) view.findViewById(R.id.itemDescTV);
        TextView price = (TextView) view.findViewById(R.id.itemPriceTV);

        Item currentItem = getItem(position);

        name.setText(currentItem.getName());
        description.setText(currentItem.getDescription());
        price.setText(String.format("$%.2f", currentItem.getPrice()));

        return view;
    }

}
