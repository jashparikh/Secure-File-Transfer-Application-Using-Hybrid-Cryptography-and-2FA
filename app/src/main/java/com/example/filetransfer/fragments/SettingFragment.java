package com.example.filetransfer.fragments;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.core.Action;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.example.filetransfer.MainActivity;
import com.example.filetransfer.R;
import com.example.filetransfer.SharedPref;
import com.example.filetransfer.SignInActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;
public class SettingFragment extends Fragment {

    SharedPref sharedPref;
    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        SwitchMaterial darktoggle = view.findViewById(R.id.darkToggle);
        sharedPref = new SharedPref(getContext());

        if(sharedPref.loadNightModeState())
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darktoggle.setChecked(true);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            darktoggle.setChecked(false);
        }
        Button button = view.findViewById(R.id.signOutBtn);
        ImageView myshare = view.findViewById(R.id.myShare);
        TextView email = view.findViewById(R.id.email);
        email.setText(Amplify.Auth.getCurrentUser().getUsername());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplify.Auth.signOut(new Action() {
                    @Override
                    public void call() {
                        FirebaseMessaging.getInstance().deleteToken();
                        startActivity(new Intent(getContext(), SignInActivity.class));
                        getActivity().finish();
                    }
                }, new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                    }
                });
            }
        });
        darktoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    sharedPref.setNightMode(true);
                    Toast.makeText(getContext(), "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Fragment fragment= new SendFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else
                {
                    sharedPref.setNightMode(false);
                   Toast.makeText(getContext(), "Dark Mode Disabled", Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Fragment fragment= new SendFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
        myshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String title = "CryptShare Download Link";
                final String msg = "Hey! I need to send you confidential files. Download CryptShare, an app which implements Hybrid Cryptography and 2FA to Receive the file securely.\nHere's the link:\nhttps://drive.google.com/file/d/1gnyN0Rn7qvVCe9TlyJHR9SAJQdEEJPQt/view?usp=sharing";
                intent.putExtra(Intent.EXTRA_SUBJECT,title);
                intent.putExtra(Intent.EXTRA_TEXT,msg);
                startActivity(Intent.createChooser(intent,"Share Using"));
            }
        });

        return view;
    }
}