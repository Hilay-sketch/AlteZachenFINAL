package com.example.altezachen3;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GridView extends BasicActivity implements NavigationBarView.OnItemSelectedListener, SearchView.OnQueryTextListener, View.OnClickListener {
    FirebaseFirestore db;//הממסד נתונים רפרנס
    android.widget.GridView GVAllItems;//גריד וויו ויזאול
    ArrayList<Item> alAllItems;//רשימה של כל הפריטים הנכנסים
    ItemsGVAdapter adapter;//אדאפטר לכל פריט
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;//תפריט ניווט
    SearchView searchView;//תיבת חיפוש מוצר
    ToggleButton price,quantity;//לחצן לפילטור
    ImageView sort;
    ArrayList<String> userFav;//במקרה ונלקח על ידי היוזר לראות את המוצרים האוהבים עליו
    Boolean priceSort = false,quantitySort = false;//בדיקה לפילטור מה נלחץ


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();//hidenig the action bar
        setContentView(R.layout.actvity_loadin_screen);
        db = FirebaseFirestore.getInstance();
        loadData();
    }



    //Thred that handling the entire interface for more smoth transtion between activitys
    //its loading the ui and then showing the xml of this activity
    private void loadData() {
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(() -> {
            userFav = getIntent().getStringArrayListExtra("fav");
            alAllItems = new ArrayList<>();
            if(userFav == null)
                loadItemsToGridView();
            else
                loadFav();
            runOnUiThread(() -> {
                // Switch the content view back to the original layout
                setContentView(R.layout.activity_grid_view);
                GVAllItems = findViewById(R.id.GVGridView);
                sort = findViewById(R.id.grid_sort);
                searchView = findViewById(R.id.grid_view_search_bar);
//                progressBar = findViewById(R.id.gridview_pr);
                bottomNavigationView =findViewById(R.id.grid_bottomNavigationView);
                bottomNavigationView.setSelectedItemId(R.id.bt_menu_gridView);
                searchView.setOnQueryTextListener(this);
                sort.setOnClickListener(this);
                bottomNavigationView.setOnItemSelectedListener(this);
            });
        });
    }


    //Input: null
    //Showing a grid with all the user favourite items
    private void loadFav() {
        if(userFav.size() >0)
            db.collection("collection3").whereIn(FieldPath.documentId(),userFav).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        // after getting data from Firebase we are
                        // storing that data in our array list
                        alAllItems.add(item);
                    }
                    // after that we are passing our array list to our adapter class.
                    adapter = new ItemsGVAdapter(GridView.this, alAllItems);
                    // after passing this array list to our adapter
                    // class we are setting our adapter to our list view.
                    GVAllItems.setAdapter(adapter);
//                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    Toast.makeText(GridView.this, "No favourites found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //loading the grid view from firestore
    private void loadItemsToGridView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("collection3").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // after getting the data we are calling on success method
                                // and inside this method we are checking if the received
                                // query snapshot is empty or not.
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // if the snapshot is not empty we are hiding
                                    // our progress bar and adding our data in a list.
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    Log.d(getString(R.string.TAG), "List size: " + list.size());
                                    for (DocumentSnapshot d : list) {
                                        // after getting this list we are passing
                                        // that list to our object class.
                                        DocumentReference sfRef = d.getReference();
                                        if (d.get("idItem", String.class) != null) {
                                            sfRef.update("idItem", d.getId());
                                        }
                                        Item item = d.toObject(Item.class);
                                        item.setIdItem(d.getId());
                                        // after getting data from Firebase we are
                                        // storing that data in our array list
                                        alAllItems.add(item);
                                    }
                                    // after that we are passing our array list to our adapter class.
                                    adapter = new ItemsGVAdapter(GridView.this, alAllItems);
                                    // after passing this array list to our adapter
                                    // class we are setting our adapter to our list view.
                                    GVAllItems.setAdapter(adapter);
//                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    Log.d(getString(R.string.TAG), "No data found in query");
                                    // if the snapshot is empty we are displaying a toast message.
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // when we get any error from Firebase.
                                Toast.makeText(GridView.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }).start();
    }
    //navgation bar handling
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("BAR","In");
        switch (item.getItemId()) {
            case R.id.bt_menu_createPost:
                Log.d("BAR","post");
                finish();
                startActivity(new Intent(this,ItemPost.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            case R.id.bt_menu_profile:
                Log.d("BAR","Profile");
                finish();
                startActivity(new Intent(this,ActivityProfile.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            case R.id.bt_menu_gridView:
                Log.d("BAR","Grid");
                finish();
                startActivity(new Intent(this,GridView.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
        }
        return false;
    }

    //Input: string, what the user searched
    //adaptes the girdview accordinely
    @Override
    public boolean onQueryTextSubmit(String query) {
        GridView.this.adapter.getFilter().filter(query);
        return false;
    }

    //Input: string, what the user typing
    //adaptes the grid view accordinely
    @Override
    public boolean onQueryTextChange(String newText) {
        GridView.this.adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v == sort)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getSystemService(LayoutInflater.class);
            View dialogView = inflater.inflate(R.layout.dialog_sorting, null);
            builder.setView(dialogView).setTitle("Sorting");
            price = dialogView.findViewById(R.id.sort_price_bt);
            quantity = dialogView.findViewById(R.id.sort_quantity_bt);
            price.setOnClickListener(this);
            quantity.setOnClickListener(this);
            builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList<Item> alFiltered = new ArrayList<>();
                    if(priceSort && quantitySort)//Both Ascending
                        filterByField(alFiltered,"itemPrice",Query.Direction.DESCENDING,"itemQuantity",Query.Direction.DESCENDING);
                    else if(priceSort)
                        filterByField(alFiltered,"itemPrice",Query.Direction.DESCENDING,"itemQuantity",Query.Direction.ASCENDING);
                    else if(quantitySort)
                        filterByField(alFiltered,"itemPrice",Query.Direction.ASCENDING,"itemQuantity",Query.Direction.DESCENDING);
                    else
                        filterByField(alFiltered,"itemPrice",Query.Direction.ASCENDING,"itemQuantity",Query.Direction.ASCENDING);
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if(v == price)
        {
            if(price.isChecked()){
                priceSort = true;
                price.setBackground(ContextCompat.getDrawable(this,R.drawable.arrow_up));}
            else{
                priceSort = false;
                price.setBackground(ContextCompat.getDrawable(this,R.drawable.arrow_down));}
        } else if (v == quantity) {
            if(quantity.isChecked()){
                quantitySort = true;
                quantity.setBackground(ContextCompat.getDrawable(this,R.drawable.arrow_up));}
            else{
                quantitySort = false;
                quantity.setBackground(ContextCompat.getDrawable(this,R.drawable.arrow_down));}
        }
    }

    /**
     This method filters items in a Firebase Firestore collection by two fields, and populates a GridView with the results.
     @param allFiltered An ArrayList of Item objects, which will be populated with the filtered results.
     @param sortField The first field to sort by.
     @param n1 The direction to sort the first field.
     @param sortField2 The second field to sort by.
     @param n2 The direction to sort the second field.
     */
    private void filterByField(ArrayList<Item> allFiltered, String sortField,Query.Direction n1,String sortField2,Query.Direction n2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("collection3").orderBy(sortField, n1).orderBy(sortField2,n2).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    Log.d(getString(R.string.TAG), "List size: " + list.size());
                                    for (DocumentSnapshot d : list) {
                                        Item item = d.toObject(Item.class);
                                        allFiltered.add(item);
                                    }
                                    adapter = new ItemsGVAdapter(GridView.this, allFiltered);
                                    GVAllItems.setAdapter(adapter);
                                } else {
                                    Log.d(getString(R.string.TAG), "No data found in query");
                                    Toast.makeText(GridView.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GridView.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                                Log.d("CH","problem: "+e);
                            }
                        });

            }
        }).start();
    }
}