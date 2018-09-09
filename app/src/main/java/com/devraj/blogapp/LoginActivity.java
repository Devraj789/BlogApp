package com.devraj.blogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmailText, loginPassText;
    Button loginButton, registerButton;
    ProgressBar login_progress;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        login_progress = findViewById(R.id.login_progress);

        loginEmailText = findViewById(R.id.login_email);
        loginPassText = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.btn_register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = loginEmailText.getText().toString();
                String loginPassword = loginPassText.getText().toString();

                //checking whether the email and vassword are empty or not
                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {

                    login_progress.setVisibility(View.VISIBLE);

                    //checking whether the user entered email and password matches with in the firebase entered value
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            } else {
                                String errormessage = task.getException().getLocalizedMessage();
                                Toast.makeText(LoginActivity.this, "Error:" + errormessage, Toast.LENGTH_SHORT).show();
                            }
                            login_progress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //for checking whether the user is logged in
        FirebaseUser currentuser = mAuth.getCurrentUser();

        if (currentuser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
