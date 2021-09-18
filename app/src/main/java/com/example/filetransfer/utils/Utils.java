package com.example.filetransfer.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class Utils {
    private static final String TAG = "DEBUG";

    public static String generateOTP() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    public static String getCurrentDate() {
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());

        Date currentTime = localCalendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String date = currentTime.toString().substring(4, 10);
        String year = currentTime.toString().substring(30);
        String time = dateFormat.format(new Date());

        return date + " " + year + " " + time;
    }

    public static void createFolder(String path, String name) {
        File file = new File(Environment.getExternalStorageDirectory() + path, name);
        if (file.exists()) {
            Log.d(TAG, "createFolder: Exists");
        } else {
            file.mkdirs();
            if (file.isDirectory()) {
                Log.d(TAG, "createFolder: Created");
            } else {
                Log.d(TAG, "createFolder: Failed");
            }
        }
    }

    public static String getFileName(Uri uri) {
        String fileName = uri.getPath().substring(uri.getPath().indexOf(":") + 1);
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        }
        return fileName;
    }

    public static String getFilePath(Uri uri) {
        return uri.getPath().substring(uri.getPath().indexOf(":") + 1);
    }
}
