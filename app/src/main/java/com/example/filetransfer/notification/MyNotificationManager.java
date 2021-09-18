package com.example.filetransfer.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.filetransfer.MainActivity;
import com.example.filetransfer.R;

import static android.app.Notification.DEFAULT_ALL;
import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.PendingIntent.getActivity;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.Color.RED;
import static android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

public class MyNotificationManager {
    static String NOTIFICATION_ID = "appName_notification_id";
    static String NOTIFICATION_NAME = "appName";
    static String NOTIFICATION_CHANNEL = "appName_channel_01";
    public static String NOTIFICATION_WORK = "appName_notification_work";
    private Context mCtx;
    private static MyNotificationManager mInstance;

    public MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }

        return mInstance;
    }

    public void sendNotification(int id,String title,String desc) {
        Intent intent = new Intent(mCtx, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, id);

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        String titleNotification = title;
        String subtitleNotification = desc;
        PendingIntent pendingIntent = getActivity(mCtx, 0, intent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mCtx, NOTIFICATION_CHANNEL).setSmallIcon(R.drawable.add)
                .setContentTitle(titleNotification).setContentText(subtitleNotification)
                .setDefaults(DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true);

        notification.setPriority(PRIORITY_MAX);

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL);

            Uri ringtoneManager = getDefaultUri(TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(CONTENT_TYPE_SONIFICATION).build();

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.setSound(ringtoneManager, audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, notification.build());
    }
}