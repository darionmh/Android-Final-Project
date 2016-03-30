package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreManagementItemsFragment extends Fragment {
    ArrayList<Item> items;
    String category;
    View view;

    public StoreManagementItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_store_management_items, container, false);
        category = getArguments().getString("category");
        items = new ArrayList<Item>();

        buildListView();

        return view;
    }

    public void buildListView(){
        if(view == null){
            return;
        }

        ListView categoriesLV = (ListView) view.findViewById(R.id.itemsLV);

        TextView title = (TextView) view.findViewById(R.id.StoreNameTV);

        title.setText(category);

        ItemArrayAdapter CategoriesAA = new ItemArrayAdapter(getContext(), R.layout.item_list_item, R.id.itemNameTV,items);

        categoriesLV.setAdapter(CategoriesAA);
    }

    public void updateListView(ArrayList<Item> list) {
        items = list;
        buildListView();
    }

}

class ItemArrayAdapter extends ArrayAdapter<Item>{

    public ItemArrayAdapter(Context context, int resource, int textViewResourceId, List<Item> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);

        TextView nameTV = (TextView) view.findViewById(R.id.itemNameTV);
        TextView descTV = (TextView) view.findViewById(R.id.itemDescTV);
        TextView priceTV = (TextView) view.findViewById(R.id.itemPriceTV);

        Item currentItem = getItem(position);

        nameTV.setText(currentItem.getName());
        descTV.setText(currentItem.getDescription());
        priceTV.setText(String.format("$%.2f", currentItem.getPrice()));

        return view;
    }
}
