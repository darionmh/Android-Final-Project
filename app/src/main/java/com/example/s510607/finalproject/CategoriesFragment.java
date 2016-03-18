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

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    //dummy string categories for now
    String[] categoriesdata= {"Breakfast","Meal","Desserts","Side Items"};
    public CategoriesFragment() {
        // Required empty public constructor

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_categories, container, false);
        ListView categoriesLV = (ListView) view.findViewById(R.id.categoriesLV);
        ArrayAdapter<String> CategoriesAA = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, categoriesdata);
        categoriesLV.setAdapter(CategoriesAA);
        categoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemsFragment itemFragment = new ItemsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,itemFragment );
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;

    }

}
