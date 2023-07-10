package com.example.altezachen3;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ItemShow extends BasicActivity implements View.OnClickListener {
    ImageView itemImage,creatorPhoto, callNumber, deleteBt,editBt; //תמונת המוצר וכפתורים למחיקת המוצר ועריכותו
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    StorageReference storageReference, imgRef;
    TextView itName,itPrice,itWidth,itLeangth,itHeight,creatorName,itQuantity;//מידע על המוצר
    ToggleButton wishlist;//להוסיף/להסיר את המוצר מהווישליסט
    CardView creatorCV;
    String phoneNumber;//מס פלאפון של היוצר
    boolean isButtonAddToFavPressed;
    Item item;//המוצר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_show);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        Activity ac = this ;
        editBt = findViewById(R.id.item_edit);
        deleteBt = findViewById(R.id.item_delete);
        itQuantity = findViewById(R.id.item_quantity);
        callNumber =findViewById(R.id.item_phone);
        storageReference = FirebaseStorage.getInstance().getReference();
        wishlist = findViewById(R.id.item_toggle);
        creatorCV = findViewById(R.id.item_userBigCardView);
        creatorName = findViewById(R.id.item_creatorName);
        creatorPhoto = findViewById(R.id.item_creatorPhoto);
        Intent intent = getIntent();
        Log.d("msf","in");
        itemImage = findViewById(R.id.item_image);
        item = (Item) intent.getSerializableExtra("itemSelected");
        imgRef = storageReference.child(item.getItImagePath());
        if (item.getItImagePath() != null) {
            Log.d("CHN", imgRef.getPath());
            Glide.with(this)
                    .load(imgRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(itemImage);
        }
        //cretor chip
        db.collection("users").document(item.getCreatorMail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile creator = documentSnapshot.toObject(Profile.class);

                if(creator != null){
                    phoneNumber = "tel:"+creator.getPhoneNumber();
                    Log.d("PRO", creator.toString());
                    if(creator.getAccountName() != null && creator.getAccountName().length() > 10)
                        creatorName.setText(creator.accountName.substring(0,7)+"..");
                    else if(creator.getAccountName() != null)
                        creatorName.setText(creator.accountName);
                    if(creator.getProfilePicturePath() != null)
                        Glide.with(ac)
                                .load(storageReference.child("images/"+creator.getProfilePicturePath()))
                                .diskCacheStrategy(DiskCacheStrategy.NONE).
                                into(creatorPhoto);}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                creatorCV.setVisibility(View.GONE);
                Log.d("PRO", "Problem occured"+e);
                creatorName.setText("Null");
            }
        });
        itName = findViewById(R.id.item_name);
        itPrice = findViewById(R.id.item_price);
        itLeangth = findViewById(R.id.item_leangth);
        itHeight = findViewById(R.id.item_height);
        itWidth = findViewById(R.id.item_width);

        itName.setText(item.getItemName());
        itPrice.setText(String.valueOf(item.getItemPrice())+'$');
        itLeangth.setText(String.valueOf(item.getItemSize().getItemX()));
        itWidth.setText(String.valueOf(item.getItemSize().getItemY()));
        itHeight.setText(String.valueOf(item.getItemSize().getItemZ()));
        itQuantity.setText("pieces: "+item.getItemQuantity());
        creatorCV.setOnClickListener(this);
        callNumber.setOnClickListener(this);
        isButtonAddToFavPressed = false;
        editBt.setOnClickListener(this);
        deleteBt.setOnClickListener(this);
        wishlist.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.add_to_fav_selector, null));
        wishlist.setOnClickListener(this);
        if (!currentUser.getEmail().equals(item.getCreatorMail())){
            editBt.setVisibility(View.GONE);
            editBt.setOnClickListener(null);
            deleteBt.setVisibility(View.GONE);
            deleteBt.setOnClickListener(null);}

    }

    @Override
    public void onClick(View v) {
        if(v == editBt)
        {
            Intent toEditPost = new Intent(this,ItemPost.class);
            toEditPost.putExtra("itemEdit", item);
            startActivity(toEditPost);
        }
        else if(v == creatorCV)
        {
            Intent showCreatorProfile = new Intent(this,ActivityProfile.class);
            showCreatorProfile.putExtra("email",item.getCreatorMail());
            startActivity(showCreatorProfile);
        }
        else if(v == deleteBt)
        {
            //delteing a product
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you'd like to delete this product?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.collection("collection3").document(item.getIdItem()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(ItemShow.this, "Deleted", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(ItemShow.this, "Couldn't delete:" +task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(ItemShow.this, "canceled", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if(v == wishlist)
        {
            if(wishlist.isChecked())
            {
                // Define the scale and alpha animations
                ObjectAnimator animGrow = ObjectAnimator.ofPropertyValuesHolder(
                        wishlist,
                        PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f),
                        PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.5f));
                animGrow.setDuration(200);
                ObjectAnimator animShrink = ObjectAnimator.ofPropertyValuesHolder(
                        wishlist,
                        PropertyValuesHolder.ofFloat("scaleX", 1.2f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1.0f),
                        PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f));
                animShrink.setDuration(200);

                // Chain the animations together
                AnimatorSet animSet = new AnimatorSet();
                animSet.playSequentially(animGrow, animShrink);
                animSet.start();
                //remove from wishlist function
                changingWishList(true);
                Log.d("del", "addd");
                isButtonAddToFavPressed = false;
                wishlist.setBackground(ContextCompat.getDrawable(this ,R.drawable.my_wish_list_fav));
            } else {
                Log.d("del", "removed");
                isButtonAddToFavPressed = true;
                changingWishList(false);
                wishlist.setBackground(ContextCompat.getDrawable(this ,R.drawable.my_wish_list_unfav));
            }

        } else if (v == callNumber) {
            String message = "Hey, I'm interested in your product, "+item.getItemName()+"!"; // replace with the desired message
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("whatsapp://send?phone=" + phoneNumber + "&text=" + message));
            startActivity(intent);
        }
    }


    //input: boolean
    //adds/removes item from wishlist
    public void changingWishList(boolean toAdd)
    {

        db.collection("users").document(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile pro = documentSnapshot.toObject(Profile.class);
                ArrayList<String> newWishlist = pro.getWishListItemsID();
                if(toAdd)
                    newWishlist.add(item.getIdItem());
                else
                    newWishlist.remove(item.getIdItem());
                Set<String> set = new HashSet<String>(newWishlist);
                newWishlist.clear();
                newWishlist.addAll(set);
                db.collection("users").document(currentUser.getEmail()).update("wishListItemsID",newWishlist).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(toAdd)
                            Toast.makeText(ItemShow.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ItemShow.this, "Deleted from the wishlist", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ItemShow.this, "Couldn't add: "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ItemShow.this, "Please sign as a user to add to wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}