package com.example.recyclebox.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.R;

import java.util.List;

public class AdditionalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> materialList,binList;
    List<Integer> colourList;
    int itemType;

    static class ViewHolderPlaces extends RecyclerView.ViewHolder {
        TextView material, materialBin;
        View v;
        public ViewHolderPlaces(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            material = itemView.findViewById(R.id.textViewMaterial);
            materialBin = itemView.findViewById(R.id.textViewMaterialBin);
        }

        void bind(String sMaterial, String bin,int colour) {
            material.setText(sMaterial);
            materialBin.setText(bin);
            v.setBackgroundColor(colour);
        }
    }

    public AdditionalAdapter(List<String> material,List<String> bin,int itemType,List<Integer> colourList ) {
        this.materialList = material;
        this.binList = bin;
        this.itemType = itemType;
        this.colourList = colourList;
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.additional_item, parent, false);
                return new AdditionalAdapter.ViewHolderPlaces(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 1:
                AdditionalAdapter.ViewHolderPlaces viewHolderPoints = (AdditionalAdapter.ViewHolderPlaces) holder;
                viewHolderPoints.bind(materialList.get(position),binList.get(position),colourList.get(position));
                break;
        }
    }
    @Override
    public int getItemCount() {
        return materialList.size();
    }
}
