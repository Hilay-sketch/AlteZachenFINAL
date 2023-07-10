package com.example.altezachen3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BasicActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;//רפרנס לאוטנטיקציה
    FirebaseFirestore db;//רפרנס לפיירסטור
    StorageReference storageReference, imgRef;//רפרנס לסטורג
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


    //creating the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menuLogOut).setIcon(R.drawable.logout);
        menu.findItem(R.id.menuReload).setIcon(R.drawable.refresh);
        return true;
    }

    //handling the options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem Mitem) {
        super.onOptionsItemSelected(Mitem);
        if (Mitem.getItemId() == R.id.menuLogOut) {
            mAuth.signOut();
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (Mitem.getItemId() == R.id.menuReload) {
            this.finish();
            startActivity(getIntent());
        }
        else if (Mitem.getItemId() ==  R.id.menuUserCreate) {
            startActivity(new Intent(this,UserCreate.class));
        }
        return true;
    }

}



