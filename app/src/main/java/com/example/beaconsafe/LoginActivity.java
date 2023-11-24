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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button LoginBtn;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgotPassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NeedNewAccountLink = (TextView) findViewById(R.id.registerLink);
        ForgotPassword = (TextView)findViewById(R.id.forgotPassword);
        UserEmail = (EditText) findViewById(R.id.loginEmail);
        UserPassword =(EditText) findViewById(R.id.loginPassword);
        LoginBtn= (Button) findViewById(R.id.loginBtn);
        mAuth= FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToForgotPasswordActivity();
            }

            private void SendUserToForgotPasswordActivity() {
                Intent forgotPassIntent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(forgotPassIntent);
                finish();
            }
        });

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterAtivity();
            }

            private void SendUserToRegisterAtivity() {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowingUserToLogin();
            }
        });
    }
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            SendUserToMainActivity();
        }
    }
    private void SendUserToMainActivity() {
        Intent homePage = new Intent(LoginActivity.this, MainActivity.class);
        homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homePage);
        finish();
    }

    private void AllowingUserToLogin() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write your email......",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password......",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we are allowing you to login into your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this,"You are logged in successfully.",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this,"Error message: "+ message+"......",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }

                private void SendUserToMainActivity() {
                    Intent homePage = new Intent(LoginActivity.this, MainActivity.class);
                    homePage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homePage);
                    finish();
                }
            });

        }
    }

}