package com.example.filetransfer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.filetransfer.model.User;
import com.example.filetransfer.utils.CustomEncrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignInActivity extends BaseActivity {
    private static final String TAG = "SignInActivity";
    TextView forgetPassBtn, registerBtn, warning;
    Button logInBtn;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FileTransferNoActionBar);
        setContentView(R.layout.activity_sign_in);


        warning = findViewById(R.id.warning);
        forgetPassBtn = findViewById(R.id.forgetPass);
        registerBtn = findViewById(R.id.registerBtn);
        logInBtn = findViewById(R.id.loginBtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        forgetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetActivity.class));
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    warning.setVisibility(View.VISIBLE);
                    Amplify.Auth.signIn(
                            email.getText().toString(),
                            password.getText().toString(),
                            new Consumer<AuthSignInResult>() {
                                @Override
                                public void accept(@NonNull AuthSignInResult value) {
                                    Log.d(TAG, "success" + value.toString());
                                    if (value.isSignInComplete()) {
                                        updateUserDetail(email.getText().toString(), password.getText().toString());

                                    }
                                }
                            }, new Consumer<AuthException>() {
                                @Override
                                public void accept(@NonNull AuthException value) {
                                    Log.d(TAG, "error: " + value.getLocalizedMessage());
                                    if (value.getLocalizedMessage().equals("User not found in the system.")) {
                                        Snackbar.make(email, "This email ID is not registered with us", Snackbar.LENGTH_LONG).setAction("Register Now", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                goToRegister();
                                            }
                                        }).show();
                                    } else {
                                        warning.setText(R.string.SignIN_IncorrectUser_or_Pwd);
                                    }
                                }
                            }
                    );
                }
            }
        });

    }

    private void updateUserDetail(String email, String password) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {

                User user = new User(String.valueOf(task.getResult()), email.trim().toLowerCase(), Amplify.Auth.getCurrentUser().getUserId(), CustomEncrypt.hashWithSHA256(password));
                Log.d("DEBUG", task.getResult());
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(Amplify.Auth.getCurrentUser().getUserId())
                        .setValue(user);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }


    private boolean validateInput() {
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            warning.setVisibility(View.VISIBLE);

            warning.setText("Email ID and Password Cannot be Blank");
            return false;
        } else {
            warning.setVisibility(View.GONE);
            return true;
        }
    }


    void goToRegister() {
        warning.setText("");
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    protected void onStart() {
        if (Amplify.Auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        super.onStart();
    }
}
