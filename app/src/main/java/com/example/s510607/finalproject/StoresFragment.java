package com.example.s510607.finalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoresFragment extends Fragment {

    ArrayList<String> stores;
    public StoresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stores, container, false);
        stores = getArguments().getStringArrayList("stores");

        ListView storesLV = (ListView) view.findViewById(R.id.storesLV);
        ArrayAdapter<String> storesAA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stores);
        storesLV.setAdapter(storesAA);
        assert storesLV != null;
        storesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CategoriesFragment categoriesFragment = new CategoriesFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,categoriesFragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

}
