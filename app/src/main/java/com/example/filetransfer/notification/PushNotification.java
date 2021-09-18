package com.example.filetransfer.notification;

public class PushNotification {
    NotificationData data;
    String to;


    PushNotification() {

    }

    public PushNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public String getTo() {
        return to;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
