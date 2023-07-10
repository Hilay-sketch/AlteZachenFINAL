package com.example.altezachen3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FIREBASE_NOTF";
    final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Heads Up Notification", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID).setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.app_icon_assets)
                .setAutoCancel(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            PackageManager pm = getPackageManager();
//            if (pm.checkPermission(android.Manifest.permission.POST_NOTIFICATIONS, getPackageName()) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getApplication(),
//                        new String[] { android.Manifest.permission.POST_NOTIFICATIONS },
//                        77);}
                return;
        }
        NotificationManagerCompat.from(this).notify(1, notification.build());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
    }
}