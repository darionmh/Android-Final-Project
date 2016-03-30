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


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreManagementCategoriesFragment extends Fragment{


    private View rootView;
    private ArrayList<String> categoryList;
    private String storeName;

    public interface StoreManagementCategoriesListener{
        public ArrayList<Item> getItems(String category);
        public ArrayList<String> getCategories();
    }

    private StoreManagementCategoriesListener storeManagementCategoriesListener;

    public StoreManagementCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_store_management_categories, container, false);

        storeName = getArguments().getString("store_name");

        buildListView();

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        buildListView();
    }

    public void buildListView(){
        ListView categoriesLV = (ListView) rootView.findViewById(R.id.categoriesLV);
        TextView title = (TextView) rootView.findViewById(R.id.StoreNameTV);

        title.setText(storeName);
        categoryList = storeManagementCategoriesListener.getCategories();

        ArrayAdapter<String> CategoriesAA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, categoryList);

        categoriesLV.setAdapter(CategoriesAA);

        categoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StoreManagementItemsFragment itemFragment = new StoreManagementItemsFragment();
                itemFragment.updateListView(storeManagementCategoriesListener.getItems(categoryList.get(position)));
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryList.get(position));
                itemFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,itemFragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public void updateListView(ArrayList<String> list) {
        categoryList = list;
        buildListView();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        storeManagementCategoriesListener = (StoreManagementCategoriesListener) context;
    }

}
