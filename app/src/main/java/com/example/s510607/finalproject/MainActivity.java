package com.example.s510607.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CartFragment.CartListener, StoreManagementCategoriesFragment.StoreManagementCategoriesListener,
        StoreManagementItemsFragment.StoreManagementItemsListener {

    Map<Item, Integer> cart;
    ArrayList<String> stores;
    Client client;
    Store currentStore;
    boolean unsavedChanges;

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

        client.user().logout().execute();
        unsavedChanges = false;
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
        String username = usernameET.getText().toString();
        usernameET.setText("");
        EditText passwordET = (EditText) findViewById(R.id.loginPasswordET);
        String password = passwordET.getText().toString();
        passwordET.setText("");

        client.user().login(username, password, new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable t) {
                if (client.user().isUserLoggedIn()) {
                    client.user().logout().execute();
                    login(null);
                }else{
                    CharSequence text = "Wrong username or password, or somebody's already logged in.";
                    Log.d("TAGGER", t.toString());
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(User u) {
                CharSequence text = "Welcome back," + u.getUsername() + ".";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                if (u.get("UserType").equals("store")) {
                    Query myQuery = client.query();
                    myQuery.equals("name", u.get("store_name"));
                    final AsyncAppData<Store> storeData = client.appData("stores", Store.class);
                    storeData.get(myQuery, new KinveyListCallback<Store>() {
                        @Override
                        public void onSuccess(Store[] stores) {
                            Log.v("TAG", "received " + stores.length + " stores");

                            currentStore = stores[0];
                            StoreManagementCategoriesFragment storeManagementCategoriesFragment = new StoreManagementCategoriesFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("store_name", currentStore.getName());
                            storeManagementCategoriesFragment.setArguments(bundle);
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment_container, storeManagementCategoriesFragment);
                            transaction.commit();
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e("TAG", "failed to fetchByFilterCriteria", error);
                        }
                    });
                } else {
                    Query myQuery = client.query();
                    final AsyncAppData<Store> storeData = client.appData("stores", Store.class);
                    storeData.get(myQuery, new KinveyListCallback<Store>() {
                        @Override
                        public void onSuccess(Store[] stores) {
                            Log.v("TAG", "received " + stores.length + " stores");

                            StoresFragment storesFragment = new StoresFragment();
                            storesFragment.setStores(stores);

                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment_container, storesFragment);
                            transaction.commit();
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e("TAG", "failed to fetchByFilterCriteria", error);
                            Toast.makeText(getApplicationContext(), "Unable to get stores, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });
    }

    public void logout(View v){
        if(unsavedChanges){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Are you sure?")
                    .setView(R.layout.leave_dialog)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            client.user().logout().execute();
                            LoginFragment loginFragment = new LoginFragment();
                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragment_container, loginFragment);
                            transaction.commit();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            builder.show();
        }else{
            client.user().logout().execute();
            LoginFragment loginFragment = new LoginFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, loginFragment);
            transaction.commit();
        }

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
                if (client.user().isUserLoggedIn()) {
                    client.user().logout().execute();
                    register(null);
                } else {
                    CharSequence text = "Could not sign up.";
                    Log.d("TAGGER", t.toString());
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
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

                if (userTypeStr.equals("store")) {
                    createStore(storeName.getText().toString());
                } else {
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
    public ArrayList<Item> getItems(String category) {
        return currentStore.getCategory(category).getItems();
    }

    @Override
    public ArrayList<String> getCategories(){
        return currentStore.getAllCategories();
    }

    @Override
    public void renameCategory(String oldName, String newName) {
        if(currentStore.renameCategory(oldName, newName)==-1){
            Toast.makeText(getApplicationContext(), "Category: "+newName+" already exists.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Change successful!", Toast.LENGTH_SHORT).show();
            unsavedChanges = true;
        }
    }

    @Override
    public void deleteCategory(String category) {
        currentStore.removeCategory(currentStore.getCategory(category));
        unsavedChanges = true;
    }

    public void updateStore(View v){
        AsyncAppData<Store> storeData = client.appData("stores", Store.class);
        storeData.save(currentStore, new KinveyClientCallback<Store>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "failed to save event data", e);
            }

            @Override
            public void onSuccess(Store r) {
                Log.d("TAG", "saved data for entity " + r.getName());
                unsavedChanges = false;
            }
        });
    }

    public void leaveCategory(View v){
        StoreManagementCategoriesFragment storeManagementCategoriesFragment = new StoreManagementCategoriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("store_name", currentStore.getName());
        storeManagementCategoriesFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, storeManagementCategoriesFragment);
        transaction.commit();
    }

    public void addCategory(String name) {
        Category cat = new Category(name);
        if(currentStore.addCategory(cat)){
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            StoreManagementCategoriesFragment fragment = (StoreManagementCategoriesFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            fragment.updateListView(currentStore.getAllCategories());
            unsavedChanges = true;
        }else{
            Toast.makeText(getApplicationContext(), "Category already exists", Toast.LENGTH_SHORT).show();
        }
    }

    public void addItem(Item item, String category){
        boolean success = currentStore.getCategory(category).addItem(item);
        if(success){
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            StoreManagementItemsFragment fragment = (StoreManagementItemsFragment) fragmentManager.findFragmentById(R.id.fragment_container);
            fragment.updateListView(currentStore.getCategory(category).getItems());
            unsavedChanges = true;
        }else{
            Toast.makeText(getApplicationContext(),"Item already exists", Toast.LENGTH_SHORT).show();
        }
    }

    public void openAddCategoryDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater li = LayoutInflater.from(this);
        final View dialogView = li.inflate(R.layout.add_category_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Add Category");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText categoryET = (EditText) dialogView.findViewById(R.id.categoryET);
                addCategory(categoryET.getText().toString());
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
    }

    public void openAddItemDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater li = LayoutInflater.from(this);
        final View dialogView = li.inflate(R.layout.add_item_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Add Item");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText itemNameET = (EditText) dialogView.findViewById(R.id.itemNameET);
                String name = itemNameET.getText().toString();

                EditText itemDescET = (EditText) dialogView.findViewById(R.id.itemDescET);
                String desc = itemDescET.getText().toString();

                EditText itemPriceET = (EditText) dialogView.findViewById(R.id.itemPriceET);
                double price = Double.parseDouble(itemPriceET.getText().toString());

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                StoreManagementItemsFragment fragment = (StoreManagementItemsFragment) fragmentManager.findFragmentById(R.id.fragment_container);

                addItem(new Item(name, desc, price), fragment.category);
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
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        client.user().logout().execute();
    }

    @Override
    public void updateItem(Item item, String category) {
        currentStore.getCategory(category).updateItem(item);
        Toast.makeText(getApplicationContext(), "Change successful", Toast.LENGTH_SHORT).show();
        unsavedChanges = true;
    }

    @Override
    public void renameItem(String oldName, String newName, String category) {
        if(currentStore.getCategory(category).renameItem(oldName, newName)==-1){
            Toast.makeText(getApplicationContext(), "Item name: "+newName+" already exists.", Toast.LENGTH_SHORT).show();
        }else{
            unsavedChanges = true;
        }
    }

    @Override
    public void deleteItem(Item item, String category) {
        unsavedChanges = true;
    }
}
