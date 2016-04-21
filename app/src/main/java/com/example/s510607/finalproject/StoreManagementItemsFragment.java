package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public interface StoreManagementItemsListener{
        public ArrayList<Item> getItems(String category);
        public void updateItem(Item item, String category);
        public void renameItem(String oldName, String newName, String category);
        public void deleteItem(Item item, String category);
    }

    StoreManagementItemsListener storeManagementItemsListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        storeManagementItemsListener = (StoreManagementItemsListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_store_management_items, container, false);
        category = getArguments().getString("category");
        if(items == null)items = storeManagementItemsListener.getItems(category);

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

        if(items.isEmpty()){
            items.add(new Item("There are no items.", "", 0));
        }
        ItemArrayAdapter itemsAA = new ItemArrayAdapter(getContext(), R.layout.item_list_item, R.id.itemNameTV,items);

        categoriesLV.setAdapter(itemsAA);

        categoriesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemModificationDialog itemModificationDialog = new ItemModificationDialog();
                itemModificationDialog.setItemsFragment(StoreManagementItemsFragment.this);
                itemModificationDialog.setItemAndCategory(items.get(position), category);
                itemModificationDialog.show(getActivity().getFragmentManager(), "Update Item");
                return true;
            }
        });
    }

    public void updateListView(ArrayList<Item> list) {
        items = list;
        buildListView();
    }

    public void updateItem(Item item, String name, double price, String description){
        if(!name.equals(item.getName())){
            storeManagementItemsListener.renameItem(item.getName(), name, category);
        }

        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);

        storeManagementItemsListener.updateItem(item, category);
        items = storeManagementItemsListener.getItems(category);
        buildListView();
    }

    public void deleteItem(Item item){
        storeManagementItemsListener.deleteItem(item, category);
        items = storeManagementItemsListener.getItems(category);
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
