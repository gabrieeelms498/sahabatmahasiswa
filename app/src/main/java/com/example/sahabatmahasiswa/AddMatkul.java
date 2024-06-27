package com.example.sahabatmahasiswa;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddMatkul extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText edtMatkul, edtSKS, edtSmt, edtRuangan, edtDosen;
    private Button btnSave, timeButton, timeButton2, buttonShowMenu;
    private int hour, minute;
    private String startTime, endTime, selectedDay;
    private ImageButton btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_matkul);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtMatkul = findViewById(R.id.edt_Matkul);
        edtSKS = findViewById(R.id.edt_SKS);
        edtSmt = findViewById(R.id.edt_Smt);
        edtRuangan = findViewById(R.id.edt_Ruangan);
        edtDosen = findViewById(R.id.edt_Dosen);

        btnback = findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_Save);
        timeButton = findViewById(R.id.timeButton);
        timeButton2 = findViewById(R.id.timeButton2);
        buttonShowMenu = findViewById(R.id.button_show_menu);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirestore();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(AddMatkul.this, Matkul.class);
                startActivity(back);
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
            }
        });

        timeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker2();
            }
        });

        buttonShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void saveDataToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AddMatkul.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference newDocRef = db.collection("matkul").document();
        String userId = currentUser.getUid();
        String matkul = edtMatkul.getText().toString().trim();
        String sks = edtSKS.getText().toString().trim();
        String smt = edtSmt.getText().toString().trim();
        String ruangan = edtRuangan.getText().toString().trim();
        String dosen = edtDosen.getText().toString().trim();
        String newMatkulId = newDocRef.getId();


        Map<String, Object> matkulData = new HashMap<>();
        matkulData.put("userId", userId);
        matkulData.put("matkul", matkul);
        matkulData.put("sks", sks);
        matkulData.put("smt", smt);
        matkulData.put("ruangan", ruangan);
        matkulData.put("dosen", dosen);
        matkulData.put("start_time", startTime);
        matkulData.put("end_time", endTime);
        matkulData.put("day", selectedDay);
        matkulData.put("matkulId",newMatkulId);

        db.collection("matkul").document(newMatkulId)
                .set(matkulData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddMatkul.this, "Mata kuliah berhasil disimpan", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddMatkul.this, Matkul.class);
                    startActivity(intent);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddMatkul.this, "Gagal menambahkan mata kuliah", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                timeButton.setText(startTime);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void popTimePicker2() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                endTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                timeButton2.setText(endTime);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_days);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectedDay = item.getTitle().toString();
                buttonShowMenu.setText(selectedDay);
                return true;
            }
        });

        popupMenu.show();
    }
}