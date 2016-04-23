package com.example.s510607.finalproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewOrdersFragment extends Fragment {

    ArrayList<String> names;
    ArrayList<String> orders;
    View view;

    public ViewOrdersFragment() {
        // Required empty public constructor
    }

    ViewOrdersListener viewOrdersListener;
    public interface ViewOrdersListener{
        void getOrders();
        void deleteOrder(String customerName, String order);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        viewOrdersListener = (ViewOrdersListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_orders, container, false);
        Button updateBTN = (Button) view.findViewById(R.id.updateBTN);
        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOrdersListener.getOrders();
            }
        });

        if(orders==null) orders = new ArrayList<>();
        if(names==null) names = new ArrayList<>();

        buildLV();
        return view;
    }

    //Method to build listview so it can be update easily
    public void buildLV(){
        Log.d("oops", "building LV");

        //Only builds if the view is not null, onCreateView has been called
        if(view == null)return;
        ListView orderLV = (ListView) view.findViewById(R.id.orderLV);
        ViewOrdersAA viewOrdersAA = new ViewOrdersAA(getContext(), R.layout.order_list_item, R.id.customerNameTV, names, orders);
        orderLV.setAdapter(viewOrdersAA);
        orderLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //When you click an order, it asks if you want to delete it
                final int orderClick = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete order?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if yes, the listener deletes it from kinvey
                                viewOrdersListener.deleteOrder(names.get(orderClick), orders.get(orderClick));
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();
            }
        });
    }

    //Method called from main activity to set the order list
    public void setOrders(ArrayList<String> orders){
        this.orders = orders;
    }

    //Method called from main activity to set the name list
    public void setNames(ArrayList<String> names){
        this.names = names;
    }

}

class ViewOrdersAA extends ArrayAdapter<String>{

    ArrayList<String> orders;
    public ViewOrdersAA(Context context, int resource, int textViewResourceId, ArrayList<String> names, ArrayList<String> orders) {
        super(context, resource, textViewResourceId, names);
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d("oops", "on lv "+position);
        View view = super.getView(position, convertView, parent);

        String customerName = getItem(position);
        String currentOrder = orders.get(position);

        TextView customerOrderTV = (TextView) view.findViewById(R.id.customerOrderTV);
        TextView customerNameTV = (TextView) view.findViewById(R.id.customerNameTV);

        customerOrderTV.setText(currentOrder);
        customerNameTV.setText(customerName);

        return view;
    }
}


