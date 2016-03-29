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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreManagementCategoriesFragment extends Fragment {

    public interface StoreManagementCategoriesListener{
        public ArrayList<String> getItems(String category);
    }

    private StoreManagementCategoriesListener storeManagementCategoriesListener;

    public StoreManagementCategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ArrayList<String> categoryList = this.getArguments().getStringArrayList("categories");
        View view= inflater.inflate(R.layout.fragment_store_management_main, container, false);
        ListView categoriesLV = (ListView) view.findViewById(R.id.categoriesLV);
        ArrayAdapter<String> CategoriesAA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, categoryList);
        categoriesLV.setAdapter(CategoriesAA);
        categoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StoreManagementItemsFragment itemFragment = new StoreManagementItemsFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("items", storeManagementCategoriesListener.getItems(categoryList.get(position)));
                itemFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,itemFragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        storeManagementCategoriesListener = (StoreManagementCategoriesListener) context;
    }

}
