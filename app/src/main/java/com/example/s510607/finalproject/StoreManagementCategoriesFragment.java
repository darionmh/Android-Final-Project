package com.example.s510607.finalproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
        ArrayList<String> getCategories();
        void renameCategory(String oldName, String newName);
        void deleteCategory(String category);
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

        //Hides the keyboard
        InputMethodManager keyboard = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

        storeName = getArguments().getString("store_name");

        buildListView();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        buildListView();
    }

    //Method used to build the listView
    //It is in its own method so that it can be easily updated
    public void buildListView(){
        final ListView categoriesLV = (ListView) rootView.findViewById(R.id.categoriesLV);
        TextView title = (TextView) rootView.findViewById(R.id.StoreNameTV);

        title.setText(storeName);
        categoryList = storeManagementCategoriesListener.getCategories();

        ArrayAdapter<String> CategoriesAA = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, categoryList);

        categoriesLV.setAdapter(CategoriesAA);

        //On click, the category is passed to the StoreManagementItemsFragment, which is then shown
        categoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StoreManagementItemsFragment itemFragment = new StoreManagementItemsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryList.get(position));
                itemFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, itemFragment);
                transaction.commit();
            }
        });

        //On long click, the user is given the option to rename the category, delete it, or cancel
        categoriesLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater li = LayoutInflater.from(getActivity());

                final View dialogView = li.inflate(R.layout.category_rename_dialog, null);
                final EditText categoryName = (EditText) dialogView.findViewById(R.id.categoryET);
                final String oldName = categoryList.get(position);

                categoryName.setText(oldName);

                builder.setView(dialogView);
                builder.setTitle("Add Item");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storeManagementCategoriesListener.renameCategory(oldName, categoryName.getText().toString());
                        categoryList = storeManagementCategoriesListener.getCategories();
                        buildListView();
                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Are you sure?")
                                .setPositiveButton("Yes, delete.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        storeManagementCategoriesListener.deleteCategory(oldName);
                                        categoryList = storeManagementCategoriesListener.getCategories();
                                        buildListView();
                                    }
                                })
                                .setNegativeButton("No",null)
                                .create().show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


                return true;
            }
        });
    }

    //Method called from main activity to update the categorylist and rebuild the listview
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
