package com.example.altezachen3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class WiFiConnectioOn extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            if(isOnline(context))
            {
//                Toast.makeText(context, "We got Wi-Fi", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "No Wifi", Toast.LENGTH_SHORT).show();
            }

        }
        catch (NullPointerException e )
        {
            e.printStackTrace();
        }

    }
    public boolean isOnline(Context context)
    {
        try{
            ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return(networkInfo!=null && networkInfo.isConnected());
        }
        catch (NullPointerException e )
        {
            e.printStackTrace();
            return false;
        }
    }
}