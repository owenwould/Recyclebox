package com.example.recyclebox;

import com.example.recyclebox.Data_models.User;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private int totalPoints;
    private int totalRecycleCount;
    private int totalPlayers;
    private List<User> users;

    public Team(User user) {
        this.name = user.getArea();
        this.totalPoints = user.getPoints();
        this.totalRecycleCount = user.getRecycledCount();
        this.totalPlayers = 1;
        this.users = new ArrayList<>();
        users.add(user);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTotalPoints() {
        return totalPoints;
    }
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getTotalRecycleCount() {
        return totalRecycleCount;
    }

    public void setTotalRecycleCount(int totalRecycleCount) {
        this.totalRecycleCount = totalRecycleCount;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    private void increasePoints(int additional) {
        totalPoints += additional;
    }
    private void increaseRecycleCount(int count){
        totalRecycleCount += count;
    }
    private void increasePlayer() {
        totalPlayers++;
    }

    public void increase(User user) {
        increasePoints(user.getPoints());
        increaseRecycleCount(user.getRecycledCount());
        increasePlayer();
        users.add(user);
    }
    public List<User> getUsers() {
        return users;
    }
}
