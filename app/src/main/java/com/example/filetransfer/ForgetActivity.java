package com.example.filetransfer;

import android.content.Intent;
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

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.core.Action;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.google.android.material.snackbar.Snackbar;

import static com.amplifyframework.auth.result.step.AuthResetPasswordStep.CONFIRM_RESET_PASSWORD_WITH_CODE;

public class ForgetActivity extends BaseActivity {

    private static final String TAG = "DEBUG";
    LinearLayout resetContainer, confirmContainer;
    Button resetBtn, confirmBtn;
    EditText email, pass1, pass2, otp;
    TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        email = findViewById(R.id.email);
        resetBtn = findViewById(R.id.reset);
        confirmBtn = findViewById(R.id.confirmBtn);
        resetContainer = findViewById(R.id.resetContainer);
        confirmContainer = findViewById(R.id.confirmContainer);
        pass1 = findViewById(R.id.password1);
        pass2 = findViewById(R.id.password2);
        otp = findViewById(R.id.otp);
        warning = findViewById(R.id.warning);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                if (!emailStr.isEmpty()) {
                    Amplify.Auth.resetPassword(
                            emailStr,
                            new Consumer<AuthResetPasswordResult>() {
                                @Override
                                public void accept(@NonNull AuthResetPasswordResult value) {
                                    Log.d(TAG, "accept: " + value.toString());
                                    if (value.getNextStep().getResetPasswordStep() == CONFIRM_RESET_PASSWORD_WITH_CODE) {
                                        Snackbar.make(resetBtn,"A Verification OTP has been sent to " + emailStr,Snackbar.LENGTH_LONG).show();

                                        resetContainer.setVisibility(View.GONE);
                                        confirmContainer.setVisibility(View.VISIBLE);
                                    }
                                }
                            }, new Consumer<AuthException>() {
                                @Override
                                public void accept(@NonNull AuthException value) {
                                    Log.d(TAG, "accept: " + value.toString());
                                }
                            }
                    );
                } else {
                    Toast.makeText(ForgetActivity.this, "PLease Enter a Valid Email Address ", Toast.LENGTH_LONG).show();
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1Str = pass1.getText().toString();
                String pass2Str = pass2.getText().toString();
                String otpStr = otp.getText().toString();

                if (otpStr.length() < 6) {
                    warning.setText("Enter the OTP sent to your registered Email ID ");
                } else if (isPasswordValid(pass1Str, pass2Str)) {
                    confirmReset(otpStr, pass1Str);
                }
            }
        });

    }

    private void confirmReset(String otpStr, String pass1Str) {
        Amplify.Auth.confirmResetPassword(
                pass1Str,
                otpStr,
                new Action() {
                    @Override
                    public void call() {
                        Snackbar.make(resetBtn,"Password Reset Successfully",Snackbar.LENGTH_LONG).show();
                        startActivity(new Intent(ForgetActivity.this, SignInActivity.class));
                    }
                }, new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        warning.setText(value.getLocalizedMessage());
                    }
                }
        );
    }

    boolean isPasswordValid(String pass1, String pass2) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        if (pass1.length() < 8 && pass2.length() < 8) {
            warning.setText("Password must be at least 8 characters in length ");
            return false;
        }
        if (!pass1.equals(pass2)) {
            warning.setText("Passwords do not match ");
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