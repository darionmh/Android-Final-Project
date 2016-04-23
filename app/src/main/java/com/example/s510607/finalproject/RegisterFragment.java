package com.example.s510607.finalproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    public RegisterFragment() {
        // Required empty public constructor
    }

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        RadioGroup userTypeGroup = (RadioGroup) view.findViewById(R.id.userTypeRBG);

        //If customer type is selected, first and last name fields are shown
        //If store type is selected, store name field is shown
        userTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                EditText storeName = (EditText) view.findViewById(R.id.storeNameET);
                EditText firstName = (EditText) view.findViewById(R.id.firstNameET);
                EditText lastName = (EditText) view.findViewById(R.id.lastNameET);
                if(checkedId == R.id.customerRB){
                    firstName.setVisibility(View.VISIBLE);
                    lastName.setVisibility(View.VISIBLE);
                    storeName.setVisibility(View.GONE);
                }else{
                    firstName.setVisibility(View.GONE);
                    lastName.setVisibility(View.GONE);
                    storeName.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

}
