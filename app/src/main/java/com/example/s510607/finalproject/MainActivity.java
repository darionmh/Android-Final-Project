package com.example.s510607.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CartFragment.CartListener, StoreManagementCategoriesFragment.StoreManagementCategoriesListener{

    Map<Item, Integer> cart;
    ArrayList<String> stores;
    Client client;
    Store currentStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cart = new HashMap<Item, Integer>();
        stores = new ArrayList<>();

        LoginFragment loginFragment = new LoginFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, loginFragment);
        transaction.commit();

        client = new Client.Builder("kid_-157iCrY1Z", "dd0301a74f0b40da811ba1a8b340aa09", this.getApplicationContext()).build();
        while(client.user().isUserLoggedIn()){
            client.user().logout().execute();
        }
    }

    public Map<Item, Integer> getCart(){
        return cart;
    }

    public void cartCancelPress(View view){

    }

    public void cartCheckOutPress(View view){

    }

    public void login(View view){
        EditText usernameET = (EditText) findViewById(R.id.loginUsernameET);
        EditText passwordET = (EditText) findViewById(R.id.loginPasswordET);

        client.user().login(usernameET.getText().toString(), passwordET.getText().toString(), new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable t) {
                CharSequence text = "Wrong username or password, or somebody's already logged in.";
                Log.d("TAGGER", t.toString());
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(User u) {
                CharSequence text = "Welcome back," + u.getUsername() + ".";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                if(u.get("UserType").equals("store")){
                    Query myQuery = client.query();
                    myQuery.equals("name", u.get("store_name"));
                    AsyncAppData<Store> storeData = client.appData("stores", Store.class);
                    storeData.get(myQuery, new KinveyListCallback<Store>() {
                        @Override
                        public void onSuccess(Store[] stores) {
                            Log.v("TAG", "received "+ stores.length + " stores");

                            currentStore = stores[0];
                            StoreManagementItemsFragment storeManagementItemsFragment = new StoreManagementItemsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("categories", currentStore.getAllCategories());
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment_container, storeManagementItemsFragment);
                            transaction.commit();
                        }
                        @Override
                        public void onFailure(Throwable error) {
                            Log.e("TAG", "failed to fetchByFilterCriteria", error);
                        }
                    });
                }else{
                    //Need to get stores somehow
                    //Figure out how they will be stored
                    stores.add("Mex");
                    stores.add("Zen");
                    stores.add("Chick");

                    StoresFragment storesFragment = new StoresFragment();

                    Bundle args = new Bundle();
                    args.putStringArrayList("stores", stores);
                    storesFragment.setArguments(args);

                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, storesFragment);
                    transaction.commit();
                }


            }
        });
    }

    public void showRegisterFragment(View view){
        RegisterFragment registerFragment = new RegisterFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, registerFragment);
        transaction.commit();
    }

    public void register(View view){
        EditText username = (EditText) findViewById(R.id.registerUsernameET);
        EditText password = (EditText) findViewById(R.id.registerPasswordET);
        EditText passwordConfirm = (EditText) findViewById(R.id.registerPasswordConfirmET);

        if(!password.getText().toString().equals(passwordConfirm.getText().toString())){
            Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(username.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Must enter a username.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Must enter a password.", Toast.LENGTH_SHORT).show();
            return;
        }

        client.user().create(username.getText().toString(), password.getText().toString(), new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable t) {
                CharSequence text = "Could not sign up.";
                Log.d("TAGGER", t.toString());
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(User u) {
                finishUserCreation();
            }
        });

    }

    public void finishUserCreation(){
        final EditText storeName = (EditText) findViewById(R.id.storeNameET);
        EditText firstName = (EditText) findViewById(R.id.firstNameET);
        EditText lastName = (EditText) findViewById(R.id.lastNameET);
        RadioGroup userType = (RadioGroup) findViewById(R.id.userTypeRBG);
        final String userTypeStr = (userType.getCheckedRadioButtonId()==R.id.customerRB)?"customer":"store";
        client.user().put("UserType", userTypeStr);
        if(userTypeStr.equals("customer")){
            client.user().put("first_name",firstName.getText().toString());
            client.user().put("last_name",lastName.getText().toString());
        }else{
            client.user().put("store_name", storeName.getText().toString());
        }

        client.user().update(new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                CharSequence text = user.getUsername() + ", your account has been created.";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                if(userTypeStr.equals("store")){
                    createStore(storeName.getText().toString());
                }else{
                    client.user().logout().execute(); // once we register, let's log out, so we have to log in explicitly.
                }

                LoginFragment loginFragment = new LoginFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.commit();
            }

            @Override
            public void onFailure(Throwable t) {
                CharSequence text = "Could not sign up.";
                Log.d("TAGGER", t.toString());

                client.user().logout().execute(); // once we register, let's log out, so we have to log in explicitly.
            }
        });


    }

    public void createStore(String storeName){
        Store store = new Store(storeName);
        AsyncAppData<Store> storeData = client.appData("stores", Store.class);
        storeData.save(store, new KinveyClientCallback<Store>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "failed to save event data", e);
                client.user().logout().execute(); // once we register, let's log out, so we have to log in explicitly.
            }

            @Override
            public void onSuccess(Store r) {
                Log.d("TAG", "saved data for entity " + r.getName());
                client.user().logout().execute(); // once we register, let's log out, so we have to log in explicitly.
            }
        });
    }

    @Override
    public ArrayList<String> getItems(String category) {
        return null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        client.user().logout().execute();
    }
}
