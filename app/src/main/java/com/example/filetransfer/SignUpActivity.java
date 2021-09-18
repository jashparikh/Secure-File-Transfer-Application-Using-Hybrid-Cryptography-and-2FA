package com.example.filetransfer;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.amazonaws.mobile.client.results.SignUpResult;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;

import java.util.regex.Pattern;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignUpActivity";
    EditText email, pass1, pass2, otp;
    TextView warning, otp_warning, notify;
    TextView loginBtn;
    Button getOtp;
    Button register;
    LinearLayout otp_container, registration_container;


    MutableLiveData<AuthException> exceptionLiveData;
    MutableLiveData<AuthSignUpResult> signUpResultLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FileTransferNoActionBar);
        setContentView(R.layout.activity_sign_up);

        signUpResultLiveData = new MutableLiveData<>();
        exceptionLiveData = new MutableLiveData<>();

        loginBtn = findViewById(R.id.loginBtn);
        email = findViewById(R.id.email);
        notify = findViewById(R.id.notify);
        otp_container = findViewById(R.id.otp_container);
        registration_container = findViewById(R.id.registration_container);

        pass1 = findViewById(R.id.password1);
        pass2 = findViewById(R.id.password2);
        otp = findViewById(R.id.otp);
        warning = findViewById(R.id.warning);
        otp_warning = findViewById(R.id.otp_warning);

        getOtp = findViewById(R.id.getOtp);
        register = findViewById(R.id.register);

        signUpResultLiveData.observe(this, new Observer<AuthSignUpResult>() {
            @Override
            public void onChanged(AuthSignUpResult signUpResult) {
                if (signUpResult.isSignUpComplete()) {
                    otp_container.setVisibility(View.VISIBLE);
                    registration_container.setVisibility(View.INVISIBLE);
                    notify.setText("A verification email has been sent to " + email.getText().toString());
                    loginBtn.setVisibility(View.GONE);


                } else {
                    otp_container.setVisibility(View.GONE);
                    registration_container.setVisibility(View.VISIBLE);
                }
            }
        });
        exceptionLiveData.observe(this, new Observer<AuthException>() {
            @Override
            public void onChanged(AuthException e) {
                warning.setText(e.getLocalizedMessage());
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }
        });


        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                String pass1Str = pass1.getText().toString();
                String pass2Str = pass2.getText().toString();

                if (emailStr.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter a Valid Email ID", Toast.LENGTH_LONG).show();
                } else if (isPasswordValid(pass1Str, pass2Str)) {
                    sendOtp(emailStr, pass1Str);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().isEmpty()) {
                    warning.setText("Email ID Cannot be Blank");
                } else if (!email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    warning.setText("Invalid Email");
                } else if (otp.getText().toString().isEmpty()) {
                    warning.setText("OTP Cannot be Blank");
                } else {
                    warning.setText("");
                    verifyOtp(email.getText().toString(), otp.getText().toString());
                }
            }
        });
    }

    private void verifyOtp(String email, String otp) {

        Amplify.Auth.confirmSignUp(
                email,
                otp,
                new Consumer<AuthSignUpResult>() {
                    @Override
                    public void accept(@NonNull AuthSignUpResult value) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d(TAG, "Succcess: " + value);
                                if (value.isSignUpComplete()) {
                                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                }
                            }
                        });
                    }
                }, new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        Log.d("Failed", value.toString());
                        otp_warning.setText(value.getLocalizedMessage());
                    }
                }//delete
        );
    }

    private void sendOtp(String email, String pass) {
        Amplify.Auth.signUp(
                email,
                pass,
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), email).build(),
                new Consumer<AuthSignUpResult>() {
                    @Override
                    public void accept(@NonNull AuthSignUpResult value) {
                        Log.d("Success", value.toString());
                        signUpResultLiveData.postValue(value);
                    }
                },
                new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        exceptionLiveData.postValue(value);
                    }
                }
        );
    }

    boolean isPasswordValid(String pass1, String pass2) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        if (pass1.length() < 8 && pass2.length() < 8) {
            warning.setText("Password must be at least 8 characters in length");
            return false;
        }
        if (!pass1.equals(pass2)) {
            warning.setText("Passwords do not match");
            return false;
        }
        if (pass1.matches(regex) && pass1.matches(regex)) {
            warning.setText("");

            return true;
        } else {
            warning.setText("Password should contain at least:\n1 special symbol\n1 upper case character\n1 number\n 1 lowercase character");
            return false;
        }
    }
}
