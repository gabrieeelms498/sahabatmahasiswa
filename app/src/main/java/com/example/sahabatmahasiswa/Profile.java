package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private TextView tvName, tvEmail, tvAge, tvGender, tvCollege;
    private CircleImageView imgProfPic;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfPic = findViewById(R.id.img_profpic);
        tvName = findViewById(R.id.tv_isinama);
        tvEmail = findViewById(R.id.tv_isiemail);
        tvAge = findViewById(R.id.tv_isiumur);
        tvGender = findViewById(R.id.tv_isigender);
        tvCollege = findViewById(R.id.tv_isicollege);
        ImageButton btnLogout = findViewById(R.id.btn_logout);
        RelativeLayout btnHome1 = findViewById(R.id.rl_Home);
        ImageButton btnHome2 = findViewById(R.id.btn_Home);
        TextView btnHome3 = findViewById(R.id.tv_Home);
        RelativeLayout btnEduspend1 = findViewById(R.id.rl_EduSpend);
        ImageButton btnEduspend2 = findViewById(R.id.btn_EduSpend);
        TextView btnEduspend3 = findViewById(R.id.tv_EduSpend);
        AppCompatButton btnedtProfpic = findViewById(R.id.btn_editpic);
        AppCompatButton btnedtProfile = findViewById(R.id.btn_editprofile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                tvName.setText(document.getString("name"));
                                tvEmail.setText(document.getString("email"));
                                tvAge.setText(document.getString("age"));
                                tvGender.setText(document.getString("gender"));
                                tvCollege.setText(document.getString("college"));

                                String profilePicUrl = document.getString("profilePicUrl");
                                if (profilePicUrl != null) {
                                    Glide.with(Profile.this)
                                            .load(profilePicUrl)
                                            .circleCrop()
                                            .into(imgProfPic);
                                }
                            } else {
                                Toast.makeText(Profile.this, "Document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("ProfileActivity", "Failed to get document: ", task.getException());
                            Toast.makeText(Profile.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    });
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();

                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                startActivity(new Intent(Profile.this, Login.class));
                finish();
            });
            btnHome1.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, Home.class));
                finish();
            });
            btnHome2.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, Home.class));
                finish();
            });
            btnHome3.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, Home.class));
                finish();
            });
            btnEduspend1.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, EduSpend.class));
                finish();
            });
            btnEduspend2.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, EduSpend.class));
                finish();
            });
            btnEduspend3.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, EduSpend.class));
                finish();
            });
            btnedtProfpic.setOnClickListener(v -> openFileChooser());
            btnedtProfile.setOnClickListener(v -> {
                startActivity(new Intent(Profile.this, EditProfile.class));
                finish();
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                StorageReference fileReference = storageRef.child(userId + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    db.collection("users").document(userId)
                                            .update("profilePicUrl", downloadUrl)
                                            .addOnSuccessListener(aVoid -> {
                                                Glide.with(Profile.this)
                                                        .load(downloadUrl)
                                                        .circleCrop()
                                                        .into(imgProfPic);
                                                Toast.makeText(Profile.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(Profile.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show());
                                }))
                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
            }
        }
    }
}
