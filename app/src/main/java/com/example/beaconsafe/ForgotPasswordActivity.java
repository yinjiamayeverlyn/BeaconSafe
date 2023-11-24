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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText forgotPassEmail;
    private Button resetPassBtn,backBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPassEmail = (EditText) findViewById(R.id.forgotPasswordEmail);
        resetPassBtn =(Button) findViewById(R.id.resetPasswordBtn);
        backBtn =(Button) findViewById(R.id.backBtn);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotPassEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this,"Please write your email......",Toast.LENGTH_SHORT).show();
                }else{
                    loadingBar.setTitle("Reset Password");
                    loadingBar.setMessage("In progress......");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ForgotPasswordActivity.this,"Reset password link has been sent to your registered email",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ForgotPasswordActivity.this,"Error occured: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            });
                }
            }
        });
    }
    }
