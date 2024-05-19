package com.example.cc10624finalproject;

public class User {
    private int userId;
    private String name;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User(int userID, String name, String password) {
        this.userId = userID;
        this.password = password;
        this.name = name;
    }

    public User(String name, String password) {
        this.password = password;
        this.name = name;
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
