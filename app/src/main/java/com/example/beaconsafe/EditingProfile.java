package com.example.beaconsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditingProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private EditText UserName,UserContact, EmergencyContact,UserAge;
    private Button SaveInformationBtn, LogOutBtn;
    private DatabaseReference UserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_profile);

        loadingBar =new ProgressDialog(this);
        UserRef = FirebaseDatabase.getInstance().getReference().child("User");
        mAuth = FirebaseAuth.getInstance();
        UserName = (EditText) findViewById(R.id.profileName);
        UserAge = (EditText) findViewById(R.id.profileAge);
        UserContact = (EditText) findViewById(R.id.profileContact);
        EmergencyContact = (EditText) findViewById(R.id.profileEmergencyContact);
        SaveInformationBtn = (Button) findViewById(R.id.profileSaveBtn);
        LogOutBtn = (Button) findViewById(R.id.profileLogoutBtn);

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginActivity = new Intent(EditingProfile.this, LoginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginActivity);
                finish();
            }

        });

        SaveInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });
    }
    private void SaveAccountSetupInformation() {
        String username = UserName.getText().toString();
        String age = UserAge.getText().toString();
        String contactNumber= UserContact.getText().toString();
        String emergencyContact = EmergencyContact.getText().toString();

        if(TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username......", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(age)){
                Toast.makeText(this,"Please write your age......",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(contactNumber)){
            Toast.makeText(this,"Please write your contact number......",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(emergencyContact)){
            Toast.makeText(this,"Please write your emergency contact number......",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("age", age);
            userMap.put("contactNumber", contactNumber);
            userMap.put("emergencyContact", emergencyContact);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

                userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Data saved successfully
                            Toast.makeText(EditingProfile.this, "Information saved successfully", Toast.LENGTH_SHORT).show();
                            Intent homepage = new Intent(EditingProfile.this, MainActivity.class);
                            homepage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homepage);
                            finish();
                        } else {
                            // Error occurred while saving data
                            String message = task.getException().getMessage();
                            Toast.makeText(EditingProfile .this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }
}