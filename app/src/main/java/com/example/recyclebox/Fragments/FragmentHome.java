package com.example.recyclebox.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recyclebox.Activities.Login;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.R;
import com.example.recyclebox.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.recyclebox.Singleton.Singleton;


public class FragmentHome extends Fragment {
    TextView tvRecyclablesQuantity, tvFact;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference infoRef = db.collection(Appconstants.COLLECTION_KEY_INFORMATION);
    DocumentReference infoDocRef = infoRef.document(Appconstants.INFO_DOCUMENT_KEY);
    DocumentReference factDocRef = infoRef.document(Appconstants.FACT_DOCUMENT_KEY);
    ListenerRegistration recyclablesListener;
    List<String> factList;
    Button nextFactButton, previousFactButton;
    Singleton singleton;
    Home_listener listener;
    boolean canChangeFact;

    int iCurrentFact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragement_home, container, false);
        tvRecyclablesQuantity = v.findViewById(R.id.home_recyclable_counter);
        tvFact = v.findViewById(R.id.textViewFacts);
        tvFact.setVisibility(View.GONE);
        nextFactButton = v.findViewById(R.id.button_home_nextFact);
        previousFactButton = v.findViewById(R.id.button_home_prevousFact);
        nextFactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canChangeFact)
                    nextFact();
                else
                    Toast.makeText(getContext(), "Fact List not loaded", Toast.LENGTH_SHORT).show();
            }
        });
        previousFactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canChangeFact)
                    previousFact();
                else
                    Toast.makeText(getContext(), "Fact List not loaded", Toast.LENGTH_SHORT).show();
            }
        });
        nextFactButton.setVisibility(View.GONE);
        previousFactButton.setVisibility(View.GONE);
        singleton = Singleton.getInstance();
        if (Singleton.isHasSetFactList()) {
            factList = singleton.getFactList();
            setFactList(factList);
        } else
            getFactList();
        setAdditionFragmentButtons(v);
        return v;
    }

    public void updateRecyclablesCounter(CharSequence text) {
        tvRecyclablesQuantity.setText(text);
    }


    @Override
    public void onStart() {
        super.onStart();
        recyclablesListener = infoDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null)
                    return;
                if (documentSnapshot.exists()) {
                    String value = String.valueOf(documentSnapshot.getLong(Appconstants.TOTAL_RECYCLED_COUNT));
                    updateRecyclablesCounter(value);
                } else
                    Toast.makeText(getContext(), "Error: Information Document not reachable" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        recyclablesListener.remove();
    }

    private void setFactList(List<String> tempFactList) {
        factList = tempFactList;
        displayFact();
        canChangeFact = true;
    }

    private void displayFact() {
        tvFact.setVisibility(View.VISIBLE);
        int min = 0;
        int max = factList.size() - 1;
        int random = new Random().nextInt((max - min) + 1) + min;
        String fact = factList.get(random);
        tvFact.setText(fact);
        iCurrentFact = random;
        nextFactButton.setVisibility(View.VISIBLE);
        previousFactButton.setVisibility(View.VISIBLE);
    }

    private void nextFact() {
        int tempIndex = iCurrentFact + 1;

        if (tempIndex > factList.size() - 1) {
            iCurrentFact = 0;
            String fact = factList.get(iCurrentFact);
            tvFact.setText(fact);
        } else {
            iCurrentFact = tempIndex;
            String fact = factList.get(iCurrentFact);
            tvFact.setText(fact);
        }
    }

    private void previousFact() {
        int tempIndex = iCurrentFact - 1;

        if (tempIndex < 0) {
            iCurrentFact = factList.size() - 1;
            String fact = factList.get(iCurrentFact);
            tvFact.setText(fact);
        } else {
            iCurrentFact = tempIndex;
            String fact = factList.get(iCurrentFact);
            tvFact.setText(fact);
        }
    }

    private void getFactList() {
        canChangeFact = false;
        factDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    List<String> tempList = new ArrayList<String>((ArrayList) doc.get(Appconstants.FACTLIST_KEY));
                    if (tempList != null && tempList.size() > 0) {
                        setFactList(tempList);
                        singleton = Singleton.getInstance();
                        singleton.setFactList(tempList);
                        Singleton.setHasSetFactList(true);
                    } else
                        Toast.makeText(getContext(), "Error with loading fact list", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AccommodationRecycling() {
        if (singleton.checkLoggedIn())
            listener.openAdditionalFragment(Appconstants.ACCOMMODATION_CODE);
        else
            Toast.makeText(getContext(), "Please sign in to access other features, press the 3 dot button on the top bar then settings", Toast.LENGTH_SHORT).show();
    }

    private void SunderlandCouncil() {
        if (singleton.checkLoggedIn())
            listener.openAdditionalFragment(Appconstants.SUNDERLAND_COUNCIL_CODE);
        else
            Toast.makeText(getContext(), "Please sign in to access other features, press the 3 dot button on the top bar then settings", Toast.LENGTH_SHORT).show();

    }

    private void University() {
        if (singleton.checkLoggedIn())
            listener.openAdditionalFragment(Appconstants.UNIVERSITY_CODE);
        else
            Toast.makeText(getContext(), "Please sign in to access other features, press the 3 dot button on the top bar then settings", Toast.LENGTH_SHORT).show();
    }

    public interface Home_listener {
        void openAdditionalFragment(int iType);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Home_listener)
            listener = (Home_listener) context;
        else
            throw new RuntimeException(context.toString() + " must implement Fragment record Interface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private void setAdditionFragmentButtons(View v) {
        Button buttonAccom, buttonSunderlandCouncil, buttonUni;
        buttonAccom = v.findViewById(R.id.home_button_viewStudentAccom);
        buttonSunderlandCouncil = v.findViewById(R.id.home_button_sunderlandcouncil);
        buttonUni = v.findViewById(R.id.home_button_viewUni);
        buttonAccom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccommodationRecycling();
            }
        });
        buttonSunderlandCouncil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SunderlandCouncil();
            }
        });
        buttonUni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                University();
            }
        });
    }



}
