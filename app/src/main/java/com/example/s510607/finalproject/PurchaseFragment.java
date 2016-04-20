package com.example.s510607.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchaseFragment extends Fragment {

    interface purchaseCommunicator //Interface to send and receive information from the MainActivity
    {
        public void purchaseSender(CreditCard creditCard);
    }

    purchaseCommunicator purCom;

    EditText cardNumberET;
    EditText expMonthET;
    EditText expYearET;
    EditText securityCodeET;
    TextView totalCostTV;
    String cardNumber;
    int expMonth;
    int expYear;
    int securityCode;
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
        totalCostTV = (TextView) view.findViewById(R.id.amtDueNumberTV);
        totalCostTV.setText(totalCost+"");

        Button cashButton = (Button) view.findViewById(R.id.cashBTN);
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashPressed();
            }
        });
        Button cardButton = (Button) view.findViewById(R.id.cardBTN);
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPressed();
            }
        });
        return view;

    }

    public void cardPressed()
    {
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
            errorMessage += "Invalid security code";
        }else {
            securityCode = Integer.parseInt(securityCodeET.getText().toString());
            if (securityCodeET.getText().toString().length() < 3) {
                errorMessage += "Invalid security code";
            }
        }
        if(errorMessage.equals("")){
            purCom.purchaseSender(new CreditCard(cardNumber, expMonth, expYear, securityCode));
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid input")
                    .setMessage(errorMessage)
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        }
    }

    public void cashPressed()
    {
        purCom.purchaseSender(new CreditCard());
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