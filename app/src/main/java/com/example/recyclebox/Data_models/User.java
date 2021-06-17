package com.example.recyclebox.Data_models;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username,rank;
    private Integer recycledCount,points;
    private String userID,documentID;
    private Map<String,Integer> recyclables;
    private String area;
    public User (){}

    public User(String userID,String username,String area) {
        this.userID = userID;
        this.points =0;
        this.rank = "Bronze";
        this.recycledCount =0;
        this.username = username;
        this.recyclables = new HashMap<>();
        this.area = area;
    }
    @Exclude
    public String getDocumentID() {
        return documentID;
    }
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
    public Map<String, Integer> getRecyclables() {
        return recyclables;
    }
    public String getUsername() {
        return username;
    }
    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public Integer getRecycledCount() {
        return recycledCount;
    }
    public void setRecycledCount(Integer recycledCount) {
        this.recycledCount = recycledCount;
    }
    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setRecyclables(Map<String, Integer> recyclables) {
        this.recyclables = recyclables;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
}
