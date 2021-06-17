package com.example.recyclebox.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.Appconstants;
import com.example.recyclebox.Fragments.FragmentLeaderboard;
import com.example.recyclebox.R;

import java.util.ArrayList;
import java.util.List;

import com.example.recyclebox.Data_models.User;
import com.example.recyclebox.Team;
import com.example.recyclebox.TeamManager;

public class LeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<User> userList;
    Integer itemType;
    String username;
    List<Team> teamList;
    List<Integer> typeList;
    List<Integer> correctIndexes;
    int teamPos = 0, currentIndex = 0;
    TeamManager manager;

    static class ViewHolderPoints extends RecyclerView.ViewHolder {
        TextView usernameTextView, pointsTextView, positionTextView, rankTextView;
        View v;

        public ViewHolderPoints(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            usernameTextView = itemView.findViewById(R.id.textViewTeamPlayerName);
            pointsTextView = itemView.findViewById(R.id.textViewTotalTeamPoints);
            positionTextView = itemView.findViewById(R.id.textViewTeamPlayerPosition);
            rankTextView = itemView.findViewById(R.id.textViewTeamTotalPlayer);
        }

        void bind(User user, int position, String username) {
            usernameTextView.setText(user.getUsername());
            String points = user.getPoints() + "XP";
            pointsTextView.setText(points);
            positionTextView.setText(String.valueOf(position + 1));
            rankTextView.setText(user.getRank());
            switch (position) {
                case 0:
                    v.setBackgroundColor(FragmentLeaderboard.goldColour);
                    break;
                case 1:
                    v.setBackgroundColor(FragmentLeaderboard.silverColour);
                    break;
                case 2:
                    v.setBackgroundColor(FragmentLeaderboard.bronzeColour);
                    break;
                default:
                    break;
            }
            if (user.getUsername().equals(username))
                v.setBackgroundColor(FragmentLeaderboard.blueColour);
        }
    }

    static class ViewHolderTeam extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPosition, textViewTotalPoints, textViewTotalItems,
                textViewTotalPlayer;

        public ViewHolderTeam(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewTeamPlayerName);
            textViewPosition = itemView.findViewById(R.id.textViewTeamPlayerPosition);
            textViewTotalPoints = itemView.findViewById(R.id.textViewTotalTeamPoints);
            textViewTotalItems = itemView.findViewById(R.id.textViewTeamTotalRecycledCount);
            textViewTotalPlayer = itemView.findViewById(R.id.textViewTeamTotalPlayer);
        }

        void bind(Team team, int positionText) {
            textViewName.setText(team.getName());
            String totalItem = "Total Number of Items Recycled: " + team.getTotalRecycleCount();
            textViewTotalItems.setText(totalItem);
            textViewPosition.setText(String.valueOf(positionText));
            String totalPoints = "Total Points: " + team.getTotalPoints() + "XP";
            textViewTotalPoints.setText(totalPoints);
            String players = "Total Players: " + team.getTotalPlayers();
            textViewTotalPlayer.setText(players);

        }

    }

    static class ViewHolderTeamPlayer extends RecyclerView.ViewHolder {

        TextView textViewPosition, textViewUsername,  textViewPointsVal, textViewItemVal;

        public ViewHolderTeamPlayer(@NonNull View itemView) {
            super(itemView);
            textViewPosition = itemView.findViewById(R.id.textViewTeamPlayerPosition);
            textViewUsername = itemView.findViewById(R.id.textViewTeamPlayerName);
            textViewPointsVal = itemView.findViewById(R.id.textViewPointValue);
            textViewItemVal = itemView.findViewById(R.id.textViewItemValue);

        }
        void bind(User user, int postion,int totalTeamItems, int totalTeamPoints) {
            textViewUsername.setText(user.getUsername());
            textViewPosition.setText(String.valueOf(postion));
            float percentPoints = (float) (user.getPoints() * 100)/ totalTeamPoints;
            float percentItems = (float)(user.getRecycledCount() * 100) /totalTeamItems ;
            String points = (int)percentPoints + "%";
            textViewPointsVal.setText(points);
            String items = (int)percentItems + "%";
            textViewItemVal.setText(items);
        }
    }


    public LeaderboardAdapter(List<User> userList, Integer itemType, String username) {
        this.userList = userList;
        this.itemType = itemType;
        this.username = username;
    }

    public LeaderboardAdapter(List<Team> teamList, Integer itemType, List<User> userList, List<Integer> typeList
    , TeamManager manager) {
        this.teamList = teamList;
        this.itemType = itemType;
        this.userList = userList;
        this.typeList = typeList;
        this.correctIndexes = new ArrayList<>();
        this.manager = manager;
    }


    @Override
    public int getItemViewType(int position) {
        if (itemType.equals(Appconstants.teamType))
            return typeList.get(position);
        else
            return itemType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
                return new LeaderboardAdapter.ViewHolderPoints(view);
            case 10:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_team_item, parent, false);
                return new LeaderboardAdapter.ViewHolderTeam(view2);
            case 2:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_team_player_item, parent, false);
                return new LeaderboardAdapter.ViewHolderTeamPlayer(view3);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int index = 0;
        boolean indexesSet = false;
        if (itemType.equals(Appconstants.teamType)) {
            if (correctIndexes.size() == typeList.size()){
                indexesSet = true;
                index =correctIndexes.get(position);
                //User list and type list are different sizes, a new index had to be introduced to correctly map them
                //Once set they dont need to be repeated and cause problems if they are
            }
        }
        switch (holder.getItemViewType()) {
            case 1:
                //Solo Leaderboard Layout
                LeaderboardAdapter.ViewHolderPoints viewHolderPoints = (LeaderboardAdapter.ViewHolderPoints) holder;
                viewHolderPoints.bind(userList.get(position), position, username);
                break;
            case 10:
                //Team
                LeaderboardAdapter.ViewHolderTeam viewHolderTeam = (LeaderboardAdapter.ViewHolderTeam) holder;
                if (!indexesSet){
                    index = teamPos;
                    correctIndexes.add(position, teamPos);
                }
                int teamPosition = index +1;
                if(index > teamList.size()-1)
                    index = teamList.size()-1;
                viewHolderTeam.bind(teamList.get(index),teamPosition);
                if (!indexesSet)
                    teamPos++;
                break;
            case 2:
                //Team Player
                LeaderboardAdapter.ViewHolderTeamPlayer viewHolderTeamPlayer = (LeaderboardAdapter.ViewHolderTeamPlayer) holder;
                if (!indexesSet){
                    index = currentIndex;
                    correctIndexes.add(position, currentIndex);
                }
                String username = userList.get(index).getUsername();
                int positionPlayer = manager.returnPlayerPosition(username) + 1;
                int teamIndex = manager.returnTeamIndex(username);
                int totalPoints = teamList.get(teamIndex).getTotalPoints();
                int totalRecyclcedCount = teamList.get(teamIndex).getTotalRecycleCount();
                viewHolderTeamPlayer.bind(userList.get(index),positionPlayer,totalRecyclcedCount,totalPoints);
                if (!indexesSet)
                    currentIndex++;
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (itemType.equals(Appconstants.teamType))
            return typeList.size();
        else
            return userList.size();
    }
}
