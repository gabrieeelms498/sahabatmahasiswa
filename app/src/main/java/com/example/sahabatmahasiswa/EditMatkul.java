package com.example.sahabatmahasiswa;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class EditMatkul extends AppCompatActivity {

    private EditText edtMatkul, edtSKS, edtSmt, edtRuangan, edtDosen;
    private Button btnSave, btnDelete, timeButton, timeButton2,buttonShowMenu;
    private FirebaseFirestore db;
    private int hour, minute;
    private String startTime, endTime, selectedDay;
    private ImageButton btnback;

    private String matkulId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_matkul);

        db = FirebaseFirestore.getInstance();

        edtMatkul = findViewById(R.id.edt_Matkul);
        edtSKS = findViewById(R.id.edt_SKS);
        edtSmt = findViewById(R.id.edt_Smt);
        edtRuangan = findViewById(R.id.edt_Ruangan);
        edtDosen = findViewById(R.id.edt_Dosen);
        timeButton = findViewById(R.id.timeButton);
        timeButton2 = findViewById(R.id.timeButton2);
        btnSave = findViewById(R.id.btn_Save);
        btnDelete = findViewById(R.id.btn_Delete);
        buttonShowMenu = findViewById(R.id.button_show_menu);
        btnback = findViewById(R.id.btn_back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            matkulId = extras.getString("matkulId");
            String matkul = extras.getString("matkul");
            String sks = extras.getString("sks");
            String smt = extras.getString("smt");
            String ruangan = extras.getString("ruangan");
            String dosen = extras.getString("dosen");
            String startTime = extras.getString("start_time");
            String endTime = extras.getString("end_time");
            String day = extras.getString("day");

            edtMatkul.setText(matkul);
            edtSKS.setText(sks);
            edtSmt.setText(smt);
            edtRuangan.setText(ruangan);
            edtDosen.setText(dosen);
            timeButton.setText(startTime);
            timeButton2.setText(endTime);
            buttonShowMenu.setText(day);
            Log.d("IntentExtras", "startTime: " + startTime);
            Log.d("IntentExtras", "endTime: " + endTime);
            Log.d("IntentExtras", "day: " + day);
            Log.d("IntentExtras", "matkulId: " + matkulId);

        }else {
            Toast.makeText(this, "Document ID is null", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMatkul();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(EditMatkul.this, Matkul.class);
                startActivity(back);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMatkul();
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

    private void updateMatkul() {
        String matkul = edtMatkul.getText().toString().trim();
        String sks = edtSKS.getText().toString().trim();
        String smt = edtSmt.getText().toString().trim();
        String ruangan = edtRuangan.getText().toString().trim();
        String dosen = edtDosen.getText().toString().trim();
        String startTime = timeButton.getText().toString().trim();
        String endTime = timeButton2.getText().toString().trim();
        String selectedDay = buttonShowMenu.getText().toString().trim();

        DocumentReference matkulRef = db.collection("matkul").document(matkulId);
        matkulRef.update("matkul", matkul, "sks", sks,"smt",smt,"ruangan",ruangan,"dosen",dosen,"start_time",startTime,"end_time",endTime,"day",selectedDay)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditMatkul.this, "Matkul berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditMatkul.this, Matkul.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditMatkul.this, "Gagal memperbarui Matkul: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void deleteMatkul() {
        db.collection("matkul").document(matkulId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditMatkul.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent ref = new Intent(EditMatkul.this,Matkul.class);
                        startActivity(ref);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("id : ",matkulId);
                        Toast.makeText(EditMatkul.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}