package com.example.recyclebox.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.Adapters.CentreAdapter;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.recyclebox.Data_models.RecyclingCentre;


public class FragmentSearch extends Fragment implements CentreAdapter.CentreAdpaterListener {
    EditText eSearchbar;
    Group searchGroup,detailGroup;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference centreRef = db.collection(Appconstants.COLLECTION_CENTRE_KEY);
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView nameTextView,addressTextView,materialCountTextView;
    List<RecyclingCentre> currentCentreList;
    final int moreDetailType = 2, searchType = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragement_search, container, false);
        eSearchbar = v.findViewById(R.id.search_searchbar);
        ImageButton searchButton = v.findViewById(R.id.search_searchbutton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        recyclerView = v.findViewById(R.id.centre_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        searchGroup = v.findViewById(R.id.search_default_group);
        detailGroup = v.findViewById(R.id.addition_info_group);
        nameTextView = v.findViewById(R.id.search_textView_Name);
        addressTextView = v.findViewById(R.id.search_textView_Address);
        materialCountTextView = v.findViewById(R.id.search_textView_materialCount);
        detailGroup.setVisibility(View.GONE);
        searchGroup.setVisibility(View.VISIBLE);
        final Button goBackButton = v.findViewById(R.id.search_goback_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToResults();

            }
        });


        return v;
    }


    private void search() {

        centreRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<RecyclingCentre> centreList = new ArrayList<>(task.getResult().size());
                    for (DocumentSnapshot centreDocument : task.getResult().getDocuments()) {
                        centreList.add(centreDocument.toObject(RecyclingCentre.class));
                    }
                    displayResults(centreList);
                    currentCentreList = new ArrayList<>(centreList);
                }
            }
        });
    }

    private void displayResults(List<RecyclingCentre> centres) {
        if (detailGroup.getVisibility() == View.VISIBLE)
            detailGroup.setVisibility(View.GONE);

        searchGroup.setVisibility(View.VISIBLE);
        CentreAdapter adapter = new CentreAdapter(centres,searchType);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showCentreDetail(RecyclingCentre centre) {

        detailGroup.setVisibility(View.VISIBLE);
        List<String> materialList = new ArrayList<>(centre.getMaterials());
        CentreAdapter adapter = new CentreAdapter(materialList,moreDetailType,false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        searchGroup.setVisibility(View.GONE);
        String name = centre.getName();
        String address = centre.getAddress() + " " + centre.getPostcode();
        nameTextView.setText(name);
        addressTextView.setText(address);
        String materialCount = "Materials:" + centre.getMaterials().size();
        materialCountTextView.setText(materialCount);

    }
    private void goBackToResults() {
        if (currentCentreList != null)
            displayResults(currentCentreList);
    }

}
