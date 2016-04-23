package com.example.s510607.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchaseFragment extends Fragment {

    interface purchaseCommunicator //Interface to send and receive information from the MainActivity
    {
        void purchaseSender(CreditCard creditCard, String address);
    }

    purchaseCommunicator purCom;

    EditText cardNumberET;
    EditText expMonthET;
    EditText expYearET;
    EditText securityCodeET;
    EditText streetET;
    EditText cityET;
    EditText stateET;
    EditText zipET;
    CheckBox saveAddressCB;
    TextView totalCostTV;

    String cardNumber;
    int expMonth;
    int expYear;
    int securityCode;

    String street;
    String city;
    String state;
    String zip;

    boolean saveAddress;

    double totalCost;

    public PurchaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);
        cardNumberET = (EditText) view.findViewById(R.id.cardNumberET);
        expMonthET = (EditText) view.findViewById(R.id.expDateMonthET);
        expYearET = (EditText) view.findViewById(R.id.expDateYearET);
        securityCodeET = (EditText) view.findViewById(R.id.securityCodeET);
        streetET = (EditText) view.findViewById(R.id.streetET);
        cityET = (EditText) view.findViewById(R.id.cityET);
        stateET = (EditText) view.findViewById(R.id.stateET);
        zipET = (EditText) view.findViewById(R.id.zipET);
        saveAddressCB = (CheckBox) view.findViewById(R.id.saveCB);
        totalCostTV = (TextView) view.findViewById(R.id.amtDueNumberTV);
        totalCostTV.setText(String.format("$ %.2f", totalCost));

        Button cashButton = (Button) view.findViewById(R.id.cashBTN);
        //If cash button is pressed, cashPressed is called
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashPressed();
            }
        });
        Button cardButton = (Button) view.findViewById(R.id.cardBTN);
        //If card button is pressed, cardPressed is called
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPressed();
            }
        });

        //Shared preferences are loaded to get the saved address, if there is one
        SharedPreferences preferences = getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        streetET.setText(preferences.getString("street", ""));
        cityET.setText(preferences.getString("city",""));
        stateET.setText(preferences.getString("state",""));
        zipET.setText(preferences.getString("zip",""));
        return view;

    }

    //Called when card is pressed
    public void cardPressed()
    {
        //Valididates all of the EditTexts
        //If one is invalid, it is added to the error message
        String errorMessage = "";

        cardNumber = cardNumberET.getText().toString();
        if(cardNumber.length()<16){
            errorMessage += "Invalid card number\n";
        }

        if(expMonthET.getText().toString().equals("")){
            errorMessage += "Invalid exp month\n";
        }else {
            expMonth = Integer.parseInt(expMonthET.getText().toString());
            if (expMonth < 1 || expMonth > 12) {
                errorMessage += "Invalid exp month\n";
            }
        }

        if(expYearET.getText().toString().equals("")){
            errorMessage += "Invalid exp year\n";
        }else {
            expYear = Integer.parseInt(expYearET.getText().toString());
            if (expYearET.getText().toString().length() < 4) {
                errorMessage += "Invalid exp year\n";
            }
        }

        if(securityCodeET.getText().toString().equals("")){
            errorMessage += "Invalid security code\n";
        }else {
            securityCode = Integer.parseInt(securityCodeET.getText().toString());
            if (securityCodeET.getText().toString().length() < 3) {
                errorMessage += "Invalid security code\n";
            }
        }

        String address = getAddress();
        if(errorMessage.equals("") && !address.equals("")){
            //If there are no errors, the information is sent to main activity
            purCom.purchaseSender(new CreditCard(cardNumber, expMonth, expYear, securityCode), address);
        }else{
            //If the address is equal to "", there was an error with it and it is added to the error message
            if(address.equals("")){
                errorMessage += "Invalid address";
            }
            //Since there was an error, the message is shown in an alertdialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid input")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        }
    }

    //Called when cash is pressed
    public void cashPressed()
    {
        //Only validates the address fields
        String address = getAddress();
        if(address.equals("")){
            //If address is invalid, an alert is shown
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid input")
                    .setMessage("Invalid address")
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        }else {
            //Otherwise a blank card is sent with the address to main activity
            purCom.purchaseSender(new CreditCard(), address);
        }
    }

    //Called when either cash or card is pressed
    public String getAddress(){
        //Validates the address fields
        street = streetET.getText().toString();
        city = cityET.getText().toString();
        state = stateET.getText().toString();
        zip = zipET.getText().toString();

        //If any are blank, a blank string is sent to signifiy an error
        if(street.equals("") || city.equals("") || state.equals("") || zip.equals("")){
            return "";
        }


        SharedPreferences preferences = getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(saveAddressCB.isChecked()){
            //If save address is checked, the address is saved to sharedpreferences
            editor.putString("street",street);
            editor.putString("city",city);
            editor.putString("state",state);
            editor.putString("zip", zip);
            editor.commit();
        }else{
            editor.clear();
            editor.commit();
        }
        return String.format("%s, %s, %s %s", street, city, state, zip);
    }

    public void setTotalCost(double totalCost){
        this.totalCost = totalCost;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        purCom = (purchaseCommunicator) context;
    }

}