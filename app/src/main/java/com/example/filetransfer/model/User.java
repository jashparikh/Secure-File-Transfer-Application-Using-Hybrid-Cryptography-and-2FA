package com.example.filetransfer.model;

public class User {

    String token;
    String email;
    String userId;
    String password;

    public User() {
    }

    public User(String token,String email, String userId, String password) {
        this.email = email;
        this.token = token;
        this.userId = userId;
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
