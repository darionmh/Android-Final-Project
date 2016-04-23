package com.example.s510607.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import com.kinvey.android.callback.KinveyUserDeleteCallback;
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
    Client client;
    Store currentStore;
    boolean unsavedChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The cart and store list is instantiated
        cart = new HashMap<Item, Integer>();
        stores = new ArrayList<>();

        //When the app loads, a splash screen is displayed
        //This gives the app time to load the kinvey connection
        SplashsScreenFragment splashScreen = new SplashsScreenFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragment_container, splashScreen);
        transaction.commit();

        //A connecion is created
        client = new Client.Builder(this.getApplicationContext()).build();
        client.user().logout().execute();

        //Unsavedchanges keeps track of whether or not the store has saved their changes to the store
        unsavedChanges = false;

        //The connection is pinged to see if it is ready
        client.ping(new KinveyPingCallback() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                //On success, the splashscreen will display for 3 more seconds, and then the login screen is shown.
                int currentSeconds = Calendar.getInstance().get(Calendar.SECOND);
                int endTime = (currentSeconds + 3) % 60;
                while (Calendar.getInstance().get(Calendar.SECOND) != endTime) {
                }
                showLogin(null);

                //The app makes sure that there is no user logged in
                client.user().logout().execute();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Unable to make connecton.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*------This section of methods is used to login, logout, and create accounts----------*/

    //Method called from the LoginFragment when login is pressed
    public void login(View view){
        EditText usernameET = (EditText) findViewById(R.id.loginUsernameET);
        String username = usernameET.getText().toString();
        usernameET.setText("");
        EditText passwordET = (EditText) findViewById(R.id.loginPasswordET);
        String password = passwordET.getText().toString();
        passwordET.setText("");

        //The client attempts to login using the provided username and password
        client.user().login(username, password, new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable t) {
                //If it failed, it makes sure that no one was still logged in
                if (client.user().isUserLoggedIn()) {
                    //If someone is still logged in, it will log them out and try again
                    client.user().logout().execute();
                    login(null);
                } else {
                    CharSequence text = "Wrong username or password";
                    Log.d("TAGGER", t.toString());
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(User u) {
                //On successful login, the user is greeted
                CharSequence text = "Welcome back, " + u.getUsername() + ".";
                client.enableDebugLogging();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                //Push notifications are attempted to be enabled
                try {
                    client.push().initialize(getApplication());
                } catch (Exception e) {
                    Log.d("Err", e.getMessage());
                }

                //Checks the user type
                if (u.get("UserType").equals("store")) {
                    //If they are a store, all of their information is retrieved from kinvey and they are sent to the StoreManagementCategoriesFragment
                    Query myQuery = client.query();
                    myQuery.equals("name", u.get("store_name"));
                    final AsyncAppData<Store> storeData = client.appData("stores", Store.class);
                    storeData.get(myQuery, new KinveyListCallback<Store>() {
                        @Override
                        public void onSuccess(Store[] stores) {
                            Log.v("TAG", "received " + stores.length + " stores");

                            //Since only 1 store should match the name, only the first one is used
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
                    //If they are a customer, all of the stores are retrieved and they are sent to the StoresFragment
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

    //Method is called whenever a user presses a logout button
    public void logout(View v){

        if(unsavedChanges){
            //If there are still unsaved changes, they are prompted to either go back and save or discard them and log out
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Are you sure?")
                    .setView(R.layout.leave_dialog)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //If yes, they are just logged out and sent back to the loginfragment
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
            //If no unsaved changes, they are logged out without prompt and sent back to the loginfragment
            client.user().logout().execute();
            LoginFragment loginFragment = new LoginFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, loginFragment);
            transaction.commit();
        }

    }

    //Method called from the RegisterFragment if register is pressed
    //It begins to create their account and make sure all fields are completed
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

        //If all fields are complete, an account is attempted to be created
        client.user().create(username.getText().toString(), password.getText().toString(), new KinveyUserCallback() {
            @Override
            public void onFailure(Throwable t) {
                if (client.user().isUserLoggedIn()) {
                    //If it failed because someone is still logged in, they are logged out and it tries again
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
                //If it was successful, they go to the finishUserCreation method where the rest of the information is added
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

        //The type of account is added
        client.user().put("UserType", userTypeStr);
        if(userTypeStr.equals("customer")){
            //If they are a customer, the first and last name fields are used and filled
            client.user().put("first_name", firstName.getText().toString());
            client.user().put("last_name", lastName.getText().toString());
        }else{
            //If they are a store, the store name field is used and filled
            client.user().put("store_name", storeName.getText().toString());
        }

        //The account is attempted to be update on kinvey
        client.user().update(new KinveyUserCallback() {
            @Override
            public void onSuccess(User user) {
                //If it is successful, they are told so
                CharSequence text = user.getUsername() + ", your account has been created.";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                if (userTypeStr.equals("store")) {
                    //If they are a store, a store is created with the inputted name
                    createStore(storeName.getText().toString());
                } else {
                    //Otherwise they are just logged out so they can login with the new information
                    client.user().logout().execute();
                }

                //The LoginFragment is shown
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

    //Method called from finishUserCreation if the user is a store
    public void createStore(String storeName){
        //A new store object is created with their name and is sent to kinvey
        Store store = new Store(storeName);

        AsyncAppData<Store> storeData = client.appData("stores", Store.class);
        storeData.save(store, new KinveyClientCallback<Store>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e("TAG", "failed to save event data", e);
                Toast.makeText(getApplicationContext(), "Could not create store", Toast.LENGTH_SHORT).show();
                client.user().logout().execute();
            }

            @Override
            public void onSuccess(Store r) {
                Log.d("TAG", "saved data for entity " + r.getName());
                Toast.makeText(getApplicationContext(), "Store created!", Toast.LENGTH_SHORT).show();
                client.user().logout().execute();
            }
        });
    }
    /*******************************************************************************************/


    /*------This section of methods is used to navigate between fragments----------*/

    /*Customer-side methods*/
    //Method called after the kinvey connection is completed to show the login screen
    public void showLogin(View v){
        new Handler().post(new Runnable() {
            public void run() {
                LoginFragment loginFragment = new LoginFragment();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.fragment_container, loginFragment);
                transaction.commitAllowingStateLoss();
            }
        });
    }

    //Shows the register fragment
    public void showRegisterFragment(View view){
        RegisterFragment registerFragment = new RegisterFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, registerFragment);
        transaction.commit();
    }

    //Shows the cart fragment
    public void showCart(View v){
        CartFragment cartFragment = new CartFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, cartFragment);
        transaction.commit();
    }

    //Sets the current store and shows the categories fragment
    public void showCategories(View v){
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setStore(currentStore);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, categoriesFragment);
        transaction.commit();
    }

    //Method is called when a customer attempts to leave a store
    public void customerLeaveStore(View v){

        //Makes sure the cart is empty before allowing them to leave
        if(!cart.isEmpty()){

            //If it isn't empty, it asks them if they want to check out or clear their cart
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

    //Method called when populating the store fragment and displaying said fragment
    public void reloadStore(){

        //Creates a query to search for all stores
        Query myQuery = client.query();
        final AsyncAppData<Store> storeData = client.appData("stores", Store.class);
        storeData.get(myQuery, new KinveyListCallback<Store>() {
            @Override
            public void onSuccess(Store[] stores) {
                Log.v("TAG", "received " + stores.length + " stores");

                //Creates the fragment and passes in the list
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

    //Called when the user presses checkout in the cart fragment, sends them to the purchase fragment
    public void cartCheckOutPress(View view){
        PurchaseFragment purchaseFragment = new PurchaseFragment();

        //Passes in the total cost of the order to the purchase fragment
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

    //Shows the ViewOrders fragment, displays the customers cart
    public void showOrders(View v){
        ViewOrdersFragment viewOrdersFragment = new ViewOrdersFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, viewOrdersFragment);
        transaction.commit();
        getOrders();
    }

    /*Store-side methods*/
    //Displays the stores current categories
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
    /*******************************************************************************************/

    /*------This section of methods is used by Listeners----------*/

    //Get cart is part of the CartFragment Listener to get the current cart from MainActivity
    @Override
    public Map<Item, Integer> getCart(){
        return cart;
    }

    //Part of the ItemFragment Listener, adds an item to the cart
    @Override
    public void ItemSend(Item item, int q) {
        cart.put(item, q);
    }

    //Part of the StoreFragment Listener, updates the currentStore when a new one is selected
    @Override
    public void updateStore(Store store) {
        currentStore = store;
    }

    //Part of the ViewOrdersFragment Listener
    //Pulls all of the currentStore's orders from kinvey
    @Override
    public void getOrders() {

        //Query searches for all stores with store_name matching the currentStore's name
        Query q = new Query();
        q.equals("store_name", currentStore.getName());
        AsyncAppData<GenericJson> orderData = client.appData("orders", GenericJson.class);
        orderData.get(q, new KinveyListCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson[] genericJsons) {
                //If it is successful, it creates two arraylists, one for customer names and one for their orders
                Toast.makeText(getApplicationContext(), "Found " + genericJsons.length + " orders!", Toast.LENGTH_SHORT).show();
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<String> orders = new ArrayList<String>();
                for (GenericJson g : genericJsons) {
                    String customerName = (String) g.get("customer_name");
                    String order = (String) g.get("order");
                    names.add(customerName);
                    orders.add(order);
                }

                //Passes this info to the vieworders fragment
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

    //Part of the ViewOrdersFragment Listener
    //Deletes an order from kinvey
    @Override
    public void deleteOrder(String customerName, String order) {

        //Searches for all orders with customer_name and order matching those of the one to delete
        Query q = new Query();
        q.equals("customer_name", customerName);
        q.equals("order", order);
        AsyncAppData<GenericJson> orderData = client.appData("orders", GenericJson.class);
        orderData.delete(q, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                //If successful, it reloads the vieworders fragment by calling getOrders
                Log.d("oops", "Deleted " + kinveyDeleteResponse.getCount());
                Toast.makeText(MainActivity.this, "Order deleted!", Toast.LENGTH_SHORT).show();
                getOrders();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Order couldn't be deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Part of the PurchaseFragment Listener
    //Gets the information from the purchase fragment and passes it to kinvey
    //Kinvey will send a push notification to the store that the customer ordered from
    @Override
    public void purchaseSender(CreditCard creditCard, String address) {
        //Formats the order into a string
        String order = "";
        for(Item i:cart.keySet()){
            order += String.format("%s: %d\n", i.getName(), cart.get(i));
        }
        order = address + "\n" + order;

        //Creates a GenericJson object and passes all the information into it
        GenericJson g = new GenericJson();
        g.put("store_name", currentStore.getName());
        g.put("customer_name", (String)client.user().get("first_name"));
        g.put("card", creditCard);
        g.put("order", order);

        //Order is then sent into kinvey
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
                //Business logic on the orders data will sent a push notification to the appropriate store
            }
        });

        //Cart is cleared and the customer is sent back the the stores fragment
        cart.clear();
        reloadStore();
    }

    //Part of the StoreManagementCategoriesFragment Listener
    //Gets all of the categories of the currentstore and sents them to the fragment
    @Override
    public ArrayList<String> getCategories(){
        return currentStore.getAllCategories();
    }

    //Part of the StoreManagementCategoriesFragment Listener
    //Gets the old and new name from the fragment and trys to rename it using the stores method
    @Override
    public void renameCategory(String oldName, String newName) {
        if(currentStore.renameCategory(oldName, newName)==-1){
            Toast.makeText(getApplicationContext(), "Category: "+newName+" already exists.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Change successful!", Toast.LENGTH_SHORT).show();
            unsavedChanges = true;
        }
    }

    //Part of the StoreManagementCategoriesFragment Listener
    //Gets the category from the fragment and deletes it from the store
    @Override
    public void deleteCategory(String category) {
        currentStore.removeCategory(currentStore.getCategory(category));
        unsavedChanges = true;
    }

    //Part of the StoreManagementItemsFragment Listener
    //Gets all of the items of the category sent by the fragment and returns them to the fragment
    @Override
    public ArrayList<Item> getItems(String category) {
        return currentStore.getCategory(category).getItems();
    }

    //Part of the StoreManagementItemsFragment Listener
    //Gets the updated item and category from the fragment and updates using the categories method
    @Override
    public void updateItem(Item item, String category) {
        currentStore.getCategory(category).updateItem(item);
        Toast.makeText(getApplicationContext(), "Change successful", Toast.LENGTH_SHORT).show();
        unsavedChanges = true;
    }

    //Part of the StoreManagementItemsFragment Listener
    //Gets the old and new name of the item and the category from the fragment and attempts to rename item using the categorys method
    @Override
    public void renameItem(String oldName, String newName, String category) {
        if(currentStore.getCategory(category).renameItem(oldName, newName)==-1){
            Toast.makeText(getApplicationContext(), "Item name: "+newName+" already exists.", Toast.LENGTH_SHORT).show();
        }else{
            unsavedChanges = true;
        }
    }

    //Part of the StoreManagementItemsFragment Listener
    //Gets the item and category from the fragment and deletes it using the category's method
    @Override
    public void deleteItem(Item item, String category) {
        currentStore.getCategory(category).removeItem(item);
        unsavedChanges = true;
    }

    /*******************************************************************************************/


    //Method used by the store to add new items to a category
    //Method is actually called by the dialog below
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

    //Method called from the StoreManagementItemsFragment when the FloatingActionButton is pressed
    //Opens a dialog to input information for a new item
    public void openAddItemDialog(View v) {
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
                String price = itemPriceET.getText().toString();

                //Gets a reference to the current fragment so that the current category can be retrieved
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                StoreManagementItemsFragment fragment = (StoreManagementItemsFragment) fragmentManager.findFragmentById(R.id.fragment_container);

                boolean nameError = false;
                boolean descError = false;
                boolean priceError = false;
                if(name.equals("")){
                    nameError = true;
                }
                if(desc.equals("")){
                    descError = true;
                }
                if(price.equals("")){
                    priceError = true;
                }
                if(nameError||descError||priceError){
                    openAddItemDialog(null,nameError,descError,priceError,name,desc,price);
                }else{
                    addItem(new Item(name, desc, Double.parseDouble(price)), fragment.category);
                }
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

    //Opens a new dialog with hints in red if they were not previously filled
    public void openAddItemDialog(Object... objects) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater li = LayoutInflater.from(this);
        final View dialogView = li.inflate(R.layout.add_item_dialog, null);
        if(objects.length == 7){
            EditText itemNameET = (EditText) dialogView.findViewById(R.id.itemNameET);
            if((boolean)objects[1])itemNameET.setHintTextColor(Color.RED);
            itemNameET.setText((String)objects[4]);

            EditText itemDescET = (EditText) dialogView.findViewById(R.id.itemDescET);
            if((boolean)objects[2])itemDescET.setHintTextColor(Color.RED);
            itemDescET.setText((String)objects[5]);

            EditText itemPriceET = (EditText) dialogView.findViewById(R.id.itemPriceET);
            if((boolean)objects[3])itemPriceET.setHintTextColor(Color.RED);
            itemPriceET.setText((String)objects[6]);
        }
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
                String price = itemPriceET.getText().toString();

                //Gets a reference to the current fragment so that the current category can be retrieved
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                StoreManagementItemsFragment fragment = (StoreManagementItemsFragment) fragmentManager.findFragmentById(R.id.fragment_container);

                boolean nameError = false;
                boolean descError = false;
                boolean priceError = false;
                if(name.equals("")){
                    nameError = true;
                }
                if(desc.equals("")){
                    descError = true;
                }
                if(price.equals("")){
                    priceError = true;
                }
                if(nameError||descError||priceError){
                    openAddItemDialog(null,nameError,descError,priceError,name,desc,price);
                }else{
                    addItem(new Item(name, desc, Double.parseDouble(price)), fragment.category);
                }
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

    //Method used by the store to add a new category
    //Method is actually called by the dialog below
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

    //Method called from the StoreManagementCategoriesFragment when the FloatingActionButton is pressed
    //Opens a dialog to input information for a new category
    public void openAddCategoryDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater li = LayoutInflater.from(this);
        final View dialogView = li.inflate(R.layout.add_category_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Add Category");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText categoryET = (EditText) dialogView.findViewById(R.id.categoryET);
                String categoryName = categoryET.getText().toString();
                if (categoryName.equals("")) {
                    openAddCategoryDialog(null, true);
                } else {
                    addCategory(categoryName);
                }
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

    //Opens a new dialog with hints in red if they were not previously filled
    public void openAddCategoryDialog(Object... obj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater li = LayoutInflater.from(this);
        final View dialogView = li.inflate(R.layout.add_category_dialog, null);
        if(obj.length == 2){
            EditText categoryET = (EditText) dialogView.findViewById(R.id.categoryET);
            categoryET.setHintTextColor(Color.RED);
        }
        builder.setView(dialogView);
        builder.setTitle("Add Category");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText categoryET = (EditText) dialogView.findViewById(R.id.categoryET);
                String categoryName = categoryET.getText().toString();
                if (categoryName.equals("")) {
                    openAddCategoryDialog(null, true);
                } else {
                    addCategory(categoryName);
                }
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

    //Logs the user out when the application is destoryed
    @Override
    public void onDestroy(){
        super.onDestroy();
        client.user().logout().execute();
    }

    //Method called from either StoreManagementCategoriesFragment or StoreManagementItemsFragment to save updated information to kinvey
    //Whenever a category or item is added/changed/deleted, this method needs called before kinvey will update
    public void saveStore(View v){
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
                Toast.makeText(getApplicationContext(), "Store saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method called from StoreManagementCategoriesFragment when the Delete button is pressed
    //If a store is done using the app, this will remove their store from the stores data as well as their account
    public void deleteStore(View v){
        //Store is given an alert dialog to make sure they truly want to delete the store
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Are you sure?")
                .setMessage("Your account, the store, and everything inside will be lost...")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If yes, a store with the same name will be found from kinvey and deleted
                        Query q = new Query();
                        q.equals("name", currentStore.getName());
                        AsyncAppData<Store> storeData = client.appData("stores", Store.class);
                        storeData.delete(q, new KinveyDeleteCallback() {
                            @Override
                            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                                client.user().delete(true, new KinveyUserDeleteCallback() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Store successfully deleted.", Toast.LENGTH_SHORT).show();

                                        //User is sent back to the login screen
                                        showLogin(null);
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                        Log.d("oops", throwable.toString());
                                        Toast.makeText(getApplicationContext(), "Could not delete account..", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.d("oops", throwable.toString());
                                Toast.makeText(getApplicationContext(), "Could not delete store..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }
}
