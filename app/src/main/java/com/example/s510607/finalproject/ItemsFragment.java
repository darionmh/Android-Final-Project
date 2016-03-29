package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {

    ListView itemsLV;
    ItemArrayAdapter itemAdapter;
    public ItemsFragment() {
        // Required empty public constructor
    }

    public interface ItemsCommunicator //Interface to send and receive information from the MainActivity
    {
        public ArrayList<Item> ItemsReceiver();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        // Inflate the layout for this fragment
        itemsLV = (ListView) view.findViewById(R.id.itemsLV);
        itemAdapter = new ItemArrayAdapter(view.getContext(), R.layout.fragment_items);
        itemsLV.setAdapter(itemAdapter);
        return view;
    }

}

class ItemArrayAdapter extends ArrayAdapter {
    public ItemArrayAdapter(Context context, int resource) {super(context,resource);}

     @Override
     public View getView(int position, View oldView, ViewGroup parent)
     {
         return super.getView(position,oldView,parent);
     }

}
