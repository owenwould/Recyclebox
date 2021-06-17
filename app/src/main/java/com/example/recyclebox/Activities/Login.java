package com.example.recyclebox.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyclebox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.recyclebox.Data_models.User;
import com.example.recyclebox.Singleton.Singleton;
import com.google.firestore.admin.v1beta1.Progress;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class Login extends AppCompatActivity {

    EditText eRegisterEmail, eRegisterPassword, eRegisterUsername;
    EditText eLoginEmail, eLoginPassword;
    Button loginButton, registerButton, signoutButton;
    Spinner areaSpinner;
    ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userCollectionRef = db.collection("users");
    private FirebaseAuth auth;
    String username, area;
    int queryResultCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        areaSpinner = findViewById(R.id.login_area_spiner);
        eRegisterEmail = findViewById(R.id.register_email);
        eRegisterPassword = findViewById(R.id.register_password);
        eRegisterUsername = findViewById(R.id.register_username);
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(onClickListener);
        eLoginEmail = findViewById(R.id.login_email);
        eLoginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(onClickListener);
        signoutButton = findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(onClickListener);
        progressBar = findViewById(R.id.login_progressBar);
        auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.area_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(spinnerAdapter);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_button:
                    register();
                    break;
                case R.id.login_button:
                    login();
                    break;
                case R.id.signout_button:
                    Singleton singleton = Singleton.getInstance();
                    singleton.signout();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
            }
        }
    };


    private void login() {
        String sEmail = eLoginEmail.getText().toString().trim();
        String sPassword = eLoginPassword.getText().toString().trim();
        area = areaSpinner.getSelectedItem().toString();
        if (sEmail.length() < 1 || sPassword.length() < 1) {
            Toast.makeText(this, "Please Enter Email, password and select area", Toast.LENGTH_SHORT).show();
            return;
        }
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser userFirebase = auth.getCurrentUser();
                    if (userFirebase != null) {
                        final String userID = userFirebase.getUid();
                        //Checks if the user has signed in before
                        userCollectionRef.whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                    queryResultCount = task.getResult().size();
                                if (queryResultCount < 1) {
                                    //Only Creates Account when user has signed in & if they haven't already got an account
                                    User user = new User(userID, username, area);
                                    userCollectionRef.document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        Singleton singleton = Singleton.getInstance();
                        singleton.setUserID(userID);


                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.d("LoginActivity", "sign in error " + task.getException());
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        Toast.makeText(Login.this, "Error Retrieving user ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Email or Password incorrect", Toast.LENGTH_SHORT).show();
                    eLoginPassword.setText("");
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                }
            }
        });
    }

    private void register() {
        String sEmail = eRegisterEmail.getText().toString().trim();
        final String sUsername = eRegisterUsername.getText().toString().trim();
        String sPassword = eRegisterPassword.getText().toString().trim();

        if (sEmail.length() < 1 || sUsername.length() < 1 || sPassword.length() < 6) {
            Toast.makeText(this, "Please fill out fields, making sure password is at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    username = sUsername;
                    Toast.makeText(Login.this, "Please Now Login", Toast.LENGTH_SHORT).show();
                } else {
                    //Error
                    Log.d("LoginActivity", "register Error  " + task.getException());
                    Toast.makeText(Login.this, "Error Creating Account", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        progressBar.setVisibility(View.GONE);


    }




}