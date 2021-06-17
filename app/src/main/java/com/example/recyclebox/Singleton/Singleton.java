package com.example.recyclebox.Singleton;

import com.example.recyclebox.Data_models.Achievement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import com.example.recyclebox.Data_models.User;


public class Singleton {
    static Singleton instance;
    public static boolean loggedIn = false;
    FirebaseAuth mAuth;
    public static String userID;
    private String userDocumentID;
    private User currentUser;
    public static boolean hasCurrentDocumentID = false, hasSetFactList = false;
    private List<String> factList;
    private String username;

    public Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public Boolean checkLoggedIn() {

        if (loggedIn)
            return true;

        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            userID = user.getUid();
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void signout() {
        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
        loggedIn = false;
        hasCurrentDocumentID = false;
        hasSetFactList =false;
        currentUser = null;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
        loggedIn = true;
    }

    public String getUserDocumentID() {
        return userDocumentID;
    }

    public void setUserDocumentID(String userDocumentID) {
        this.userDocumentID = userDocumentID;
    }

    public static boolean isHasCurrentDocumentID() {
        return hasCurrentDocumentID;
    }

    public static void setHasCurrentDocumentID(boolean hasCurrentDocumentID) {
        Singleton.hasCurrentDocumentID = hasCurrentDocumentID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<String> getFactList() {
        return factList;
    }

    public void setFactList(List<String> factList) {
        this.factList = factList;
    }

    public static boolean isHasSetFactList() {
        return hasSetFactList;
    }

    public static void setHasSetFactList(boolean hasSetFactList) {
        Singleton.hasSetFactList = hasSetFactList;
    }


}
