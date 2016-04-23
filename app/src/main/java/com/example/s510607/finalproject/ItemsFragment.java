package com.example.s510607.finalproject;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    int quantity = 0;

    Item itemToSend;

    public interface ItemsReceiver{
        public void ItemSend(Item item,int q);
    }

    ItemsReceiver iReceiver;

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
        itemsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View clickView, final int position, long id) {

                final Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.quantity_dialog_layout);
                d.setTitle("Select Quantity");
                final EditText dialogET = (EditText) d.findViewById(R.id.quantityDialogET);
                if(dialogET.requestFocus()) {
                    InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(dialogET, 0);
                }
                Button okButton = (Button) d.findViewById(R.id.dialogOKBTN);
                Button cancel = (Button) d.findViewById(R.id.dialogCancelBTN);
                okButton.setOnClickListener(new AdapterView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialogET.getText().toString().equals("")) {
                            d.cancel();
                        }
                        quantity = Integer.parseInt(dialogET.getText().toString());
                        iReceiver.ItemSend(items.get(position), quantity);
                        d.dismiss();
                        Toast.makeText(getContext(), "Item added to cart!", Toast.LENGTH_SHORT).show();
                    }
                });

                cancel.setOnClickListener(new AdapterView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.cancel();
                    }
                });
                d.show();
                quantity = 0;
            }
        });



        return view;


    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        iReceiver = (ItemsReceiver) context;
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
