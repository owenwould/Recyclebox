package com.example.recyclebox.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.recyclebox.AchievementManager;
import com.example.recyclebox.Appconstants;
import com.example.recyclebox.Fragments.FragmentAdditional;
import com.example.recyclebox.Fragments.FragmentHome;
import com.example.recyclebox.Fragments.FragmentLeaderboard;
import com.example.recyclebox.Fragments.FragmentRecord;
import com.example.recyclebox.Fragments.FragmentSearch;
import com.example.recyclebox.Fragments.FragmentStats;
import com.example.recyclebox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.recyclebox.Data_models.Achievement;
import com.example.recyclebox.Data_models.User;
import com.example.recyclebox.Singleton.Singleton;

public class MainActivity extends AppCompatActivity implements FragmentRecord.FragmentRecordListener, FragmentStats.FragmentStatsListner, FragmentHome.Home_listener {

    FragmentHome homeFragment;
    FragmentRecord recordFragment;
    FragmentSearch searchFragment;
    FragmentStats statsFragment;
    FragmentLeaderboard leaderboardFragment;
    Singleton singleton;
    User currentUser;
    CollectionReference userRef, achievementRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean userLoaded = false, needsToBeNotified = false;
    AchievementManager achievementManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
        homeFragment = new FragmentHome();
        recordFragment = new FragmentRecord();
        searchFragment = new FragmentSearch();
        statsFragment = new FragmentStats();
        leaderboardFragment = new FragmentLeaderboard();

        singleton = Singleton.getInstance();

        achievementRef = db.collection(Appconstants.ACHIEVEMENT_COLLECTION);
        userRef = db.collection("users");


        if (singleton.checkLoggedIn()){
            currentUser = setCurrentUser();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_menu_option:
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Boolean canLoggin = singleton.checkLoggedIn();
            if (canLoggin) {
                if (!userLoaded) {
                    Toast.makeText(MainActivity.this, "User account still loading wait a few more seconds, " +
                            "Internet connection will affect the speed of the process", Toast.LENGTH_SHORT).show();
                    needsToBeNotified = true;
                    return false;
                }
            }

            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    if (canLoggin)
                        selectedFragment = homeFragment;
                    break;
                case R.id.nav_record:
                    if (canLoggin)
                        selectedFragment = recordFragment;
                    break;
                case R.id.nav_search:
                    if (canLoggin)
                        selectedFragment = searchFragment;
                    break;
                case R.id.nav_profile:
                    if (canLoggin)
                        selectedFragment = statsFragment;
                    break;
                case R.id.nav_leaderboard:
                    if (canLoggin)
                        selectedFragment = leaderboardFragment;
                    break;
            }
            if (selectedFragment == null) {
                Toast.makeText(MainActivity.this, "Please sign in to access other features, press the 3 dot button on the top bar then settings", Toast.LENGTH_LONG).show();
                return false;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }

    };


    public User setCurrentUser() {
        if (singleton.checkLoggedIn()) {
            if (Singleton.isHasCurrentDocumentID()) {
                currentUser = singleton.getCurrentUser();
                userLoaded = true;
            } else {
                userRef.whereEqualTo("userID", singleton.getUserID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentList = new ArrayList<>(task.getResult().getDocuments());
                            if (documentList.size() > 1)
                                Toast.makeText(MainActivity.this, "More than one user returned, error may occur in this session", Toast.LENGTH_SHORT).show();

                            if (documentList.isEmpty()) {
                                //Sometimes when an account is created firestore hasnt created the second its needed
                                Runnable r = new Runnable() {
                                    @Override
                                    public void run() {
                                        currentUser = setCurrentUser();
                                    }
                                };
                                Handler handler = new Handler();
                                handler.postDelayed(r,1000);
                            }

                            for (DocumentSnapshot doc : documentList) {
                                User user = doc.toObject(User.class);
                                user.setDocumentID(doc.getId());
                                currentUser = user;
                                singleton.setUserDocumentID(user.getDocumentID());
                                Singleton.setHasCurrentDocumentID(true);
                                singleton.setCurrentUser(currentUser);
                                singleton.setUsername(user.getUsername());
                                getAchievementList(user);
                            }

                        } else
                            Toast.makeText(MainActivity.this, "Unable to load current user, internet is required for app", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return currentUser;
    }


    private void getAchievementList(final User user) {
        achievementRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Achievement> list = new ArrayList<>();
                    List<DocumentSnapshot> docList = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : docList) {
                        list.add(doc.toObject(Achievement.class));
                    }
                  setAchievementManager(list,user);
                }
            }
        });
    }

    private void setAchievementManager(List<Achievement> list,User user) {
        achievementManager = new AchievementManager(list);
        achievementManager.setList(user.getRecyclables(), user.getRecycledCount());
        userLoaded = true;
        if (needsToBeNotified)
            Toast.makeText(MainActivity.this, "User Account loaded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public User returnUser() {
        return setCurrentUser();
    }

    @Override
    public AchievementManager returnAchievementManager() {
        return achievementManager;
    }

    @Override
    public void updateAchievementManager(List<Achievement> list) {
        achievementManager.setAchievementList(list);
        //Prevents current values with regards to achievements being out of sync
    }

    @Override
    public User returnCurrentUser() {
        return setCurrentUser();
    }


    @Override
    public List<Achievement> returnAchievementList() {
        return achievementManager.getAchievementList();
    }

    @Override
    public void openAdditionalFragment(int iType) {
        Fragment fragment;
        switch (iType) {
            case Appconstants.ACCOMMODATION_CODE:
                fragment = new FragmentAdditional(Appconstants.ACCOMMODATION_CODE);
                break;
            case Appconstants.SUNDERLAND_COUNCIL_CODE:
                fragment = new FragmentAdditional(Appconstants.SUNDERLAND_COUNCIL_CODE);
                break;
            case Appconstants.UNIVERSITY_CODE:
                fragment = new FragmentAdditional(Appconstants.UNIVERSITY_CODE);
                break;
            default:
                fragment = null;
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please use bottom navigation, back button disabled to prevent unintended behaviour", Toast.LENGTH_SHORT).show();
    }
}
