package com.example.altezachen3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.index.qual.GTENegativeOne;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button signUp, logIn;//כפתורים להרשמה התחברות
    private FirebaseAuth mAuth;//רפרנס מערכת אימות פיירבייס
    String email;//מייל
    String password;//סיסמה
    EditText emailPre;//תיבת טקסט אימייל
    EditText passwordPre;//תיבת טקסט סיסמה
    BroadcastReceiver broadcastReceiver;//ברודקסט רסיבר לבדיקת מצב החיבוריות לרשת
    FirebaseFirestore db;//רפרנס לפיירסטור
    Intent logToCraeteAccount,logToAccount;//מעבר לאקטיביטי שונה ביחס לחיבור המשתמש
    Integer REQUEST_CODE_PERMISSIONS = 100;//קוד אישור

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_loadin_screen);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loadData();
    }
    private void loadData() {
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(() -> {
            broadcastReceiver = new WiFiConnectioOn();
            registerNetworkBroadcastReciver();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            this.startService(new Intent(this, MyFirebaseMessagingService.class));
            if (currentUser != null){
                reload();}//לעשות שברילוד אם יש משתמש שיעביר ישר למסך השני ובכך יהיה פינגפונג

            runOnUiThread(() -> {
                // Switch the content view back to the original layout
                setContentView(R.layout.activity_main);
//                pr = findViewById(R.id.login_proggres_bar);
                signUp = findViewById(R.id.btSignUp);
                logIn = findViewById(R.id.btRegister);
                emailPre = findViewById(R.id.editMail);
                passwordPre = findViewById(R.id.editPassword);
                if(currentUser != null)
                {emailPre.setVisibility(View.GONE);
                    passwordPre.setVisibility(View.GONE);
                    signUp.setVisibility(View.GONE);
                    logIn.setVisibility(View.GONE);}
                logToAccount = new Intent(LoginActivity.this, GridView.class);
                logToCraeteAccount = new Intent(LoginActivity.this, UserCreate.class);
                signUp.setOnClickListener(this);
                logIn.setOnClickListener(this);
                PackageManager pm = getPackageManager();
                String[] permissions = {
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.VIBRATE,
                        android.Manifest.permission.POST_NOTIFICATIONS,
                        android.Manifest.permission.ACCESS_NETWORK_STATE
                };
                // Check if all the permissions are granted
                boolean allPermissionsGranted = true;
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                if (!allPermissionsGranted) {
                    // All permissions are already granted, perform your action
                    ActivityCompat.requestPermissions(this, permissions, 1000);
                } else {
                    // Request the permissions
                }
                if (pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, getPackageName()) != PackageManager.PERMISSION_GRANTED
                        || pm.checkPermission(android.Manifest.permission.INTERNET, getPackageName()) != PackageManager.PERMISSION_GRANTED
                        || pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName()) != PackageManager.PERMISSION_GRANTED
                        || pm.checkPermission(android.Manifest.permission.VIBRATE, getPackageName()) != PackageManager.PERMISSION_GRANTED
                        || pm.checkPermission(android.Manifest.permission.POST_NOTIFICATIONS, getPackageName()) != PackageManager.PERMISSION_GRANTED
                        || pm.checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                    if (pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[] { android.Manifest.permission.ACCESS_NETWORK_STATE },
                                REQUEST_CODE_PERMISSIONS);
                    }

                    if (pm.checkPermission(android.Manifest.permission.INTERNET, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[] { android.Manifest.permission.INTERNET },
                                REQUEST_CODE_PERMISSIONS);
                    }

                    if (pm.checkPermission(android.Manifest.permission.CAMERA, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[] { android.Manifest.permission.CAMERA },
                                REQUEST_CODE_PERMISSIONS);
                    }

                    if (pm.checkPermission(android.Manifest.permission.VIBRATE, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[] { android.Manifest.permission.VIBRATE },
                                REQUEST_CODE_PERMISSIONS);
                    }

                    if (pm.checkPermission(android.Manifest.permission.POST_NOTIFICATIONS, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[] { android.Manifest.permission.POST_NOTIFICATIONS },
                                REQUEST_CODE_PERMISSIONS);
                    }

                    if (pm.checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                                REQUEST_CODE_PERMISSIONS);
                    }

                }

            });
        });
    }


    //regestring the brodcast of the wifi
    protected void registerNetworkBroadcastReciver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    //unregestring the brodcast of the wifi
    protected void unregisterNetwork() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    }

    @Override
    public void onStart() {
        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            emailPre.setVisibility(View.GONE);
//            passwordPre.setVisibility(View.GONE);
//            signUp.setVisibility(View.GONE);
//            logIn.setVisibility(View.GONE);
//            reload();}//לעשות שברילוד אם יש משתמש שיעביר ישר למסך השני ובכך יהיה פינגפונג
//        else
//            pr.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Do something with the permission
            } else {
                // Permission denied
                // Show an explanation to the user or handle the denial
            }
        }
    }

    public void onClick(View v) {

        // All permissions are granted
        // Do your work here

        password = passwordPre.getText().toString();
        email = emailPre.getText().toString();
        if (v == signUp) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Auth", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent toUserCreate = new Intent(LoginActivity.this, UserCreate.class);
                        startActivity(toUserCreate);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Auth", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Toast.makeText(this, "signing up", Toast.LENGTH_SHORT).show();
        } else if (v == logIn) {
            Toast.makeText(this, "log in", Toast.LENGTH_SHORT).show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("logIn", "signInWithEmail:success");
                        FirebaseUser authContext = mAuth.getCurrentUser();
                        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
                        docRef.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DocumentSnapshot document = task2.getResult();
                                if (document.exists()) {
                                    startActivity(logToAccount);
                                } else {
                                    startActivity(logToCraeteAccount);
                                }
                            } else {
                                Log.d("US", "error");
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("logIn", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void reload() {
        Log.d("OpenItemPost", "Open Item Post");
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    startActivity(logToAccount);
                } else {
                    startActivity(logToCraeteAccount);
                }
            } else {
                Log.d("US", "error");
            }
        });

    }

}