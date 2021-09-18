package com.example.filetransfer.model;

public class Receive {
    public Receive() {
    }

    String fileName;
    String fileExt;
    String fileUrl;
    String senderName;
    int count;
    String time;
    String encryptedString;
    String rsaKey;

    public Receive(String fileName, String fileExt, String fileUrl, String senderName, String time, int count, String rsaKey, String encryptedString) {
        this.encryptedString = encryptedString;
        this.rsaKey = rsaKey;
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.fileUrl = fileUrl;
        this.senderName = senderName;
        this.time = time;
        this.count = count;
    }

    public String getEncryptedString() {
        return encryptedString;
    }

    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }

    public void setRsaKey(String rsaKey) {
        this.rsaKey = rsaKey;
    }

    public String getRsaKey() {
        return rsaKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTime() {
        return time;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
