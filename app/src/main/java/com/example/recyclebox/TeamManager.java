package com.example.recyclebox;

import com.example.recyclebox.Data_models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamManager {
    Map<String, Team> teamMap;
    List<Team> teamList;
    List<User> userList;

    public void sortTeamList(){
        for (Team team : teamMap.values()) {
            teamList.add(team);
        }
        //Sorts into teams by points in descending order
        Collections.sort(teamList, new SortbyPoints());
    }


    public List<Integer> returnTypeList() {
        //Creates a list of types where it goes team -> users -> next team - users
        //Sorts user list to match list
        List<Integer> list = new ArrayList<>();
        for (Team team: teamList) {
            list.add(Appconstants.teamType);
            for (User user:team.getUsers()) {
                list.add(Appconstants.teamPlayerType);
                addToSortedUserList(user);
            }
        }
        return list;
    }
    public void updateTeam(String key, User user) {
        //Updates the values for team for each user
        if (teamMap.containsKey(key))
            teamMap.get(key).increase(user);
        else {
            Team team = new Team(user);
            teamMap.put(key,team);
        }
    }

    public TeamManager() {
        this.teamMap = new HashMap<>();
        this.teamList = new ArrayList<>();
        this.userList = new ArrayList<>();
    }

    private void addToSortedUserList(User user) {
        userList.add(user);
    }

    public List<User> returnUserList() {
        return userList;
    }
    public List<Team> returnTeamList() {
        return teamList;
    }


    class SortbyPoints implements Comparator<Team> {
        // Used for sorting in descending order for total points
        public int compare(Team a, Team b) {
            return b.getTotalPoints() - a.getTotalPoints();
        }
    }

    public int returnPlayerPosition(String username) {
        for(int i=0; i < teamList.size();i++) {
            List<User> users = teamList.get(i).getUsers();
            for (int j=0; j < users.size(); j++) {
                if (users.get(j).getUsername().equals(username))
                    return j;
            }
        }
        return 0;
    }

    public int returnTeamIndex(String username) {
        for(int i=0; i < teamList.size();i++) {
            List<User> users = teamList.get(i).getUsers();
            for (int j=0; j < users.size(); j++) {
                if (users.get(j).getUsername().equals(username))
                    return i;
            }
        }
        return 0;
    }
}
