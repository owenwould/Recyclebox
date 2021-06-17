package com.example.recyclebox.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.Adapters.StatsAdapter;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.recyclebox.Data_models.Achievement;
import com.example.recyclebox.Data_models.User;
import com.example.recyclebox.Singleton.Singleton;


public class FragmentStats extends Fragment {

    Button displayStatsButton, displayAchievementsButton;
    ProgressBar pbStatsLeft, pbStatsRight;
    RecyclerView recyclerView;
    StatsAdapter statsAdapter, achievementAdapter;
    RecyclerView.LayoutManager layoutManager;
    FragmentStatsListner listener;
    User currentUser;
    Singleton singleton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference achievementRef;
    List<Achievement> achievementList;
    boolean achievementsSet = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragement_stats, container, false);
        singleton = Singleton.getInstance();
        pbStatsLeft = v.findViewById(R.id.statsProgressLeft);
        pbStatsRight = v.findViewById(R.id.statsProgressRight);
        displayStatsButton = v.findViewById(R.id.buttonStats);
        displayStatsButton.setOnClickListener(onClickListener);
        displayAchievementsButton = v.findViewById(R.id.buttonAchievements);
        displayAchievementsButton.setOnClickListener(onClickListener);
        recyclerView = v.findViewById(R.id.stats_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        achievementRef = db.collection(Appconstants.ACHIEVEMENT_COLLECTION);
        currentUser = singleton.getCurrentUser();

        if (currentUser == null)
            currentUser = listener.returnCurrentUser();

        setAchievementList();
        setStatsRecycler();
        TextView title = v.findViewById(R.id.textViewPlayerName);
        title.setText(currentUser.getUsername());

        achievementList = new ArrayList<>();
        //Recycler adapter set in show stats
        showStats(true);
        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonStats:
                    showStats(true);
                    break;
                case R.id.buttonAchievements:
                    showStats(false);
                    break;
            }
        }
    };

    public interface FragmentStatsListner {
        User returnCurrentUser();
        List<Achievement> returnAchievementList();
    }

    private void setAchievementList() {
        achievementList = listener.returnAchievementList();
        achievementsSet = true;
        sortAchievements();
    }



    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        if (context instanceof FragmentStatsListner)
            listener = (FragmentStatsListner) context;
        else
            throw new RuntimeException(context.toString() + " must implement Fragment record Interface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    void setStatsRecycler() {
        List<String> valueTitles = new ArrayList<>();
        List<String> values = new ArrayList<>();
        valueTitles.add("Team");
        values.add(currentUser.getArea());
        valueTitles.add("Points");
        values.add(currentUser.getPoints().toString());
        valueTitles.add("Rank");
        values.add(currentUser.getRank());
        valueTitles.add("Total number of recycled items");
        values.add(currentUser.getRecycledCount().toString());



        for (Map.Entry<String, Integer> entry : currentUser.getRecyclables().entrySet()) {
            valueTitles.add(entry.getKey());
            values.add(entry.getValue().toString());
        }
        statsAdapter = new StatsAdapter(1, values, valueTitles);
    }

    private void sortAchievements() {
        //sort achievements by their achievement Type, effectively they're status bronze,silver gold
        boolean isSwaped = true;
        List<Achievement> list = achievementList;
        while (isSwaped)
        {
            isSwaped = false;
            for (int i = 0; i < list.size() - 1; i++)
            {
                if (list.get(i).getAchievementType() > list.get((i+1)).getAchievementType())
                {
                    Achievement temp = list.get(i);
                    list.set(i, list.get((i+1)));
                    list.set(i+1,temp);
                    isSwaped = true;
                }

            }
        }
        achievementList = list;
        setAchievementAdapter();
    }


    private void showStats(boolean bValue) {
        if (bValue) {
            pbStatsLeft.setProgress(100);
            pbStatsRight.setProgress(0);
            recyclerView.setAdapter(statsAdapter);
            displayAchievementsButton.setEnabled(true);
            displayStatsButton.setEnabled(false);
        } else {
            pbStatsRight.setProgress(100);
            pbStatsLeft.setProgress(0);
            if (!achievementsSet) {
                Toast.makeText(getContext(), "Achievement list still loading please wait a few seconds and" +
                        " try again", Toast.LENGTH_SHORT).show();
                return;
            }
            recyclerView.setAdapter(achievementAdapter);
            displayAchievementsButton.setEnabled(false);
            displayStatsButton.setEnabled(true);
        }
    }

    private void setAchievementAdapter() {
        achievementAdapter = new StatsAdapter(2,achievementList);
    }





}
