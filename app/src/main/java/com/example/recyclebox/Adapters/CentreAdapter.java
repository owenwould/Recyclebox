package com.example.recyclebox.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.R;

import java.util.List;

import com.example.recyclebox.Data_models.RecyclingCentre;

public class CentreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    CentreAdapter.CentreAdpaterListener listener;
    List<String> materialList;
    List<RecyclingCentre> centreList;
    int itemType;

    static class ViewHolderSearch extends RecyclerView.ViewHolder {
        TextView centreNameText;
        TextView centreAddressText;
        TextView centreMaterialSizeText;
        public ViewHolderSearch(View itemView) {
            super(itemView);
            centreNameText = itemView.findViewById(R.id.textViewCentreName);
            centreAddressText = itemView.findViewById(R.id.textViewCentreAddress);
            centreMaterialSizeText = itemView.findViewById(R.id.textViewMaterialCount);
        }
        public void bind(RecyclingCentre centre) {
            String name = centre.getName();
            centreNameText.setText(name);
            String address = centre.getAddress() + " " + centre.getPostcode();
            centreAddressText.setText(address);
            String materialCount = "Materials:" + centre.getMaterials().size();
            centreMaterialSizeText.setText(materialCount);
        }
    }
    static class ViewHolderDetail extends RecyclerView.ViewHolder {
        TextView materialTextView;

        public ViewHolderDetail(View itemView) {
            super(itemView);
            materialTextView = itemView.findViewById(R.id.textViewMaterial);
        }
        public void bind(String material) {
            materialTextView.setText(material);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //TypeList vals will be set too 0 or 2
        return itemType;

    }
    @Override
    public int getItemCount() {
        if (centreList != null) {
            return centreList.size();
        } else {
            return materialList.size();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.centre_recyclerview_item, parent, false);
                return new CentreAdapter.ViewHolderSearch(view);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.centre_material_item, parent, false);
                return new CentreAdapter.ViewHolderDetail(view2);
            default:
                return null;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                CentreAdapter.ViewHolderSearch viewHolderSearch = (CentreAdapter.ViewHolderSearch) holder;
                viewHolderSearch.bind(centreList.get(position));
                viewHolderSearch.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.showCentreDetail(centreList.get(position));
                        //When a layout within the recycler view is clicked it calls listener
                        //implemented in search fragment
                    }
                });
                break;
            case 2:
                CentreAdapter.ViewHolderDetail viewHolderDetail = (CentreAdapter.ViewHolderDetail) holder;
                viewHolderDetail.bind(materialList.get(position));
                break;

        }
    }
    public CentreAdapter(List<RecyclingCentre> centreList, Integer itemType) {
        this.centreList = centreList;
        this.itemType = itemType;
    }
    public CentreAdapter(List<String> materialList, Integer itemType, Boolean b) {
        this.materialList = materialList;
        this.itemType = itemType;
    }



    public void setListener(CentreAdpaterListener listener) {
        this.listener = listener;
    }

    public interface CentreAdpaterListener {
        void showCentreDetail(RecyclingCentre centre);
    }
}
