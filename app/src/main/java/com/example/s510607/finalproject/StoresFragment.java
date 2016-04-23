package com.example.s510607.finalproject;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoresFragment extends Fragment {

    Store[] stores;
    public StoresFragment() {
        // Required empty public constructor
    }


    public interface StoreFragmentListener{
        void updateStore(Store store);
    }

    StoreFragmentListener storeFragmentListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        storeFragmentListener = (StoreFragmentListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stores, container, false);

        //Hides the keyboard
        InputMethodManager keyboard = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);

        //Creates an arraylist of all of the store names for the listview
        ArrayList<String> storeNames = new ArrayList<>();
        for(Store s:stores){
            storeNames.add(s.getName());
        }

        ListView storesLV = (ListView) view.findViewById(R.id.storesLV);
        ArrayAdapter<String> storesAA = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, storeNames);
        storesLV.setAdapter(storesAA);

        storesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //On item click, the store is passed to the CategoriesFragment, which is then shown
                CategoriesFragment categoriesFragment = new CategoriesFragment();
                categoriesFragment.setStore(stores[position]);
                storeFragmentListener.updateStore(stores[position]);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,categoriesFragment );
                transaction.commit();
            }
        });

        return view;
    }

    public void setStores(Store[] stores){
        this.stores = stores;
    }

}
