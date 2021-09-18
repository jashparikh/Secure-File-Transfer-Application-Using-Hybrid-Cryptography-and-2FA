package com.example.filetransfer.notification;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


//class extending FirebaseMessagingService
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("DEBUG", "Notification");
        if (remoteMessage != null) {
            MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
            Map<String, String> msg = remoteMessage.getData();
            Log.d("DEBUG", msg.toString());
            myNotificationManager.sendNotification(2, msg.get("title"), msg.get("message"));
            Log.d("DEBUG", remoteMessage.toString());
        }
        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        if (remoteMessage.getData().size() > 0) {
            //handle the data message here
        }

        //getting the title and the body


        //then here we can use the title and body to build a notification 
    }
}
 