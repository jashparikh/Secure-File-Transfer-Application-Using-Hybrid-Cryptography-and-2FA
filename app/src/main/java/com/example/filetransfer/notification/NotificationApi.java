package com.example.filetransfer.notification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.example.filetransfer.notification.Constants.CONTENT_TYPE;
import static com.example.filetransfer.notification.Constants.SERVER_KEY;

public interface NotificationApi {


    @Headers({"Authorization: key=" + SERVER_KEY, "Content-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<ResponseBody> postNotification(@Body PushNotification notification);
}
