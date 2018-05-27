package com.mad.firebasepractice;

public class User {

    public String userId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId) {
        this.userId = userId;
    }

}