package com.example.filetransfer.model;

public class Sent {

    String receiverEmail;
    String receiverKey;

    public Sent(String receiverEmail, String receiverKey) {
        this.receiverKey = receiverKey;
        this.receiverEmail = receiverEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getReceiverKey() {
        return receiverKey;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public void setReceiverKey(String receiverKey) {
        this.receiverKey = receiverKey;
    }

    @Override
    public String toString() {
        return "Sent{" +
                "receiverEmail='" + receiverEmail + '\'' +
                ", receiverKey='" + receiverKey + '\'' +
                '}';
    }
}
