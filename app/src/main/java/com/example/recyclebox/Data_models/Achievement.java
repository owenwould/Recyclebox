package com.example.recyclebox.Data_models;

import com.google.firebase.firestore.Exclude;

public class Achievement {
    private String name;
    private String description;
    private int total;
    private int type;
    private boolean completed =false;
    private int achievementType;
    private int currentVal;


    public Achievement(String name, String description, int total, int type,int achievementType) {
        this.name = name;
        this.description = description;
        this.total = total;
        this.type = type;
        this.achievementType = achievementType;
    }

    public Achievement() {
    } //Necessary for firestore to convert document to object

    @Exclude
    public boolean isCompleted() {
        return completed;
    }
    @Exclude
    public int getCurrentVal() {
        return currentVal;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public int getAchievementType() {
        return achievementType;
    }
    public void setAchievementType(int achievementColour) {
        this.achievementType = achievementColour;
    }
    public void setCurrentVal(int currentVal) {
        this.currentVal = currentVal;
    }
}
