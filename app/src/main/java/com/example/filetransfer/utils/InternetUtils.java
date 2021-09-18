package com.example.filetransfer.utils;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.lifecycle.LiveData;

public class InternetUtils extends LiveData<Boolean> {
    BroadcastReceiver internetReceiver;
    Application application;

    InternetUtils(Application application) {
        this.application = application;
    }

    Boolean isInternetOn() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void onActive() {
        registerInternetReceiver();
    }

    private void registerInternetReceiver() {
        if (internetReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            internetReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = extras.getParcelable("networkInfo");
                    boolean value = info.getState() == NetworkInfo.State.CONNECTED;
                }
            };

            application.registerReceiver(internetReceiver, filter);
        }
    }


    private void unRegisterBroadCastReceiver() {
        if (internetReceiver != null) {
            application.unregisterReceiver(internetReceiver);
            internetReceiver = null;
        }
    }
}
