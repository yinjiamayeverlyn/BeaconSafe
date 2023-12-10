package com.example.beaconsafe;

import static com.example.beaconsafe.R.id.nav_home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("User");
    public static final int main=0x7f030004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);;

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Home");
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(savedInstanceState ==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new HomeFragment()).commit();
            navigationView.setCheckedItem(nav_home);
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == nav_home){
                replaceFragment(new HomeFragment());
            }else if(id == R.id.alert){
                replaceFragment(new RealTimeAlertFragment());
            }else if(id == R.id.counselor){
                replaceFragment(new CounselorFragment());
            }else if(id ==R.id.profile){
                replaceFragment(new ProfileFragment());
            } else if (id == R.id.sosButton) {
                Intent sos = new Intent(MainActivity.this,Emergency.class);
                startActivity(sos);
            }
            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        FirebaseApp.initializeApp(this);


        /*ImageButton sosButton = (ImageButton) findViewById(R.id.sosButton);
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emergencyActivity = new Intent(MainActivity.this, Emergency.class);
                startActivity(emergencyActivity);
            }
        });*/

    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);


        LinearLayout reporting = dialog.findViewById(R.id.layoutReporting);
        LinearLayout cancel = dialog.findViewById(R.id.layoutCancel);

        reporting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ToReport = new Intent(MainActivity.this,Reporting_User.class);
                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Reporting is clicked",Toast.LENGTH_SHORT).show();
                startActivity(ToReport);

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(mAuth == null || currentUser == null){
            SendUserToLoginActivity();
        }else{
            CheckUserDataExistence();
        }
    }
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    /*
    private void CheckUserExistence(){
        final String currentUserID= mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(currentUserID)){
                    SendUserToSetupActivity();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void CheckUserDataExistence() {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        UserRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User data exists, check if there are any child nodes (fields)
                    if (snapshot.getChildrenCount() > 0) {
                        // User has data, continue with your main activity
                    } else {
                        // No child nodes (fields) under this UID, send the user to the setup activity
                        SendUserToSetupActivity();
                    }
                } else {
                    // User data doesn't exist, send the user to the setup activity
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

    }


    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, EditingProfile.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            Toast.makeText(MainActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
        } else if(id ==R.id.nav_notification) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new NotificationFragment()).commit();
            Toast.makeText(MainActivity.this, "Notification Selected", Toast.LENGTH_SHORT).show();
        }else if(id ==R.id.nav_education) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new EducationModuleFragment()).commit();
            Toast.makeText(MainActivity.this, "Education Module Selected", Toast.LENGTH_SHORT).show();
        }else if(id ==R.id.nav_community) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new CommunityForumFragment()).commit();
            Toast.makeText(MainActivity.this, "Community Forum Selected", Toast.LENGTH_SHORT).show();
        }else if(id ==R.id.nav_feedback) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new FeedbackFragment()).commit();
            Toast.makeText(MainActivity.this, "Feedback Selected", Toast.LENGTH_SHORT).show();
        }else if(id ==R.id.nav_chatsupport) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ChatSupportFragment()).commit();
            Toast.makeText(MainActivity.this, "Chat Support Selected", Toast.LENGTH_SHORT).show();
        }else if(id ==R.id.nav_legalaid) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new LegalAidLocatorFragment()).commit();
            Toast.makeText(MainActivity.this, " Legal Aid Locator Selected", Toast.LENGTH_SHORT).show();
        }else if(id ==R.id.nav_logout) {
            Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            SendUserToLoginActivity();
        }else if (id==R.id.nav_sos){
            Toast.makeText(MainActivity.this,"SOS",Toast.LENGTH_SHORT).show();

        }
        return false;
    }

}