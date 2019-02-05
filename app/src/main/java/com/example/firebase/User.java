package com.example.firebase;

public class User {
    private String userName;
    private String userEmail;
    private String key;

    public User() {
    }

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public User(String userName, String userEmail, String key) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.key = key;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
