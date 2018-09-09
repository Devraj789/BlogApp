package com.devraj.blogapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView circleImageView;
    Uri mainImageURL = null;

    EditText setupname;
    Button setupbtn;

    ProgressBar progressBar;

    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String user_id;

    boolean ischanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_activity);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Setup");


        progressBar = findViewById(R.id.progressBar);
        circleImageView = findViewById(R.id.circleimageview);
        setupname = findViewById(R.id.setup_name);
        setupbtn = findViewById(R.id.setup_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressBar.setVisibility(View.VISIBLE);
        setupbtn.setEnabled(false);

        //getting data from the firebasestore
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURL = Uri.parse(image);

                        setupname.setText(name);
                        Glide.with(SetupActivity.this).load(image).into(circleImageView);
                    }

                } else {

                    String error = task.getException().getLocalizedMessage();
                    Toast.makeText(SetupActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();

                }
                progressBar.setVisibility(View.INVISIBLE);
                setupbtn.setEnabled(true);
            }

        });
        //end of getting firebasestore data


        setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = setupname.getText().toString();

                if (!TextUtils.isEmpty(username) && mainImageURL != null) {
                    progressBar.setVisibility(View.VISIBLE);

                    if (ischanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURL).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, username);

                                } else {
                                    String error = task.getException().getLocalizedMessage();
                                    Toast.makeText(SetupActivity.this, "Error:" + error, Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    } else {
                        storeFirestore(null, username);
                    }
                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(SetupActivity.this);
                    }
                } else {
                    //this for lower version of the SDk
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(SetupActivity.this);
                }
            }
        });


    }

    //for storing the contents in the firebasefirestore
    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String username) {

        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURL;

        }
        //sending data to the firebasestore
        Map<String, String> usermap = new HashMap<>();
        usermap.put("name", username);
        usermap.put("image", download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetupActivity.this, "The user Settings are updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetupActivity.this, MainActivity.class));
                } else {
                    String error = task.getException().getLocalizedMessage();
                    Toast.makeText(SetupActivity.this, "FirestoreError:" + error, Toast.LENGTH_SHORT).show();
                }
                //end

                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURL = result.getUri();
                circleImageView.setImageURI(mainImageURL);
                ischanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
}