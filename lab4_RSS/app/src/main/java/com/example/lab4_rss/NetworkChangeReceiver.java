package com.example.lab4_rss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);
        NetworkState state = NetworkUtil.getConnectivityStatus(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if (context.getClass() == MainActivity.class) {
            if (state == NetworkState.NOT_CONNECTED)
                ((MainActivity) context).setOfflineMode();
            else
                ((MainActivity) context).setOnlineMode();
        }
    }
}


