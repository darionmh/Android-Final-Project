package com.example.s510607.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import layout.SplashsScreenFragment;

public class MainActivity extends AppCompatActivity implements CartFragment.CartListener, StoreManagementCategoriesFragment.StoreManagementCategoriesListener,
        StoreManagementItemsFragment.StoreManagementItemsListener,ItemsFragment.ItemsReceiver,PurchaseFragment.purchaseCommunicator,
        StoresFragment.StoreFragmentListener, ViewOrdersFragment.ViewOrdersListener{

    HashMap<Item, Integer> cart;
    ArrayList<String> stores;
    ArrayList<Purchase> purchases;
    Client client;
    Store currentStore;
    boolean unsavedChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cart = new HashMap<Item, Integer>();
        stores = new ArrayList<>();

        SplashsScreenFragment splashScreen = new SplashsScreenFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragment_container, splashScreen);
        transaction.commit();

        client = new Client.Builder(this.getApplicationContext()).build();
        client.user().logout().execute();
        unsavedChanges = false;

        client.ping(new KinveyPingCallback() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                int currentSeconds = Calendar.getInstance().get(Calendar.SECOND);
                int endTime = (currentSeconds+3)%60;
                while(Calendar.getInstance().get(Calendar.SECOND)!=endTime){}
                showLogin(null);
                client.user().logout().execute();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Unable to make connecton.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLogin(View v){
        new Handler().post(new Runnable() {
            public void run() {
                LoginFragment loginFragment = new LoginFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.commitAllowingStateLoss();
                //transaction.commit();
            }
        });
    }

    @Override
    public Map<Item, Integer> getCart(){
        return cart;
    }

    public void cartCancelPress(View view){
        backToCategories(null);
    }

    public void cartCheckOutPress(View view){
        PurchaseFragment purchaseFragment = new PurchaseFragment();
        double totalCost = 0;
        for(Item i:cart.keySet()){
            totalCost += i.getPrice()*cart.get(i);
        }
        purchaseFragment.setTotalCost(totalCost);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, purchaseFragment);
        transaction.commit();
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
                } else {
                    CharSequence text = "Wrong username or password, or somebody's already logged in.";
                    Log.d("TAGGER", t.toString());
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(User u) {
                CharSequence text = "Welcome back, " + u.getUsername() + ".";
                client.enableDebugLogging();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                try {
                    client.push().initialize(getApplication());
                } catch (Exception e) {
                    Log.d("Err", e.getMessage());
                }
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

    public void customerLeaveStore(View v){
        if(!cart.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("You have items in your cart!");
            builder.setPositiveButton("Checkout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showCart(null);
                }
            });
            builder.setNegativeButton("Empty and Leave", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cart.clear();
                    reloadStore();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else{
            reloadStore();
        }
    }

    public void showCart(View v){
        CartFragment cartFragment = new CartFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment);
        transaction.commit();
    }

    public void reloadStore(){
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

    public void backToCategories(View v){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setStore(currentStore);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,categoriesFragment );
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

    @Override
    public void ItemSend(Item item, int q) {
        cart.put(item, q);
    }

    @Override
    public void purchaseSender(CreditCard creditCard, String address) {
        String order = "";
        for(Item i:cart.keySet()){
            order += String.format("%s: %d\n", i.getName(), cart.get(i));
        }
        order = address + "\n" + order;

        //Purchase p = new Purchase(currentStore.getName(), (String) client.user().get("first_name"), creditCard, creditCard.cardNumber == 0);
        GenericJson g = new GenericJson();
        g.put("store_name", currentStore.getName());
        g.put("customer_name", (String)client.user().get("first_name"));
        g.put("card", creditCard);
        g.put("order", order);
        AsyncAppData<GenericJson> purchaseData = client.appData("orders", GenericJson.class);
        purchaseData.save(g, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onFailure(Throwable e) {
                Log.d("Oops", e.toString());
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(GenericJson r) {
                Log.d("Oops", "yay");
                Toast.makeText(getApplicationContext(), "Order submitted", Toast.LENGTH_SHORT).show();
            }
        });

        reloadStore();
    }

    @Override
    public void updateStore(Store store) {
        currentStore = store;
    }

    @Override
    public void getOrders() {
        Query q = new Query();
        q.equals("store_name", currentStore.getName());
        final boolean[] finish = new boolean[1];
        finish[0] = false;
        AsyncAppData<GenericJson> orderData = client.appData("orders", GenericJson.class);
        orderData.get(q, new KinveyListCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson[] genericJsons) {
                Toast.makeText(getApplicationContext(), "Found "+genericJsons.length+" orders!", Toast.LENGTH_SHORT).show();
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<String> orders = new ArrayList<String>();
                for(GenericJson g:genericJsons){
                    String customerName = (String)g.get("customer_name");
                    String order = (String)g.get("order");
                    names.add(customerName);
                    orders.add(order);
                }
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                ViewOrdersFragment fragment = (ViewOrdersFragment) fragmentManager.findFragmentById(R.id.fragment_container);
                fragment.setOrders(orders);
                fragment.setNames(names);
                fragment.buildLV();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Could not get orders", Toast.LENGTH_SHORT).show();
                Log.d("Oops", throwable.toString());
            }
        });
    }

    @Override
    public void deleteOrder(String customerName, String order) {
        Query q = new Query();
        Log.d("oops", customerName+" "+order);
        q.equals("customer_name", customerName);
        q.equals("order", order);
        AsyncAppData<GenericJson> orderData = client.appData("orders", GenericJson.class);
        orderData.delete(q, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                Log.d("oops", "Deleted "+kinveyDeleteResponse.getCount());
                Toast.makeText(MainActivity.this, "Order deleted!", Toast.LENGTH_SHORT).show();
                getOrders();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Order couldn't be deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showOrders(View v){
        ViewOrdersFragment viewOrdersFragment = new ViewOrdersFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,viewOrdersFragment);
        transaction.commit();
        getOrders();
    }

    public void storeBackToCategories(View v){
        StoreManagementCategoriesFragment storeManagementCategoriesFragment = new StoreManagementCategoriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("store_name", currentStore.getName());
        storeManagementCategoriesFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, storeManagementCategoriesFragment);
        transaction.commit();
    }
}
