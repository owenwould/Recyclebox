package com.example.recyclebox.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.R;

import java.util.List;

import com.example.recyclebox.Data_models.Achievement;

import static java.lang.String.valueOf;

public class StatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int itemType;
    List<String> valuesList;
    List<String> valueTitleList;
    List<Achievement> achievementList;
    static class ViewHolderStats extends RecyclerView.ViewHolder {
        TextView statsTextView;
        public ViewHolderStats(@NonNull View itemView) {
            super(itemView);
            statsTextView = itemView.findViewById(R.id.stats_item_text);
        }

        void bind(String sTitle, String sValue) {
            String val = sTitle + ": " + sValue;
            statsTextView.setText(val);
        }
    }

    static class ViewHolderAchievement extends RecyclerView.ViewHolder {

        TextView name,desc,currentProgres;
        ProgressBar progressBar;
        public ViewHolderAchievement(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewAchievementName);
            desc = itemView.findViewById(R.id.textViewAchievementDesc);
            currentProgres = itemView.findViewById(R.id.textViewAchievementProgress);
            progressBar = itemView.findViewById(R.id.achievement_progressbar);
        }
        void bind(Achievement achievement) {
            name.setText(achievement.getName());
            progressBar.setMax(achievement.getTotal());
            progressBar.setProgress(achievement.getCurrentVal());
            desc.setText(achievement.getDescription());
            String currentProgress;
            int current = achievement.getCurrentVal();
            int total = achievement.getTotal();
            if (current >= total) { currentProgress = "Completed"; }
            else { currentProgress = current + "/" + total ; }
            currentProgres.setText(currentProgress);
        }
    }

    public StatsAdapter(int itemType, List<String> valuesList, List<String> valueTitleList) {
        this.itemType = itemType;
        this.valuesList = valuesList;
        this.valueTitleList = valueTitleList;
    }
    public StatsAdapter(int itemType, List<Achievement> achievementList) {
        this.itemType = itemType;
        this.achievementList = achievementList;
    }



    @Override
    public int getItemViewType(int position) {
        return itemType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_item, parent, false);
                return new StatsAdapter.ViewHolderStats(view);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item,parent,false);
                return new StatsAdapter.ViewHolderAchievement(view2);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 1:
                StatsAdapter.ViewHolderStats viewHolderPoints = (StatsAdapter.ViewHolderStats) holder;
                viewHolderPoints.bind(valueTitleList.get(position),valuesList.get(position));
                break;
            case 2:
                StatsAdapter.ViewHolderAchievement viewHolderAchievement = (StatsAdapter.ViewHolderAchievement) holder;
                viewHolderAchievement.bind(achievementList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (valueTitleList != null) {
            return valueTitleList.size();
        }
        else {
            return achievementList.size();
        }

    }
}
