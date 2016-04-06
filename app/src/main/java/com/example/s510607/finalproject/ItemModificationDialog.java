package com.example.s510607.finalproject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemModificationDialog extends DialogFragment {

    public ItemModificationDialog() {
        // Required empty public constructor
    }

    StoreManagementItemsFragment itemsFragment;
    Item item;
    String category;

    @Override
    public Dialog onCreateDialog(Bundle sis){
        LayoutInflater li = LayoutInflater.from(getActivity());
        final View dialogView = li.inflate(R.layout.add_item_dialog, null);
        final EditText itemName = (EditText) dialogView.findViewById(R.id.itemNameET);
        final EditText itemPrice = (EditText) dialogView.findViewById(R.id.itemPriceET);
        final EditText itemDesc = (EditText) dialogView.findViewById(R.id.itemDescET);

        itemName.setText(item.getName());
        itemPrice.setText(item.getPrice()+"");
        itemDesc.setText(item.getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Item")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemsFragment.updateItem(item, itemName.getText().toString(), Double.parseDouble(itemPrice.getText().toString()), itemDesc.getText().toString());
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Are you sure?")
                                .setPositiveButton("Yes, delete.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        itemsFragment.deleteItem(item);
                                    }
                                })
                                .setNegativeButton("No",null)
                                .create().show();
                    }
                })
                .setNegativeButton("Cancel", null);
        return builder.create();
    }

    public void setItemAndCategory(Item item, String category){
        this.item = item;
        this.category = category;
    }

    public void setItemsFragment(StoreManagementItemsFragment itemsFragment){
        this.itemsFragment = itemsFragment;
    }
}
