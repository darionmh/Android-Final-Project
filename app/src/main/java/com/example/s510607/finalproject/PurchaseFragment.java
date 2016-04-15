package com.example.s510607.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.s510607.finalproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchaseFragment extends Fragment {

    interface purchaseCommunicator //Interface to send and receive information from the MainActivity
    {
        public void purchaseSender(Purchase p);
    }

    purchaseCommunicator purCom;

    EditText cardNumberET;
    EditText expMonthET;
    EditText expYearET;
    EditText securityCodeET;
    int cardNumber;
    int expMonth;
    int expYear;
    int securityCode;

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
        return view;

    }

    public void cardPressed()
    {
        cardNumber = Integer.parseInt(cardNumberET.getText().toString());
        expMonth = Integer.parseInt(expMonthET.getText().toString());
        expYear = Integer.parseInt(expYearET.getText().toString());
        securityCode = Integer.parseInt(securityCodeET.getText().toString());
        purCom.purchaseSender(new Purchase(cardNumber,expMonth,expYear,securityCode));


    }

    public void cashPressed()
    {
        purCom.purchaseSender(new Purchase());
    }

    @Override
    public void onAttach(Context context)
    {
        purCom = (purchaseCommunicator) context;
    }

}

class Purchase
{
    int cardNumber=0;
    int expMonth=0;
    int expYear=0;
    int securityCode=0;
    boolean cash;

    public Purchase()
    {
       cash = true;
    }


    public Purchase(int card,int expM,int expY,int secCode)
    {
        cardNumber = card;
        expMonth = expM;
        expYear = expY;
        securityCode = secCode;
        cash = false;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public int getSecurityCode() {
        return securityCode;
    }
    public boolean isCash()
    {
        return cash;
    }
}


