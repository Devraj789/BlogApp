package com.devraj.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;

    String currentuser_id;

    Toolbar toolbar;
//    FloatingActionButton addpostbtn;

    BottomNavigationView mainBottomNav;

    //bottom fragments in the application
    FragmentHome fragmentHome;
    FragmentNotification fragmentNotification;
    FragmentAccount fragmentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo Blog");


        if (mAuth.getCurrentUser() != null) {
            mainBottomNav = findViewById(R.id.bottomnavigationview);

            //Fragments
            fragmentHome = new FragmentHome();
            fragmentAccount = new FragmentAccount();
            fragmentNotification = new FragmentNotification();

            //default dekhauna ko lagi
            replaceFragment(fragmentHome);

            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.home:
                            replaceFragment(fragmentHome);
                            return true;

                        case R.id.notification:
                            replaceFragment(fragmentNotification);
                            return true;

                        case R.id.account:
                            replaceFragment(fragmentAccount);
                            return true;

                        default:
                            return false;
                    }
                }
            });
//            //for going to post activity
//            addpostbtn = findViewById(R.id.floatingActionButton);
//            addpostbtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(MainActivity.this, NewPostActivity.class));
//                }
//            });
//            //end of post activity
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;

            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SetupActivity.class));
                return true;

            case R.id.add:
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                return true;

            default:

                return false;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is signed in
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();

        } else {

            currentuser_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(currentuser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (!task.getResult().exists()) {
                            startActivity(new Intent(MainActivity.this, SetupActivity.class));
                            finish();
                        }

                    } else {
                        String errormessage = task.getException().getLocalizedMessage();
                        Toast.makeText(MainActivity.this, "Error:" + errormessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();

    }
}
