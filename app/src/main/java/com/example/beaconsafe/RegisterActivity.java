package com.example.beaconsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.NetworkRegistrationInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserEmail = (EditText) findViewById(R.id.registerEmail);
        UserPassword = (EditText) findViewById(R.id.registerPassword);
        UserConfirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
        CreateAccountBtn = (Button) findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }
    private void SendUserToMainActivity() {
        Intent homePage = new Intent(RegisterActivity.this, MainActivity.class);
        homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homePage);
        finish();
    }
    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write your email......",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this,"Please write your password......",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this,"Please write your confirm password......",Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this,"Your password do not match with your confirm password......",Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your new account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // User registration successful
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            // Get the user UID
                            String uid = currentUser.getUid();

                            // Create a child in the database with UID as the key
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

                            // You can add more details to the user node if needed
                            SendUserToSetupActivity();
                            Toast.makeText(RegisterActivity.this, "Your are authenticated successfully......", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    } else {
                        // User registration failed
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error Occured: " + message + "......", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }

                private void SendUserToSetupActivity() {
                    Intent setupIntent = new Intent(RegisterActivity.this, EditingProfile.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(setupIntent);
                    finish();
                }

            });
        }
    }

}