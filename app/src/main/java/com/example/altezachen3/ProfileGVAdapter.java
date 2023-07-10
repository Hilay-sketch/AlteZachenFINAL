package com.example.altezachen3;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileGVAdapter extends ArrayAdapter<Item> {

    FirebaseFirestore db;
    int pos = 0,height = 500,width =500;

    Item item;
    TextView itemNameGridView;
    StorageReference storageReference, imgRef;
    ImageView photoView;
    CardView cardView;
    Activity ac;

    public ProfileGVAdapter(@NonNull Context context, @NonNull ArrayList<Item> items) {
        super(context, 0, items);
        db = FirebaseFirestore.getInstance();
        ac = (Activity)context;
        width =getScreenWidth(ac);
        Log.d("WID","width screen: "+ width);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridView2 = convertView;
        if (gridView2 == null) {
            Log.d("msg", "== null");
            gridView2 = LayoutInflater.from(getContext()).inflate(R.layout.profile_grid_view_adapter, parent, false);
        }
        item = getItem(position);
        pos = position;
        storageReference = FirebaseStorage.getInstance().getReference();
        imgRef = storageReference.child(item.getItImagePath());
        Log.d("ER", "1111");
        photoView = gridView2.findViewById(R.id.profile_item_photo);
        if (item.getItImagePath() != null) {
            Log.d("CHN", imgRef.getPath());
            Log.d("wid",String.valueOf(width/3));
            Glide.with(getContext())
                    .load(imgRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(width/3,width/3)
                    .into(photoView);
        }
        //Setting the image view size correctly
        photoView.setLayoutParams(new RelativeLayout.LayoutParams(width/3, width/3));
        Log.d("wid", "width: " + photoView.getWidth());
//        String itNameTrim = item.getItemName();
//        if( item.getItemName().length() > 14)
//            itNameTrim =  item.getItemName().substring(0,11).trim() + "...";
//        itemNameGridView.setText(itNameTrim);
//        itemNameGridView.setMaxWidth((width/2)-20);
        //When Clicked
        gridView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = getItem(position);
                Log.d("CL", item.getItemName());
                Intent itemInfo = new Intent(ac, ItemShow.class);
                itemInfo.putExtra("itemSelected", item);
                Log.d("sw", itemInfo.toString());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ac, cardView, "imageTransition");
                startActivity(ac,itemInfo,options.toBundle());
            }
        });
        return gridView2;
    }


    //to get the screen size

    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }
    public static int getScreenHeight(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }
}
