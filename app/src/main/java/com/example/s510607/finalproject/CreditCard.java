package com.example.s510607.finalproject;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by darionhiggins on 4/19/16.
 */
public class CreditCard extends GenericJson {
    @Key
    String cardNumber="";
    @Key
    int expMonth=0;
    @Key
    int expYear=0;
    @Key
    int securityCode=0;

    public CreditCard(){}

    public CreditCard(String cardNumber, int expMonth, int expYear, int securityCode) {
        this.cardNumber = cardNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.securityCode = securityCode;
    }
}
