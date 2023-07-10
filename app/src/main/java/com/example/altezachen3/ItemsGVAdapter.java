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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.transition.ChangeImageTransform;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemsGVAdapter extends ArrayAdapter<Item> {
    FirebaseFirestore db;
    int pos = 0,height = 500,width =500;//נתונים סטטים כגודל המסך מיקום האדפטר
    Item item;//המוצר אותו קיבלנו מהגריד וויו
    TextView itemNameGridView,itemPriceGridView;//שם ומחיר המוצר להצגה
    StorageReference storageReference, imgRef;//רפרנסים לסטורג'
    ImageView photoView;//תמונת המוצר
    CardView cardView;//קארד וויו בו נשים את כל הנתונים כולל התמונה והמחיר
    Activity ac;//האקטיביטי ממנה הגענו
    Context acContext;//הקונטקסט של האקטיביטי ממנה הגענו

    public ItemsGVAdapter(@NonNull Context context, @NonNull ArrayList<Item> items) {
        super(context, 0, items);
        db = FirebaseFirestore.getInstance();
        ac = (Activity)context;
        acContext = context;
        height = getScreenHeight(ac);
        width = getScreenWidth(ac);
    }

    //setting the view of the adapter
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid_view_adapter, parent, false);
        }
        itemNameGridView = gridView.findViewById(R.id.gridViewTextItemName);
        item = getItem(position);
        pos = position;
        storageReference = FirebaseStorage.getInstance().getReference();
        imgRef = storageReference.child(item.getItImagePath());
        cardView = gridView.findViewById(R.id.card_view);
        itemPriceGridView = gridView.findViewById(R.id.adapter_price);
        itemPriceGridView.setText(item.getItemPrice()+"$");
        photoView = gridView.findViewById(R.id.btImg);
        if (item.getItImagePath() != null) {
            Log.d("CHN", imgRef.getPath());
            Log.d("wid",String.valueOf(width/2));
            Glide.with(getContext())
                    .load(imgRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(width/2,width/2)
                    .into(photoView);
        }
        Log.d("wid", "width: " + photoView.getWidth());
        String itNameTrim = item.getItemName();
        if( item.getItemName().length() > 14)
            itNameTrim =  item.getItemName().substring(0,11).trim() + "...";
        itemNameGridView.setText(itNameTrim);
        itemNameGridView.setMaxWidth((width/2)-20);
        //When Clicked
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = getItem(position);
                Log.d("CL", item.getItemName());
                Intent itemInfo = new Intent(ac, ItemShow.class);
                itemInfo.putExtra("itemSelected", item);
                Log.d("sw", itemInfo.toString());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ac, cardView, "imageTransition");
                startActivity(ac,itemInfo, options.toBundle());
            }
        });
    return gridView;
    }



    //getting the screen resolution
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