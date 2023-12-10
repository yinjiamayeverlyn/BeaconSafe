package com.example.beaconsafe;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Emergency extends AppCompatActivity {

    private String emergencyContactName;
    private String emergencyPhoneNumber;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        fetchEmergencyContactInfo(currentUserId);
        ImageButton sosButton = findViewById(R.id.sosButton);
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(view.getContext(),Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) view.getContext(),new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else{
                    Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + emergencyPhoneNumber));
                    startActivity(dial);
                }
            }
        });


        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(Emergency.this,MainActivity.class);
                startActivity(home);
            }
        });

        FirebaseApp.initializeApp(this);

        // Get the current authenticated user


        fetchEmergencyContactInfo(currentUser.getUid());


        TextView changeContact = findViewById(R.id.cgEmerContact);
        changeContact.setPaintFlags(changeContact.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        changeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View ContactView = LayoutInflater.from(Emergency.this).inflate(R.layout.activity_cg_emg_contact,null);
                AlertDialog builder = new AlertDialog.Builder(Emergency.this).setView(ContactView).create();
                //ContactView.setBackgroundColor(getResources().getColor(R.color.purple));
                TextView cancel_button = ContactView.findViewById(R.id.cancel_button);
                TextView ok_button = ContactView.findViewById(R.id.ok_button);

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

                EditText InputName = ContactView.findViewById(R.id.NameDisplay);
                EditText InputContact = ContactView.findViewById(R.id.PhoneNumberDisplay);

                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the entered text
                        String NameInput = InputName.getText().toString();
                        String PhoneInput = InputContact.getText().toString();

                        // Update the emergency contact information in the Firebase Realtime Database

                        String currentUserId = currentUser.getUid();
                        DatabaseReference emergencyContactsRef = FirebaseDatabase.getInstance().getReference("User").child(currentUserId);

                        // Update the values in the database
                        emergencyContactsRef.child("emergencyName").setValue(NameInput);
                        emergencyContactsRef.child("emergencyContact").setValue(PhoneInput);

                        // Do something with the entered text, if needed
                        Log.d(TAG, "Emergency Contact Name: " + NameInput);
                        Log.d(TAG, "Emergency Contact Phone Number: " + PhoneInput);


                        builder.dismiss();

                    }
                });
                builder.show();


            }
        });



    }

    protected void onResume() {
        super.onResume();
        checkAndRequestPermissions();
        fetchEmergencyContactInfo(currentUserId);
    }

    private void checkAndRequestPermissions() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + emergencyPhoneNumber));
                    startActivity(dial);
            } else {
                Toast.makeText(getApplicationContext(), "Access To Calling is Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchEmergencyContactInfo(String userId) {
        // Assuming "User" is the node containing user information
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    // Retrieve emergency contact information
                    emergencyContactName = userSnapshot.child("emergencyName").getValue(String.class);
                    emergencyPhoneNumber = userSnapshot.child("emergencyContact").getValue(String.class);

                    // Update UI with the retrieved data
                    updateUIWithEmergencyContact(emergencyContactName, emergencyPhoneNumber);

                    // Other actions if needed
                    Log.d(TAG, "Emergency Contact Name: " + emergencyContactName);
                    Log.d(TAG, "Emergency Contact Phone Number: " + emergencyPhoneNumber);
                } else {
                    // No user data found
                    Log.d(TAG, "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void updateUIWithEmergencyContact(String name, String phoneNumber) {
        // Update TextViews with the retrieved data
        TextView contactNameTextView = findViewById(R.id.ContactName);
        TextView contactNumberTextView = findViewById(R.id.ContactNumber);

        contactNameTextView.setText(name);
        contactNumberTextView.setText(phoneNumber);
    }


}