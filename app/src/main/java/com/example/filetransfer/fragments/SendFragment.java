package com.example.filetransfer.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.amplifyframework.storage.StorageException;
import com.amplifyframework.storage.result.StorageUploadFileResult;
import com.example.filetransfer.utils.CustomEncrypt;
import com.example.filetransfer.Internet.ConnectionLiveData;
import com.example.filetransfer.Internet.ConnectionModel;
import com.example.filetransfer.R;
import com.example.filetransfer.utils.Utils;
import com.example.filetransfer.adapter.SentAdapter;
import com.example.filetransfer.model.Receive;
import com.example.filetransfer.model.Sent;
import com.example.filetransfer.model.User;
import com.example.filetransfer.notification.NotificationApi;
import com.example.filetransfer.notification.NotificationData;
import com.example.filetransfer.notification.PushNotification;
import com.example.filetransfer.notification.RetrofitInstance;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.NoSuchPaddingException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.filetransfer.Internet.ConnectionLiveData.MobileData;
import static com.example.filetransfer.Internet.ConnectionLiveData.WifiData;

public class SendFragment extends Fragment implements SentAdapter.SentInterface {

    private static final String TAG = "MY_LOG";
    private static final Object TOPIC = "/UsersTransaction";
    boolean flag;
    String srcFileName;
    String srcFilePath;
    String ext;
    FloatingActionButton sendBtn;
    String key;
    String currentUserEmail;
    String receiverKey, receiverEmail;
    Uri fileUri;
    HashMap<String, String> map;
    RecyclerView recyclerView;
    CoordinatorLayout coordinatorLayout;
    DatabaseReference databaseRef;
    SentAdapter adapter;
    String keyDES;
    String keyAES;
    String encryptedString, rsaKey;

    public SendFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        FirebaseMessaging.getInstance().subscribeToTopic("/UsersTransaction");
        recyclerView = view.findViewById(R.id.recycler_view);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        flag = false;
        key = generateKey();
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getContext());
        connectionLiveData.observe((LifecycleOwner) getContext(), new Observer<ConnectionModel>() {
            @Override
            public void onChanged(@Nullable ConnectionModel connection) {
                if (connection.getIsConnected()) {
                    switch (connection.getType()) {
                        case WifiData:
                        case MobileData:
                            sendBtn.show();
                            getAllUserDetail();
                            break;
                    }
                } else {
                    sendBtn.hide();
                }
            }
        });

        databaseRef = FirebaseDatabase.getInstance().getReference();
        sendBtn = view.findViewById(R.id.sendBtn);
        map = new HashMap<>();
        currentUserEmail = Amplify.Auth.getCurrentUser().getUserId();

        Query query = FirebaseDatabase.getInstance().getReference().child("UsersTransaction").child(Amplify.Auth.getCurrentUser().getUserId())
                .child("Sent");

        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(query, String.class)
                        .build();
        adapter = new SentAdapter(options, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        sendBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                createFolder();
                openPicker();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }

        });


        return view;
    }

    private void getAllUserDetail() {
        databaseRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    if (user != null) {
                        String key = user.getUserId();
                        String email = user.getEmail().toLowerCase();
                        map.put(email, key);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    void uploadFile() {
        File encryptedFile = new File(Environment.getExternalStorageDirectory() + "/FileTransfer/Sent/" + srcFileName + ext);
        String fileUrl = receiverKey + System.currentTimeMillis() + srcFileName;
        Amplify.Storage.uploadFile(fileUrl, encryptedFile, new Consumer<StorageUploadFileResult>() {
            @Override
            public void accept(@NonNull StorageUploadFileResult value) {
                encryptedFile.delete();
                String currentUserEmail = Amplify.Auth.getCurrentUser().getUsername();
                Receive receive = new Receive(srcFileName, ext, fileUrl, currentUserEmail, Utils.getCurrentDate(), 3, rsaKey, encryptedString);
                Sent sent = new Sent(receiverEmail, receiverKey);
                updateReceiver(receive);
                updateSender(sent);
                flag = false;
                Log.d(TAG, "success: " + value.toString());
                Snackbar.make(recyclerView, "File Sent!", Snackbar.LENGTH_LONG).show();
            }
        }, new Consumer<StorageException>() {
            @Override
            public void accept(@NonNull StorageException value) {
                Log.d(TAG, "error: " + value.toString());
                Toast.makeText(getContext(), value.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateReceiver(Receive receive) {

        FirebaseDatabase.getInstance().getReference().child("UsersTransaction")
                .child(receiverKey)
                .child("Receive")
                .push()
                .setValue(receive).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(receiverKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        String token = user.getToken();
                        PushNotification pushNotification = new PushNotification(new NotificationData("Incoming File from " + currentUserEmail, ""), token);
                        sendNotification(pushNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void updateSender(Sent sender) {
        String id = Amplify.Auth.getCurrentUser().getUserId();
        FirebaseDatabase.getInstance().getReference().child("UsersTransaction")
                .child(id)
                .child("Sent")
                .child(sender.getReceiverKey())
                .setValue(sender.getReceiverEmail());
    }


    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 100);
    }

    private void createFolder() {
        Utils.createFolder("", "FileTransfer");
        Utils.createFolder("/FileTransfer", "Sent");
        Utils.createFolder("/FileTransfer", "Received");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {


            Uri uri = data.getData();


            fileUri = uri;

            srcFileName = getFileName(uri);
            Log.d("DEBUG", getFileName(uri));

            ext = srcFileName.substring(srcFileName.lastIndexOf("."));
            srcFileName = srcFileName.substring(0, srcFileName.lastIndexOf("."));
            File targetFile = null;
            try {
                InputStream ios = getContext().getContentResolver().openInputStream(uri);
                byte[] buffer = new byte[ios.available()];
                ios.read(buffer);
                targetFile = new File(Environment.getExternalStorageDirectory() + "/FileTransfer/Sent/" + srcFileName + ext);
                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            srcFilePath = targetFile.getAbsolutePath();

            Log.d("DEBUG", srcFileName + ":" + targetFile.getAbsolutePath() + ":" + ext);
            try {
                keyAES = generateKey();
                keyDES = generateKey();
                CustomEncrypt.encryptFileWithAES(keyAES, keyDES, srcFileName, srcFilePath, ext);

                String merged = keyAES + "." + keyDES;
                Log.d("DEBUG", merged);
                encryptedString = CustomEncrypt.encryptWithRSA(merged);
                rsaKey = CustomEncrypt.getPrivateKey();

            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            if (flag) {
                showConfirmationDialog(receiverEmail);
            } else {
                showReceiverInputDialog();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    String generateKey() {
        final String random = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        System.out.println("uuid = " + random);
        System.out.println("uuid = " + random.length());
        return random;
    }

    private void deleteFileOnCancel() {
        String srcFileName = Utils.getFileName(fileUri);
        File file = new File(Environment.getExternalStorageDirectory() + "/FileTransfer/Sent/" + srcFileName);
        file.delete();
    }

    private void showReceiverInputDialog() {
        EditText editText = new EditText(getContext());
        new AlertDialog.Builder(getContext()).setTitle("Enter Receiver's Email Address")
                .setView(editText)
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFileOnCancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString().toLowerCase().trim();
                        if (input.isEmpty()) {
                            Toast.makeText(getContext(), "Please Enter Receiver's Email ID", Toast.LENGTH_SHORT).show();
                        } else {
                            if (map.containsKey(input)) {
                                receiverEmail = input;
                                receiverKey = map.get(input.toLowerCase());
                                showConfirmationDialog(receiverEmail);
                            } else {
                                deleteFileOnCancel();
                                Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .show().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                deleteFileOnCancel();
            }
        });
    }

    private void showConfirmationDialog(String email) {
        new AlertDialog.Builder(getContext())
                .setTitle("Attention")
                .setCancelable(false)
                .setMessage(srcFileName + " will be sent to " + email + ".")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFileOnCancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadFile();
                    }
                })
                .show().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                deleteFileOnCancel();
            }
        });
    }

    @Override
    public void onClick(View v, String model) {
        receiverEmail = model;
        receiverKey = map.get(model.toLowerCase());
        flag = true;
        openPicker();
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
}