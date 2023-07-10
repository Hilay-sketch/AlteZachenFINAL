package com.example.altezachen3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserCreate extends BasicActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;//רפרנס לאוטנטיקציה
    FirebaseUser currentUser;//רפרנס ליוזר
    FirebaseFirestore db;//רפרנס לפיירסטור
    StorageReference storageRef;//רפרס לסטורג
    FirebaseStorage storage;//רפרנס לסטורג
    TextView usermail;//מראה את המייל של היוזר הנוכחי
    Button sumbitUser,gallery,camera;//לחצנים להבאת מידע והלאעתו
    ImageView profilePicture;//תמונת הפרופיל
    CardView imgCardView;//מכיל את התמונת פרופיל
    EditText userName, userPhone;//שם משתמש ופלאפון
    Intent galleryAdd, data;//פעולות להבאת תמונה
    String imgPath;//מיקום התמונה בסטורג
    CountryCodePicker codePicker;//תקייה ממנה אני מביא את המדינה ומיקוד היוזר
    CheckBox isManufacturer;//היוזר אומר אם הוא יצרן או פרטי
    Uri uri;//אורי התמונה
    int REQUEST_IMAGE_CAPTURE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        usermail = findViewById(R.id.cu_user_mail);
        sumbitUser = findViewById(R.id.cu_sumbit_user);
        profilePicture = findViewById(R.id.cu_profile_picture);
        imgCardView = findViewById(R.id.cu_cardViewProf);
        userName = findViewById(R.id.cu_user_name);
        userPhone = findViewById(R.id.cu_user_phone);
        codePicker = findViewById(R.id.cu_phone_start);
        codePicker.setDefaultCountryUsingNameCode("IL");
        codePicker.resetToDefaultCountry();
        isManufacturer = findViewById(R.id.cu_is_manufacturer);
        camera =findViewById(R.id.cu_camera);
        gallery = findViewById(R.id.cu_gallery);

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        sumbitUser.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        usermail.setText(currentUser.getEmail());
    }

    @Override
    public void onClick(View v) {
        if (v == sumbitUser) {
            createUser();
            if (checkData()) {
                UploadPhoto();
                db.collection("users").document(currentUser.getEmail()).set(createUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserCreate.this, "Welcome", Toast.LENGTH_SHORT).show();
                        Intent toGridView = new Intent(UserCreate.this,GridView.class);
                        startActivity(toGridView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("err", e.toString());
                        Toast.makeText(UserCreate.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (v == gallery) {
            galleryAdd = new Intent(Intent.ACTION_PICK);
            galleryAdd.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryAdd.putExtra("GALLERY_REQ_CODE", 1000);
            someActivityResultLauncher.launch(galleryAdd);
        }
        else if(v == camera)
        {
            //CHECKING THAY I HAVE PREMISSON TO CAMERA
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            }
            openCamera();
        }
    }

    //INPUT NULL
    //OUTPUT:USER CLASS OBJECT
    private Profile createUser() {
        String countryName = codePicker.getSelectedCountryName();
        if (isManufacturer.isChecked())
            return new ProfileManufacturer(userName.getText().toString(), currentUser.getEmail(), "+" + codePicker.getSelectedCountryCode() + userPhone.getText().toString(), imgPath, countryName);
        else
            return new Profile(userName.getText().toString(), currentUser.getEmail(), "+" + codePicker.getSelectedCountryCode() + userPhone.getText().toString(), imgPath);
    }

    //Input: null
    //output true if the data is correct else false
    public boolean checkData() {
        if (profilePicture.getDrawable() == null) {
            Toast.makeText(this, "add a profile picture", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userName.getText().toString().equals("")) {
            Toast.makeText(this, "add a name field", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userPhone.getText().toString().length() != 9) {
            Toast.makeText(this, "your number is incorrect", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //If the image given was okay upload it to the imageview on the activity
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        data = result.getData();
                        profilePicture.setImageURI(data.getData());
                        profilePicture.setTransitionName(data.getDataString());
                    }
                }
            });

    //uploading an image to firestoreStorage
    private void UploadPhoto() {
        Uri file;
        if(data != null){
            file = data.getData();}
        else
        {
            file = uri;
        }
        imgPath = itemPath();
        StorageReference riversRef = storageRef.child("images/" + imgPath);
        UploadTask uploadTask = riversRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("UPIMAGE", exception.toString());
                Toast.makeText(UserCreate.this, "Sorry currently we are unable to upload tour image", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserCreate.this, "The image was successfully uploaded", Toast.LENGTH_SHORT).show();

            }
        });

    }


    //getting the uri from image
    //Input: context of the activity, bitmap of the image
    //Output: Uri path of that image
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
            profilePicture.setImageBitmap(image);
            profilePicture.setImageURI(data.getData());
            uri = getImageUri(this, image);
        }
    }

    //Input: null
    //output: null
    //calls and creates the camera function
    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
        //Log.d("CAM", data.toString());
    }

    //INPUT: NULL
    //OUTPUT:String that contains a random image path
    public String itemPath() {
        String path = "";
        String usermail2 = currentUser.getEmail();
        char[] n1 = usermail2.toCharArray();
        for (int i = 0; n1[i] != '@' && i < n1.length - 2; i++) {
            if (n1[i] != '[' && n1[i] != '[' && n1[i] != '@' && n1[i] != ' ' && n1[i] != '!' && n1[i] != '.' && n1[i] != '/' && n1[i] != '*') {
                path = path + n1[i] + String.valueOf(Math.round(Math.random() * (10 - 1) + 1));
            }
        }
        Log.d("PATH", path);
        return path;
    }
}