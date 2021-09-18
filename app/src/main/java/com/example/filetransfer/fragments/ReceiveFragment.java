package com.example.filetransfer.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.amplifyframework.storage.StorageException;
import com.amplifyframework.storage.result.StorageDownloadFileResult;
import com.amplifyframework.storage.result.StorageRemoveResult;
import com.example.filetransfer.utils.CustomEncrypt;
import com.example.filetransfer.R;
import com.example.filetransfer.utils.Utils;
import com.example.filetransfer.adapter.ReceiveAdapter;
import com.example.filetransfer.model.Receive;
import com.example.filetransfer.model.User;
import com.example.filetransfer.notification.NotificationApi;
import com.example.filetransfer.notification.NotificationData;
import com.example.filetransfer.notification.PushNotification;
import com.example.filetransfer.notification.RetrofitInstance;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiveFragment extends Fragment implements ReceiveAdapter.ReceiverInterface {

    String TAG = "DEBUG";
    RecyclerView recyclerView;
    String userHashPWD;
    Button downloadBtn;
    ReceiveAdapter adapter;
    String token, OTP;

    public ReceiveFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recieve, container, false);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Amplify.Auth.getCurrentUser().getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    User user = snapshot.getValue(User.class);
                    userHashPWD = user.getPassword();
                    token = user.getToken();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        downloadBtn = view.findViewById(R.id.downloadBtn);
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("UsersTransaction")
                .child(Amplify.Auth.getCurrentUser().getUserId())
                .child("Receive");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Log.d(TAG, "onChildAdded:" + dataSnapshot.toString());
                Log.d(TAG, "onChildAdded:" + previousChildName);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        query.addChildEventListener(childEventListener);
        FirebaseRecyclerOptions<Receive> options = new FirebaseRecyclerOptions.Builder<Receive>()
                        .setQuery(query, Receive.class)
                        .build();
        adapter = new ReceiveAdapter(options, (ReceiveAdapter.ReceiverInterface) this);
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        Log.d("MY_LOG", Amplify.Auth.getCurrentUser().getUsername());

        return view;
    }

    @Override
    public void onClick(DatabaseReference ref, View v, Receive model) {
        showOtpInputDialog(ref, model);
    }

    private void showOtpInputDialog(DatabaseReference ref, Receive model) {
        OTP = Utils.generateOTP();
        PushNotification pushNotification = new PushNotification(new NotificationData("OTP to Download File", OTP), token);
        sendNotification(pushNotification);
        EditText editText = new EditText(getContext());
        new AlertDialog.Builder(getContext()).setTitle("Enter OTP")

                .setView(editText)
                .setMessage("Check Notifications for OTP")
                .setCancelable(false)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input_otp = editText.getText().toString();
                        if (input_otp.length() != 6) {
                            Toast.makeText(getContext(), "OTP is a 6 digit number", Toast.LENGTH_SHORT).show();
                        } else {
                            if (OTP.equals(input_otp)) {
                                showPasswordInputDialog(ref, model);
                            } else {
                                Toast.makeText(getContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void showPasswordInputDialog(DatabaseReference ref, Receive model) {
        EditText editText = new EditText(getContext());
        editText.setText("");
        new AlertDialog.Builder(getContext()).setTitle("Confirm Your Account Password")
                .setCancelable(false)
                .setView(editText)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = editText.getText().toString();
                        if (!pwd.isEmpty() && userHashPWD != null) {
                            String hashed = CustomEncrypt.hashWithSHA256(pwd);
                            Log.d("DEBUG", hashed);
                            Log.d("DEBUG", userHashPWD);
                            if (userHashPWD.equals(hashed)) {
                                if (model.getCount() > 0) {
                                    Snackbar.make(recyclerView, "Downloading Started", Snackbar.LENGTH_LONG).show();
                                    downloadFile(model);
                                } else {
                                    Snackbar.make(recyclerView, "File was Destroyed.", Snackbar.LENGTH_LONG).show();
                                }
                            } else {
                                //Increment count for incorrect password;
                                int count = model.getCount();
                                if (count == 0) {
                                    Snackbar.make(recyclerView, "Too Many Attempts. File Destroyed.", Snackbar.LENGTH_LONG).show();
                                    deleteFile(model.getFileUrl());
                                } else {
                                    count--;
                                    Snackbar.make(recyclerView, "Incorrect Password. Tries Remaining: " + count, Snackbar.LENGTH_LONG).show();
                                    ref.child("count").setValue(count);
                                }
                            }
                        }
                    }
                })
                .show();
    }

    private void deleteFile(String fileUrl) {
        Amplify.Storage.remove(fileUrl, new Consumer<StorageRemoveResult>() {
            @Override
            public void accept(@NonNull StorageRemoveResult value) {
                Log.d("DEBUG", value.toString());
            }
        }, new Consumer<StorageException>() {
            @Override
            public void accept(@NonNull StorageException value) {
                Log.d("DEBUG", value.toString());
            }
        });
    }

    void downloadFile(Receive model) {
        File downloadFile = new File(Environment.getExternalStorageDirectory() + "/FileTransfer/Received/" + model.getFileName() + model.getFileExt());
        Amplify.Storage.downloadFile(
                model.getFileUrl(),
                downloadFile,
                new Consumer<StorageDownloadFileResult>() {
                    @Override
                    public void accept(@NonNull StorageDownloadFileResult value) {
                        Snackbar.make(recyclerView, "File Downloading", Snackbar.LENGTH_LONG).show();
                        decryptFile(downloadFile, model);
                    }
                }, new Consumer<StorageException>() {
                    @Override
                    public void accept(@NonNull StorageException value) {
                        Toast.makeText(getContext(), value.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void decryptFile(File downloadFile, Receive model) {
        String rsaKey = model.getRsaKey();
        String encryptedString = model.getEncryptedString();
        String merged = null;
        try {
            merged = CustomEncrypt.decryptWithRSA(encryptedString, rsaKey);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        if (merged != null) {
            String aesKey = merged.substring(0, 16);
            String desKey = merged.substring(17);
            try {
                CustomEncrypt.decryptFileWithDES(aesKey, desKey, model.getFileName(), downloadFile.getAbsolutePath(), model.getFileExt());
                Snackbar.make(recyclerView, "File Decrypted & Downloaded ", Snackbar.LENGTH_LONG).setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFolder();
                    }
                }).show();
            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void sendNotification(PushNotification notification) {
        try {
            NotificationApi response = RetrofitInstance.getClient().create(NotificationApi.class);
            Call<ResponseBody> call = response.postNotification(notification);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("DEBUG", response.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("DEBUG", t.getLocalizedMessage());
                }
            });

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public void openFolder() {

        String path = Environment.getExternalStorageDirectory() + "/FileTransfer/Received/";
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(uri, "*/*");
        getContext().startActivity(intent);
    }
}