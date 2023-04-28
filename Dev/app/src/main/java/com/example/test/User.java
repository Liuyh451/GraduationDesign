package com.example.test;

public class User {
    private String username;
    private int userId;
    private String password;
    private String avatar;


    public User(String username, String password, String avatar, int userId) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.userId = userId;
    }

    // getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
