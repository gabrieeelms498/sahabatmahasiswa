package com.example.sahabatmahasiswa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {
    private EditText evName, evUmur;
    private Spinner edtGender, edtCollege;
    private ImageButton btnBack;
    private Button btnSimpan;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        evName = findViewById(R.id.edt_Name);
        evUmur = findViewById(R.id.edt_Age);
        edtGender = findViewById(R.id.spin_Gender);
        edtCollege = findViewById(R.id.spin_College);
        btnSimpan = findViewById(R.id.btn_Simpan);

        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Laki-laki", "Perempuan"});
        edtGender.setAdapter(adapterGender);

        ArrayAdapter<String> adapterCollege = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"UPN Veteran Jakarta", "UPN Veteran Yogyakarta", "UPN Veteran Jawa Timur"});
        edtCollege.setAdapter(adapterCollege);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                evName.setText(document.getString("name"));
                                evUmur.setText(document.getString("age"));
                                String gender = document.getString("gender");
                                if (gender != null) {
                                    int genderPosition = adapterGender.getPosition(gender);
                                    edtGender.setSelection(genderPosition);
                                }

                                String college = document.getString("college");
                                if (college != null) {
                                    int collegePosition = adapterCollege.getPosition(college);
                                    edtCollege.setSelection(collegePosition);
                                }
                            } else {
                                Toast.makeText(EditProfile.this, "Dokumen tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("EditProfile", "Gagal mendapatkan dokumen: ", task.getException());
                            Toast.makeText(EditProfile.this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnSimpan = findViewById(R.id.btn_Simpan);
        btnSimpan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent back = new Intent(EditProfile.this, Profile.class);
            startActivity(back);
        } else if (id == R.id.btn_Simpan) {
            updateProfile();
        }
    }

    private void updateProfile() {
        String name = evName.getText().toString().trim();
        String age = evUmur.getText().toString().trim();
        String gender = edtGender.getSelectedItem().toString();
        String college = edtCollege.getSelectedItem().toString();

        if (name.isEmpty() || age.isEmpty()) {
            Toast.makeText(EditProfile.this, "Nama dan umur harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("age", age);
            user.put("gender", gender);
            user.put("college", college);

            db.collection("users").document(userId).update(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfile.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProfile.this, Profile.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfile.this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                        Log.w("EditProfile", "Error updating document", e);
                    });
        }
    }
}