package com.example.recyclebox.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.Adapters.LeaderboardAdapter;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.R;
import com.example.recyclebox.Team;
import com.example.recyclebox.TeamManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.recyclebox.Data_models.User;
import com.example.recyclebox.Singleton.Singleton;


public class FragmentLeaderboard extends Fragment {
    RecyclerView recyclerView;
    LeaderboardAdapter adapterSolo,adapterTeam;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef;
    Context context;
    public static int goldColour,silverColour,bronzeColour,blueColour;
    Singleton singleton;
    TextView averageTextView,motivationTextView;
    final int leaderboardPointsType = 1;
    boolean isShowingSolo = true;
    Button SoloButton, TeamButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        userRef = db.collection("users");
        singleton = Singleton.getInstance();
        context = getContext();
        goldColour  = ContextCompat.getColor(context, R.color.colourGold);
        silverColour = ContextCompat.getColor(context, R.color.colourSilver);
        bronzeColour =  ContextCompat.getColor(context, R.color.colourBronze);
        blueColour = ContextCompat.getColor(context,R.color.colourBlue);
        setleaderboardList();
        View v = inflater.inflate(R.layout.fragement_leaderboard, container, false);
        recyclerView = v.findViewById(R.id.leaderboard_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        averageTextView = v.findViewById(R.id.textViewLeaderboard_average);
        motivationTextView = v.findViewById(R.id.textViewLeaderboard_motivation_text);
        setButtons(v);
        return v;
    }

    private void setUpRecyclerView(List<User> userList) {
        adapterSolo = new LeaderboardAdapter(userList, leaderboardPointsType,singleton.getUsername());
        recyclerView.setAdapter(adapterSolo);
        setAverage(userList);
        setMotivation(userList);
        setTeams(userList);
    }

    private void setleaderboardList() {
        userRef.orderBy(Appconstants.POINTS_KEY, Query.Direction.DESCENDING).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentList = new ArrayList<>(task.getResult().getDocuments());
                            List<User> userList = new ArrayList<>();
                            for (DocumentSnapshot document : documentList) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            setUpRecyclerView(userList);
                        }
                    }
                });
    }


    private void setAverage(List<User> userList) {
        int iTotal = 0;
        for (User user:userList) {
            iTotal += user.getRecycledCount();
        }
        int average = iTotal/userList.size();
        int recycleCount = singleton.getCurrentUser().getRecycledCount();
        if (recycleCount >= average)
            averageTextView.setText("Average number of items recycled: " + average + "." +"\n" + " Well done you have recycled " + recycleCount + " items" );
        else {
            int difference = average - recycleCount;
            averageTextView.setText("Average number of items recycled: " + average + "." + "\n" +
                    "Recycle " + difference + " more items to recycle more than the average" );
        }
    }
    private void setMotivation(List<User> userList) {
        for (int i=0; i < userList.size();i++) {
            if (userList.get(i).getUserID().equals(singleton.getUserID())) {
                if (i < 1) {
                    //Already leader
                    User current = userList.get(0);
                    String message = "Well done" + current.getUsername() +  " you are at the top of the leaderboard ";
                    motivationTextView.setText(message);
                    break;
                }
                else {
                    User current = userList.get(i);
                    User targetUser = userList.get(i-1);
                    int difference = targetUser.getPoints() - current.getPoints();
                    int amount = (difference / 10) + 1;
                    String message = "Recycle " + amount + " more items to overtake player above you.";
                    motivationTextView.setText(message);
                    break;
                }
            }
        }
    }


    private void setTeams(List<User> userList) {
        TeamManager teamManager = new TeamManager();
        for (int i=0; i < userList.size();i++) {
            User user = userList.get(i);
            String sTeam = user.getArea();
            teamManager.updateTeam(sTeam,user);
        }
        //Keep order of functions the same,team list must be sorted first, type list must be set for
        //user list also
        teamManager.sortTeamList();
        List<Team> teamList = teamManager.returnTeamList();
        List<Integer> typeList = teamManager.returnTypeList();
        List<User> reSortedUserList = teamManager.returnUserList();
        adapterTeam = new LeaderboardAdapter(teamList,Appconstants.teamType,reSortedUserList,typeList,teamManager);
    }


    private void setButtons(View v) {
        SoloButton = v.findViewById(R.id.leaderboard_button_solo);
        SoloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swtichLeaderboard();
            }
        });
        TeamButton = v.findViewById(R.id.leaderboard_button_team);
        TeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swtichLeaderboard();
            }
        });
        SoloButton.setEnabled(false);
        TeamButton.setEnabled(true);
    }

    private void swtichLeaderboard() {

        if (isShowingSolo) {
            SoloButton.setEnabled(true);
            TeamButton.setEnabled(false);
            recyclerView.setAdapter(adapterTeam);
            isShowingSolo = false;
        }
        else{
            SoloButton.setEnabled(false);
            TeamButton.setEnabled(true);
            recyclerView.setAdapter(adapterSolo);
            isShowingSolo = true;
        }

    }








}
