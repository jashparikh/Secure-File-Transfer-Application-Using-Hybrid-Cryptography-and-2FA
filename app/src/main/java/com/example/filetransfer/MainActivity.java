package com.example.filetransfer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.filetransfer.Internet.ConnectionLiveData;
import com.example.filetransfer.Internet.ConnectionModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import static com.example.filetransfer.Internet.ConnectionLiveData.MobileData;
import static com.example.filetransfer.Internet.ConnectionLiveData.WifiData;

public class MainActivity extends BaseActivity {

    FragmentContainerView fragmentContainerView;
    BottomNavigationView bottomNavigationView;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPref shared = new SharedPref(getApplicationContext());
        if(shared.loadNightModeState())
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
        bottomNavigationView = findViewById(R.id.bottomNavView);
        fragmentContainerView = findViewById(R.id.fragment);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());

        Snackbar snackbar = Snackbar.make(coordinatorLayout,"No Internet Connection",Snackbar.LENGTH_INDEFINITE)
                .setBehavior(new NoSwipeBehaviour());

        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getApplicationContext());
        connectionLiveData.observe( this, new Observer<ConnectionModel>() {
            @Override
            public void onChanged(@Nullable ConnectionModel connection) {
                if (connection.getIsConnected()) {
                    switch (connection.getType()) {
                        case WifiData:
                        case MobileData:
                            snackbar.dismiss();
                            break;
                    }
                } else {
                    snackbar.show();
                }
            }
        });
    }

    private class NoSwipeBehaviour extends BaseTransientBottomBar.Behavior {
        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }
}