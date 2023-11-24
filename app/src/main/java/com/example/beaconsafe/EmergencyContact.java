package com.example.beaconsafe;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beaconsafe.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmergencyContact extends AppCompatActivity {

    private static final String TAG = "EmergencyContact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencycontact);
        FirebaseApp.initializeApp(this);

        // Get the current authenticated user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Assuming "emergencyContacts" is the node containing emergency contact information
            DatabaseReference emergencyContactsRef = FirebaseDatabase.getInstance().getReference("emergencyContacts").child(currentUserId);

            emergencyContactsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve emergency contact information
                        String contactName = dataSnapshot.child("Name").getValue(String.class);
                        String contactPhoneNumber = dataSnapshot.child("PhoneNumber").getValue(String.class);

                        // Use the retrieved data (e.g., display it in UI)
                        Log.d(TAG, "Emergency Contact Name: " + contactName);
                        Log.d(TAG, "Emergency Contact Phone Number: " + contactPhoneNumber);
                    } else {
                        // No emergency contact information found
                        Log.d(TAG, "No emergency contact information found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        } else {
            // User is not authenticated, handle accordingly
            Log.d(TAG, "User not authenticated");
        }

        TextView changeContact = findViewById(R.id.cgEmerContact);
        changeContact.setPaintFlags(changeContact.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        changeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContact.this);

                View ContactView = LayoutInflater.from(EmergencyContact.this).inflate(R.layout.activity_cg_emg_contact,null);
                builder.setView(ContactView);

                //ContactView.setBackgroundColor(getResources().getColor(R.color.purple));

                // Find the EditText in the inflated layout
                EditText InputName = ContactView.findViewById(R.id.NameDisplay);
                EditText InputContact = ContactView.findViewById(R.id.PhoneNumberDisplay);

                // Set the positive button action (OK)
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get the entered text
                        String NameInput = InputName.getText().toString();
                        String PhoneInput = InputContact.getText().toString();

                        // Update the emergency contact information in the Firebase Realtime Database
                        if (currentUser != null) {
                            String currentUserId = currentUser.getUid();
                            DatabaseReference emergencyContactsRef = FirebaseDatabase.getInstance().getReference("emergencyContacts").child(currentUserId);

                            // Update the values in the database
                            emergencyContactsRef.child("Name").setValue(NameInput);
                            emergencyContactsRef.child("PhoneNumber").setValue(PhoneInput);

                            // Do something with the entered text, if needed
                            Log.d(TAG, "Emergency Contact Name: " + NameInput);
                            Log.d(TAG, "Emergency Contact Phone Number: " + PhoneInput);
                        } else {
                            // User is not authenticated, handle accordingly
                            Log.d(TAG, "User not authenticated");
                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked Cancel
                        dialogInterface.dismiss();
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
