package com.example.filetransfer;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences sharedPreferences;
    public SharedPref(Context context)
    {
        sharedPreferences = context.getSharedPreferences("User Preferences",Context.MODE_PRIVATE);
    }
    public void setNightMode(Boolean state)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("DarkMode",state);
        editor.apply();
    }
    public Boolean loadNightModeState()
    {
        return sharedPreferences.getBoolean("DarkMode",false);
    }
}
