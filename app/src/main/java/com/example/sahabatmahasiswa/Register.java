package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtName, edtAge, edtPassword;
    private Spinner spinGender, spinCollege;
    private ImageButton btnUploadProfPic;
    private Button btnRegister, btnGoToLogin;
    private Uri profilePicUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");

        edtEmail = findViewById(R.id.edt_Email);
        edtName = findViewById(R.id.edt_Name);
        edtAge = findViewById(R.id.edt_Age);
        edtPassword = findViewById(R.id.edt_PW);
        spinGender = findViewById(R.id.spin_Gender);
        spinCollege = findViewById(R.id.spin_College);
        btnUploadProfPic = findViewById(R.id.btn_uploadprofpic);
        btnRegister = findViewById(R.id.btn_Register);
        btnGoToLogin = findViewById(R.id.btn_gotologin);

        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Laki-laki", "Perempuan"});
        spinGender.setAdapter(adapterGender);

        ArrayAdapter<String> adapterCollege = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"UPN Veteran Jakarta", "UPN Veteran Yogyakarta", "UPN Veteran Jawa Timur"});
        spinCollege.setAdapter(adapterCollege);

        btnRegister.setOnClickListener(this);
        btnGoToLogin.setOnClickListener(this);
        btnUploadProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profilePicUri = data.getData();
            btnUploadProfPic.setImageURI(profilePicUri);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_Register) {
            registerUser();
        } else if (id == R.id.btn_gotologin) {
            startActivity(new Intent(Register.this, Login.class));
        }
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String age = edtAge.getText().toString().trim();
        String gender = spinGender.getSelectedItem().toString();
        String college = spinCollege.getSelectedItem().toString();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || age.isEmpty() || password.isEmpty() || profilePicUri == null) {
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadProfilePic(user, email, name, age, gender, college);
                        } else {
                            Toast.makeText(Register.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void uploadProfilePic(FirebaseUser user, String email, String name, String age, String gender, String college) {
        if (user != null && profilePicUri != null) {
            StorageReference fileReference = storageRef.child(user.getUid() + ".jpg");
            fileReference.putFile(profilePicUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            saveUserToFirestore(user.getUid(), email, name, age, gender, college, downloadUri.toString());
                                        } else {
                                            Toast.makeText(Register.this, "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Register.this, "Upload gambar gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUserToFirestore(String userId, String email, String name, String age, String gender, String college, String profilePicUrl) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("name", name);
        user.put("age", age);
        user.put("gender", gender);
        user.put("college", college);
        user.put("profilePicUrl", profilePicUrl);

        db.collection("users").document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Simpan data gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Register", "Pengguna dihapus dari Authentication");
                                    } else {
                                        Log.w("Register", "Gagal menghapus pengguna dari Authentication");
                                    }
                                }
                            });
                        }
                    }
                });
    }
}