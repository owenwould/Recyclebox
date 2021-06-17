package com.example.recyclebox.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recyclebox.AchievementManager;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.recyclebox.Data_models.Achievement;
import com.example.recyclebox.Data_models.User;
import com.example.recyclebox.Singleton.Singleton;

public class FragmentRecord extends Fragment implements AchievementManager.AchivementManagerListner {

    Singleton singleton;

    final int POINT_REWARD = 10;
    User currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef;
    DocumentReference docRef;
    RadioGroup radioGroup;
    FragmentRecordListener listener;
    Button recyclablesButton;
    boolean canRecord = true;
    ProgressBar progressBar;
    List<Button> helpButtonList;
    AchievementManager achievementManager;
    int iType = -1;
    int iHelpIndex = -1;
    boolean onlyDisplayOneToast = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragement_record, container, false);
        recyclablesButton = v.findViewById(R.id.record_recyclables_button);
        recyclablesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canRecord)
                    recordRecyclable();

            }
        });
        recyclablesButton.setEnabled(false);
        userRef = db.collection("users");

        singleton = Singleton.getInstance();
        currentUser = singleton.getCurrentUser();
        if (currentUser == null)
            currentUser = listener.returnUser();

        progressBar = v.findViewById(R.id.record_progressbar);
        progressBar.setVisibility(View.GONE);
        achievementManager = listener.returnAchievementManager();
        achievementManager.setListner(this);

        docRef = db.collection(Appconstants.COLLECTION_KEY_INFORMATION).document(Appconstants.INFO_DOCUMENT_KEY);
        helpButtonList = new ArrayList<>();
        setRadioButtons(v);
        return v;
    }


    private void recordRecyclable() {

        canRecord = false;
        if (currentUser != null) {
            recyclablesButton.setEnabled(false);
            int currentPoints = currentUser.getPoints();
            currentPoints += POINT_REWARD;
            int recycledCount = currentUser.getRecycledCount();
            recycledCount += 1;

            String rank = currentUser.getRank();
            String newRank = rank;

            if (rank.equals(Appconstants.BRONZE_RANK)) {
                if (recycledCount >= Appconstants.SILVER_RANK_POINTS)
                    newRank = Appconstants.SIlVER_RANK;
            } else if (rank.equals(Appconstants.SIlVER_RANK)) {
                if (recycledCount >= Appconstants.GOLD_RANK_POINTS)
                    newRank = Appconstants.GOLD_RANK;
            } else if (rank.equals(Appconstants.GOLD_RANK)) {
                if (recycledCount >= Appconstants.Plat_RANK_POINTS)
                    newRank = Appconstants.Plat_RANK;
            }

            //Update values so that its not necessary to retrieve whole document each time
            currentUser.setPoints(currentPoints);
            currentUser.setRecycledCount(recycledCount);
            String documentID = currentUser.getDocumentID();
            userRef.document(documentID).update(Appconstants.POINTS_KEY, currentPoints);
            userRef.document(documentID).update(Appconstants.RECYCLED_COUNT_KEY, recycledCount);

            boolean hasRankedUp = false;
            if (!newRank.equals(rank)) {
                //Ranked up
                hasRankedUp = true;
                userRef.document(documentID).update(Appconstants.RANK_KEY, newRank);
                currentUser.setRank(newRank);
            }

            updateUserRecyclables(documentID);

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    long currentTotal = documentSnapshot.getLong(Appconstants.TOTAL_RECYCLED_COUNT);
                    updateTotalRecyclables(currentTotal);
                }
            });
            singleton.setCurrentUser(currentUser);
            if (!hasRankedUp) {
                //Achievement toast will be displayed, don't display two at once
                if (!onlyDisplayOneToast)
                    Toast.makeText(getContext(), "Well Done, " + currentUser.getUsername() + " Another Item Recorded", Toast.LENGTH_SHORT).show();
                else
                    onlyDisplayOneToast = false;
            }
            achievementManager.checkAchievements(iType);
        } else {
            Toast.makeText(getContext(), "Error unable to obtain current user", Toast.LENGTH_SHORT).show();
            canRecord = true;
        }
    }

    @Override
    public void achievementsChecked() {
        //Called by achievement manager
        listener.updateAchievementManager(achievementManager.getAchievementList());
    }

    private void updateTotalRecyclables(long val) {
        val += 1;
        docRef.update(Appconstants.TOTAL_RECYCLED_COUNT, val);
    }

    private void updateUserRecyclables(String documentId) {

        String key;
        int id = radioGroup.getCheckedRadioButtonId();
        RadioButton rb = getView().findViewById(id);
        key = rb.getText().toString().trim();

        Map<String, Integer> temporaryMap = currentUser.getRecyclables();

        if (temporaryMap.containsKey(key))
            temporaryMap.put(key, temporaryMap.get(key) + 1);
        else {
            temporaryMap.put(key, 1);
            onlyDisplayOneToast = true;
            String message = "Well done " + currentUser.getUsername() + " for recycling something new";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        currentUser.setRecyclables(temporaryMap);
        userRef.document(documentId).update(Appconstants.MAP_KEY, temporaryMap);
        progressBar.setVisibility(View.VISIBLE);


        new CountDownTimer(1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) ((100 * (1000 - millisUntilFinished) / 1000));
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                canRecord = true;
                progressBar.setVisibility(View.GONE);
                recyclablesButton.setEnabled(true);
            }
        }.start();
    }

    private void achievementUnlocked(int points) {
        int currentPoints = currentUser.getPoints();
        currentPoints += points;
        String documentID = currentUser.getDocumentID();
        userRef.document(documentID).update(Appconstants.POINTS_KEY, currentPoints);
        currentUser.setPoints(currentPoints);
        singleton.setCurrentUser(currentUser);
    }

    public interface FragmentRecordListener {
        User returnUser();

        AchievementManager returnAchievementManager();

        void updateAchievementManager(List<Achievement> list);
    }

    @Override
    public void achievementUnlocked(String name, int points) {
        Toast.makeText(getContext(), "Achievement Unlocked: " + name + "." +
                " " + points + "XP for completing achievement", Toast.LENGTH_SHORT).show();
        achievementUnlocked(points);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragmentRecordListener)
            listener = (FragmentRecordListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement Fragment record Interface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    void setRadioButtons(View v) {

        Button bAerosol, bCardboard, bMetalTin, bPaper, bPlasticBottles, bPlasticPackaging, bGlassBottles;
        bAerosol = v.findViewById(R.id.record_aerosols_button);
        bCardboard = v.findViewById(R.id.record_cardboard_button);
        bMetalTin = v.findViewById(R.id.record_metaltin_button);
        bPaper = v.findViewById(R.id.record_paper_button);
        bPlasticBottles = v.findViewById(R.id.record_plasticbottles_button);
        bPlasticPackaging = v.findViewById(R.id.record_plasticpackaging_button);
        bGlassBottles = v.findViewById(R.id.record_glassbottles_button);

        View.OnClickListener helpListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                switch (v.getId()) {
                    case R.id.record_aerosols_button:
                        message = aerosolMessage();
                        helpDialog(message, "HOW TO RECYCLE AEROSOLS");
                        break;
                    case R.id.record_cardboard_button:
                        message = cardboardMessage();
                        helpDialog(message, "HOW TO RECYCLE CARD AND CARDBOARD");
                        break;
                    case R.id.record_metaltin_button:
                        message = metalTinMessage();
                        helpDialog(message, "HOW TO RECYCLE FOOD TINS");
                        break;
                    case R.id.record_paper_button:
                        message = paperMessage();
                        helpDialog(message, "PAPER RECYCLING TIPS");
                        break;
                    case R.id.record_plasticbottles_button:
                        message = plasticBottlesMessage();
                        helpDialog(message, "HOW TO RECYCLE PLASTIC BOTTLES");
                        break;
                    case R.id.record_plasticpackaging_button:
                        message = plasticPackagingMessage();
                        helpDialog(message, "HOW TO RECYCLE PLASTIC POTS, TUBS AND TRAYS");
                        break;
                    case R.id.record_glassbottles_button:
                        message = glassBottlesMessage();
                        helpDialog(message, "HOW TO RECYCLE GLASS BOTTLES AND JARS");
                        break;
                }
            }
        };

        bAerosol.setOnClickListener(helpListener);
        bCardboard.setOnClickListener(helpListener);
        bMetalTin.setOnClickListener(helpListener);
        bPaper.setOnClickListener(helpListener);
        bPlasticBottles.setOnClickListener(helpListener);
        bPlasticPackaging.setOnClickListener(helpListener);
        bGlassBottles.setOnClickListener(helpListener);


        helpButtonList.add(bAerosol);
        helpButtonList.add(bCardboard);
        helpButtonList.add(bMetalTin);
        helpButtonList.add(bPaper);
        helpButtonList.add(bPlasticBottles);
        helpButtonList.add(bPlasticPackaging);
        helpButtonList.add(bGlassBottles);


        for (Button button : helpButtonList) {
            button.setVisibility(View.INVISIBLE);
        }


        radioGroup = v.findViewById(R.id.record_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {


                    if (iHelpIndex != -1) {
                        Button buttonHide = helpButtonList.get(iHelpIndex);
                        buttonHide.setVisibility(View.INVISIBLE);
                    }
                    recyclablesButton.setEnabled(true);
                    RadioButton rb = getView().findViewById(checkedId);
                    String sRadioButtonText = rb.getText().toString();
                    iType = Appconstants.returnMaterialType(sRadioButtonText);
                    iHelpIndex = Appconstants.returnRadioButtonIndex(sRadioButtonText);
                    //Any changes to radiobuttons text must be matched in app constants
                    Button button = helpButtonList.get(iHelpIndex);
                    button.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    void helpDialog(String message, String title) {
        HelpDialog dialog = new HelpDialog(title, message);
        dialog.show(getFragmentManager(), "helper");
    }


    String aerosolMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ensure aerosols are completely empty before recycling");
        sb.append("\n");
        sb.append("\n");
        sb.append("Do not pierce, crush or flatten aerosol cans");
        sb.append("\n");
        sb.append("\n");
        sb.append("Detach any loose or easily removable parts, such as the lid, and dispose of them with the rest of your recycling");
        String y = sb.toString();
        return y;
    }

    String metalTinMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Empty and rinse items - left over foods or liquids can contaminate other recyclables, edges can be sharp");
        sb.append("\n");
        sb.append("\n");
        sb.append("You can leave labels on - these are removed in the recycling process");
        sb.append("\n");
        sb.append("\n");
        sb.append("Metal lids and caps on glass containers, e.g. metal jam jar lids, can be left on to recycle with glass");
        sb.append("\n");
        sb.append("\n");
        sb.append("These are different types of metal to cans/tins/aerosols and are recycled in a different way");
        sb.append("\n");
        sb.append("\n");
        sb.append("By putting the lids/caps back on glass jars and bottles it reduces the chance of them getting lost through the sorting process");
        String y = sb.toString();
        return y;
    }

    String paperMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Paper is a valuable recyclable material but only when it is clean");
        sb.append("\n");
        sb.append("\n");
        sb.append("Remove any plastic wrapping from newspapers and magazines - this can be recycled with plastic bags at larger supermarkets");
        sb.append("\n");
        sb.append("\n");
        sb.append("If you scrunch paper and it doesn't spring back, then it can be recycled");
        String y = sb.toString();
        return y;
    }

    String plasticBottlesMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Squash bottles to save space");
        sb.append("\n");
        sb.append("\n");
        sb.append("Empty and rinse bottles. Left over foods or liquids can contaminate other recyclables and if bottles contain liquid they may not be recycled as deemed too heavy by the automated sorting process. Liquid can also damage the machinery");
        sb.append("\n");
        sb.append("\n");
        sb.append("Leave on labels - these will be removed in the process");
        String y = sb.toString();
        return y;
    }

    String glassBottlesMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Put lids and caps back on. This reduces the chance of them getting lost during the sorting process as they can be recycled separately");
        sb.append("\n");
        sb.append("\n");
        sb.append("Empty and rinse - a quick rinse will do. Leftover liquid can contaminate other recyclables which may mean they aren't recycled");
        String y = sb.toString();
        return y;

    }

    String cardboardMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("The main types of cardboard we find at home are corrugated cardboard (i.e. packing boxes) and the types used for food and drink packaging. Both types are recyclable but councils sometimes collect them separately");
        sb.append("\n");
        sb.append("\n");
        sb.append("Tape, polystyrene and plastic inserts need be removed from cardboard packaging");
        String y = sb.toString();
        return y;
    }

    String plasticPackagingMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rinse food packaging - a quick rinse will do. Left-over food residue can contaminate other recyclables");
        sb.append("\n");
        sb.append("\n");
        sb.append("Labels and lids can all be left on but plastic film must be removed and put in the general rubbish bin");
        String y = sb.toString();
        return y;
    }


}
