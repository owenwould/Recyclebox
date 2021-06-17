package com.example.recyclebox.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclebox.Adapters.AdditionalAdapter;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentAdditional extends Fragment {
    Context context;
    TextView textViewTitle,textViewAdditionInfo;
    RecyclerView rc;
    RecyclerView.LayoutManager manager;
    AdditionalAdapter adapter;
    String sAccom = "Student Accommodation Recycling",sUni = "University of Sunderland Recycling",sCouncil = "Sunderland Council Recycling";
    List<String> materialList,binList;
    List<Integer> colourList;

    int type;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragement_additional, container, false);
        rc = v.findViewById(R.id.addition_recycler);
        textViewTitle = v.findViewById(R.id.textViewAdditional_title);
        textViewAdditionInfo = v.findViewById(R.id.textView_Additional_Basic);
        manager = new LinearLayoutManager(getContext());
        rc.setLayoutManager(manager);
        context = getContext();
        materialList = new ArrayList<>();
        binList = new ArrayList<>();
        colourList = new ArrayList<>();

        switch (type) {
            case Appconstants.UNIVERSITY_CODE:
                setUniversity();
                break;
            case Appconstants.SUNDERLAND_COUNCIL_CODE:
                setSunderlandCouncil();
                break;
            case Appconstants.ACCOMMODATION_CODE:
                setStudentAccommodation();
                break;
        }
        return v;
    }
    void setUniversity() {
        textViewTitle.setText(sUni);
        String sAdditionalInfo = "Please take care when choosing paper recycling bin as green bins are only for white paper." +
                "Bins can be found all over the university buildings, water fountains are also present. Bringing a reusable cup to the cafes " +
                "rather than using a single use cup reduces the price of the coffee by 20p.";
        textViewAdditionInfo.setText(sAdditionalInfo);
        setLists(Appconstants.plasticBottles,"Red Plastic bin",Appconstants.returnColour(context,R.color.colourPlastic));
        setLists(Appconstants.paper, "Blue/Green Paper bin",Appconstants.returnColour(context,R.color.colourCardboard));//Paper and cardboard have same colour
        setLists(Appconstants.glassBottles,Appconstants.glassBin,Appconstants.returnColour(context,R.color.colourGlass));
        setLists(Appconstants.metalTins,"Brown/Grey Metal bin",Appconstants.returnColour(context,R.color.colourMetal));
        adapter = new AdditionalAdapter(materialList,binList,1,colourList);
        rc.setAdapter(adapter);
    }
    void setStudentAccommodation() {
        String sAdditionalInfo = "Recycling goes into recycling bag in the kitchen and then placed into the outside mixed recycling bin." +
                "Additional items such as batteries can be recycle by taking them to reception";
        textViewAdditionInfo.setText(sAdditionalInfo);
        textViewTitle.setText(sAccom);
        setLists(Appconstants.aerosols,Appconstants.mixedRecyclingBin,Appconstants.returnColour(context,R.color.colourMetal));
        setLists(Appconstants.cardboard,Appconstants.mixedRecyclingBin,Appconstants.returnColour(context,R.color.colourCardboard));
        setLists(Appconstants.metalTins,Appconstants.mixedRecyclingBin,Appconstants.returnColour(context,R.color.colourMetal));
        setLists(Appconstants.plasticBottles,Appconstants.mixedRecyclingBin,Appconstants.returnColour(context,R.color.colourPlastic));
        setLists(Appconstants.plasticPackaging,Appconstants.mixedRecyclingBin,Appconstants.returnColour(context,R.color.colourPlastic));
        setLists(Appconstants.glassBottles,Appconstants.glassBin,Appconstants.returnColour(context,R.color.colourGlass));
        adapter = new AdditionalAdapter(materialList,binList,1,colourList);
        rc.setAdapter(adapter);
    }
    void setSunderlandCouncil() {
        String sAdditionalInfo = "Bin days can be found on the Sunderland council website";
        textViewAdditionInfo.setText(sAdditionalInfo);
        textViewTitle.setText(sCouncil);
        setLists(Appconstants.aerosols, Appconstants.blueBin,Appconstants.returnColour(context,R.color.colourMetal));
        setLists(Appconstants.cardboard,Appconstants.blueBin,Appconstants.returnColour(context,R.color.colourCardboard));
        setLists(Appconstants.metalTins,Appconstants.blueBin,Appconstants.returnColour(context,R.color.colourMetal));
        setLists(Appconstants.paper,Appconstants.blackInnerBin,Appconstants.returnColour(context,R.color.colourCardboard));
        setLists(Appconstants.plasticBottles,Appconstants.blueBin,Appconstants.returnColour(context,R.color.colourPlastic));
        setLists(Appconstants.plasticPackaging,Appconstants.blueBin,Appconstants.returnColour(context,R.color.colourPlastic));
        setLists(Appconstants.glassBottles,Appconstants.blueBin,Appconstants.returnColour(context,R.color.colourGlass));
        adapter = new AdditionalAdapter(materialList,binList,1,colourList);
        rc.setAdapter(adapter);
    }

    void setLists(String material, String bin, int colour) {
        materialList.add(material);
        binList.add(bin);
        colourList.add(colour);
    }

    public FragmentAdditional(int type) {
        this.type = type;
    }
}
