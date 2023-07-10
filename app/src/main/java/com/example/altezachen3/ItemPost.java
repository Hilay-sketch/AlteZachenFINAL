package com.example.altezachen3;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class
ItemPost extends BasicActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, NavigationBarView.OnItemSelectedListener {
    Button add, uploadPhoto, cameraPhoto;
    private FirebaseAuth mAuth;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    Uri uri;//קובץ להעלאה לתמונה
    Boolean update =false;
    Bitmap image;//קובץ להעלאה לתמונה
    Boolean isSeller;//בודק אם מוכר כדי לפתוח/לנעול את האופציה להעלות מוצר
    TextView txUserName;//שם היוזר
    EditText preItName, preItPrice, preItQuantity, preItLength, preItWidth, preItHeight;//מידע על המוצר כתיבת טקסט
    String itName, itPrice, itQuantity, itLength, itWidth, itHeight, itCatagory, catagoryS, itImagePath;//מידע על המוצר
    Item item1,itemEdit;//המוצר
    ItemSize size1;//גודל המוצר
    Intent intentToListView, galleryAdd, data,intent;//פעולות שדרכם נוכל להעלות תמונה ממקומות שונים
    Spinner spCatagory;//ספינר לקטוגריה
    StorageReference storageRef;
    FirebaseStorage storage;
    ImageView photo;
    BottomNavigationView bottomNavigationView;
    TextView hint,hintPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_post);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        txUserName = findViewById(R.id.txUser);
        itemEdit = null;
        bottomNavigationView =findViewById(R.id.post_bottomNavigationView);
        intent = getIntent();
        itemEdit = (Item) intent.getSerializableExtra("itemEdit");

        ArrayList<String> array_spinner = new ArrayList<>();
        array_spinner.add( "Chair");
        array_spinner.add("Sofa");
        array_spinner.add("Table");
        array_spinner.add("Other");

        spCatagory = findViewById(R.id.spinnerCatgory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array_spinner);
        spCatagory.setAdapter(adapter);
        spCatagory.setOnItemSelectedListener(this);

        add = findViewById(R.id.btUploadItem);
        hint = findViewById(R.id.post_cant_upload);
        hintPhoto = findViewById(R.id.hintAddAphoto);

        preItName = findViewById(R.id.editItName);
        preItPrice = findViewById(R.id.editItPrice);
        preItHeight = findViewById(R.id.editItHeight);
        preItQuantity = findViewById(R.id.editItQuantity);
        preItWidth = findViewById(R.id.editItWidth);
        preItLength = findViewById(R.id.editItLength);
        uploadPhoto = findViewById(R.id.btAddPhoto);
        photo = findViewById(R.id.IVimage);
        cameraPhoto =findViewById(R.id.btCameraPhoto);
        db = FirebaseFirestore.getInstance();

        add.setOnClickListener(this);
        cameraPhoto.setOnClickListener(this);

        uploadPhoto.setOnClickListener(this);
        bottomNavigationView.setSelectedItemId(R.id.bt_menu_createPost);
        bottomNavigationView.setOnItemSelectedListener(this);
        if(itemEdit != null){
            preItName.setText(itemEdit.getItemName());
            preItPrice.setText(String.valueOf(itemEdit.getItemPrice()));
            preItQuantity.setText(String.valueOf(itemEdit.getItemQuantity()));
            preItLength.setText(String.valueOf(itemEdit.getItemSize().getItemY()));
            preItWidth.setText(String.valueOf(itemEdit.getItemSize().getItemX()));
            preItHeight.setText(String.valueOf(itemEdit.getItemSize().getItemZ()));
            spCatagory.setSelection(array_spinner.indexOf(itemEdit.getCategory()));
            imgRef = storageReference.child(itemEdit.getItImagePath());
            hintPhoto.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).
                    load(imgRef).
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    into(photo);
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(imgRef)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            image = resource;
                            update = true;
                            uri = getImageUri(getApplicationContext(), image);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    if(task.getResult().get("seller",boolean.class))
                    {
                        Log.d("US", "in "+task.getResult().toString());
                        isSeller = true;
                    }
                    else {
                        Log.d("US","out "+ task.getResult().toString());
                        isSeller = false;
                        add.setVisibility(View.GONE);
                        hint.setVisibility(View.VISIBLE);
                    }
                    txUserName.setText(task.getResult().get("accountName",String.class));
                }
                else
                    txUserName.setText(currentUser.getEmail());

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == add) {
            // Define the scale and alpha animations
            ObjectAnimator animGrow = ObjectAnimator.ofPropertyValuesHolder(
                    add,
                    PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f),
                    PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.5f));
            animGrow.setDuration(200);
            ObjectAnimator animShrink = ObjectAnimator.ofPropertyValuesHolder(
                    add,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f, 1.0f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f, 1.0f),
                    PropertyValuesHolder.ofFloat("alpha", 0.5f, 1.0f));
            animShrink.setDuration(200);

            // Chain the animations together
            AnimatorSet animSet = new AnimatorSet();
            animSet.playSequentially(animGrow, animShrink);
            animSet.start();
            if(update){
                if(checkItemValues()){
                    UploadPhoto2();
                    DocumentReference docRef = db.collection("collection3").document(itemEdit.getIdItem());
                    docRef.set(createItem()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ItemPost.this, "Updated", Toast.LENGTH_SHORT).show();
                            docRef.update("idItem", docRef.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ItemPost.this, "Couldn't update:"+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else {
                if (checkItemValues()) {
                    UploadPhoto2();//when add clicked the photo will uploaded
                    db.collection("collection3").add(createItem()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Updating the id in the firestore
                            documentReference.update("idItem", documentReference.getId());
                            db.collection("users").document(currentUser.getEmail()).update("postsList", FieldValue.arrayUnion(documentReference.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ItemPost.this, "Successful added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ItemPost.this, "Failed to upload:" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d("UP", "the item didn't apply it self");
                    Toast.makeText(ItemPost.this, "failed - short the name", Toast.LENGTH_LONG).show();
                }
            }
        } else if (v == uploadPhoto) {//uploading a photo to the activity
            galleryAdd = new Intent(Intent.ACTION_PICK);
            galleryAdd.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryAdd.putExtra("GALLERY_REQ_CODE", 1000);
            someActivityResultLauncher.launch(galleryAdd);
        } else if (v == cameraPhoto) {
            //CHECKING THAY I HAVE PREMISSON TO CAMERA
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            }
            dispatchTakePictureIntent();
        }
    }
    //Camera intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("CAMERA_REQ_CODE",1000);
        openCamera();
    }
    //Taking a photo from gallery or camera and uploading it to the activity
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        data = result.getData();
                        photo.setImageURI(data.getData());
                        photo.setTransitionName(data.getDataString());
                    }
                    else
                        Log.d("CAM","Problem erupt:"+ result);
                }
            });



    //Input: null
    //Output: true if all the values are good else false
    private boolean checkItemValues() {
        Item checkIt = createItem();
        if(checkIt.getItemName().length() < 35 && checkIt.getItemName().length() >0)
            if(checkIt.getItemPrice() > 0)
                if(checkIt.getItemQuantity() > 0)
                    if (checkIt.getItemSize().getItemX() > 0 && checkIt.getItemSize().getItemY() > 0 && checkIt.getItemSize().getItemZ() > 0)
                        if (checkIt.getCategory() != null)
                            if (photo != null)
                                return true;
        return false;
    }

    //creates and handels the camera request
    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
        //Log.d("CAM", data.toString());
    }

    //getting the uri from image
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //for the camera its gets an intent and put on it the data from the camera and handels the camera
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            photo.setImageBitmap(image);
            photo.setImageURI(data.getData());
            uri = getImageUri(this, image);
        }
    }


    //uploading an image to firestoreStorage
    private void UploadPhoto2() {
        Uri file;
        if(data != null){
            file = data.getData();}
        else
        {
            file = uri;
        }
        itImagePath = itemPath();
        StorageReference riversRef = storageRef.child("images/" + itImagePath);
        UploadTask uploadTask = riversRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("UPIMAGE", exception.toString());
                Toast.makeText(ItemPost.this, "Sorry currently we are unable to upload tour image", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ItemPost.this, "The image was successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //creates and returning a random genreted string
    public String itemPath() {
        String path = "";
        String usermail2 =currentUser.getEmail();
        char[] n1 =usermail2.toCharArray();
        for (int i =0; n1[i] != '@' && i < n1.length-2; i++)
        {
            if (n1[i] != '[' && n1[i] != '['  && n1[i] != '@'  && n1[i] != ' ' && n1[i] != '!' && n1[i] != '.'&& n1[i] != '/' && n1[i] != '*')
            {
                path = path + n1[i] + String.valueOf(Math.round(Math.random()*(10-1)+1));
            }
        }
        Log.d("PATH",path);
        return path;
    }


    public Item createItem() {//יוצר עצם שאותו אני שומר
        if(preItPrice.getText() != null)
            itPrice = preItPrice.getText().toString();
        if(preItName.getText() != null)
            itName = preItName.getText().toString();
        if(preItQuantity.getText() != null)
            itQuantity = preItQuantity.getText().toString();
        if(preItLength.getText() != null)
            itLength = preItLength.getText().toString();
        if(preItWidth.getText() != null)
            itWidth = preItWidth.getText().toString();
        if(preItHeight.getText() != null)
            itHeight = preItHeight.getText().toString();
        if(catagoryS != null)
            itCatagory = catagoryS;
        if(Double.parseDouble(itHeight)   > 0 && Double.parseDouble(itLength) > 0 && Double.parseDouble(itWidth) >0)
            size1 = new ItemSize(Double.parseDouble(itWidth), Double.parseDouble(itLength), Double.parseDouble(itHeight));
        item1 = new Item(itName, Double.parseDouble(itPrice), Integer.parseInt(itQuantity), size1, itCatagory, "images/" + itImagePath, currentUser.getEmail());
        return item1;
    }

    //spinner onListner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        catagoryS = adapterView.getItemAtPosition(i).toString();
    }

    //spinner defuelt option
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        catagoryS = "Chair";
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
                startActivity(new Intent(ItemPost.this,GridView.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
        }
        return false;
    }
}
