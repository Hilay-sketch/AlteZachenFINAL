package com.example.altezachen3;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ActivityProfile extends BasicActivity implements NavigationBarView.OnItemSelectedListener, View.OnClickListener {

    private CardView prCardView;//מאכלס התמונת פרופיל
    private ImageView prPicture,showFav,phoneCall;//תמונות פרופיל, כפתור לוואצפ ווישליסט
    private TextView prName,prHintNoSeller;//שם המוכר ובמקרה ואינו מוכר מציג הודעה בהתאם
    android.widget.GridView prGridView;//גריד וויו למוכר של כל המוצרים שלו
    private FirebaseAuth mAuth;//רפרנס התקשרות למשתמש
    FirebaseUser currentUser;//יוזר נוכחי
    FirebaseFirestore db;//רפרנס לפיירסטור
    StorageReference storageReference, imgRef;//רפרנס לקבלץ תמונות מהענן
    FirebaseStorage storage;
    Profile user;//פרופיל של המשתמש
    ProfileGVAdapter adapter;//אדאפטר לגריד וויו של המשתמש
    ArrayList<String> userPosts,userFav;//ארייליסט למוצרים של היוזר
    ProfileManufacturer userSeller;//למקרה והוא מוכר יוזר מסוג אחר
    BottomNavigationView bottomNavigationView;//בר ניווט
    ArrayList<Item> alAllItems;//ארייליסט למוצרים של היוזר
    DocumentSnapshot doc;//רפרנס למסמכים בפיירסטור
    String userFromOther,phoneNumber;//ואם זה לא אנחנו פרטי היוזר האחר מס פלאפון של היוזר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userFromOther = getIntent().getStringExtra("email");
        prCardView = findViewById(R.id.pr_cardView);
        showFav = findViewById(R.id.pr_show_fav);
        prHintNoSeller =findViewById(R.id.pr_no_seller);
        prPicture = findViewById(R.id.pr_picture);
        userFav = new ArrayList<>();
        phoneCall = findViewById(R.id.pr_phone);
        prName = findViewById(R.id.pr_name);
        prGridView = findViewById(R.id.pr_grid_view);
        mAuth = FirebaseAuth.getInstance();
        alAllItems = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        userPosts = new ArrayList<>();
        phoneCall.setOnClickListener(this);
        bottomNavigationView =findViewById(R.id.profile_bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bt_menu_profile);
        bottomNavigationView.setOnItemSelectedListener(this);
        showFav.setOnClickListener(this);
        if(userFromOther != null){
            loadToGridView(userFromOther);}
        else{
            loadToGridView(currentUser.getEmail());}
    }


    //input: the mail of the user
    //loading the user interface and after finishing the grid view
    private void loadToGridView(String mail) {
        //loading the user interface
        db.collection("users").document(mail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    doc = task.getResult();
                    if (task.getResult().getBoolean("seller")) {
                        userSeller = task.getResult().toObject(ProfileManufacturer.class);
                        prName.setText(userSeller.accountName);
                        phoneNumber =  "tel:"+String.valueOf(task.getResult().get("phoneNumber"));
                        userPosts.addAll(userSeller.getPostsList());
                        userFav.addAll(userSeller.wishListItemsID);
                        if(userSeller.wishListItemsID != null)
                            userFav.addAll(userSeller.wishListItemsID);
                        Log.d("LSTSIZE", String.valueOf(userPosts.size()));
                        imgRef = storageReference.child("images/" + userSeller.getProfilePicturePath());
                        Log.d("msg", "images/" + userSeller.getProfilePicturePath());
                        loadingUserPosts(mail);
                    } else {
                        user = task.getResult().toObject(Profile.class);
                        prName.setText(user.accountName);
                        prHintNoSeller.setVisibility(View.VISIBLE);
                        if(user.wishListItemsID != null)
                            userFav.addAll(user.wishListItemsID);
                        imgRef = storageReference.child("images/" + user.getProfilePicturePath());
                        Log.d("msg", "images/" + user.getProfilePicturePath());
                    }

                    Glide.with(getApplicationContext())
                            .load(imgRef)
                            .into(prPicture);
                } else {
                    Toast.makeText(ActivityProfile.this, "You need to create a user!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //input: the mail of the user
    //loading the user posts
    private void loadingUserPosts(String mail) {
        //if the user deleted itself he still can get his posts
        db.collection("collection3").whereEqualTo("creatorMail",mail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d(getString(R.string.TAG), "List size: " + list.size());
                    for (DocumentSnapshot d : list) {
                        // after getting this list we are passing
                        // that list to our object class.
                        Item item = d.toObject(Item.class);
                        item.setIdItem(d.getId());
                        // after getting data from Firebase we are
                        // storing that data in our array list
                        alAllItems.add(item);
                    }
                    // after that we are passing our array list to our adapter class.
                    adapter = new ProfileGVAdapter(ActivityProfile.this, alAllItems);
                    // after passing this array list to our adapter
                    // class we are setting our adapter to our list view.
                    prGridView.setAdapter(adapter);
                }
                else {
                    Log.d(getString(R.string.TAG), "No data found in query");
                    // if the snapshot is empty we are displaying a toast message.
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityProfile.this, "No posts found for this user", Toast.LENGTH_SHORT).show();
            }
        });}
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

    @Override
    public void onClick(View v) {
        if(v == phoneCall)//CALLS THE USER
        {
            String message = "Hey, I'm interested in your products, please get back to me ASAP."; // replace with the desired message
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("whatsapp://send?phone=" + phoneNumber + "&text=" + message));
            startActivity(intent);
        }
        else if(v == showFav)
        {
            Intent showUserFav = new Intent(this,GridView.class);

            showUserFav.putExtra("fav", userFav);
            startActivity(showUserFav);
        }
    }
}