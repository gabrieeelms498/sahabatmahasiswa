package com.example.sahabatmahasiswa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.text.ParseException;
public class AddTask extends AppCompatActivity {

    private Button dateButton;
    private Button timeButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button saveButton;
    int hour, minute;
    private Calendar selectedDateCalendar;
    private EditText edtNama, edtDesc;
    private String startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        saveButton = findViewById(R.id.btn_Simpan);
        edtNama = findViewById(R.id.edt_nama);
        edtDesc = findViewById(R.id.edt_Desc);
        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(AddTask.this, Task.class);
                startActivity(back);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirestore();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTimePicker();
            }
        });
    }

    private void saveDataToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(AddTask.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference newDocRef = db.collection("matkul").document();
        String userId = currentUser.getUid();
        String tugas = edtNama.getText().toString().trim();
        String deskripsi = edtDesc.getText().toString().trim();
        String newTaskId = newDocRef.getId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(selectedDateCalendar.getTime());

        Map<String, Object> tugasData = new HashMap<>();
        tugasData.put("userId", userId);
        tugasData.put("status", false);
        tugasData.put("tugas", tugas);
        tugasData.put("deskripsi", deskripsi);
        tugasData.put("day", dateString);
        tugasData.put("time", startTime);
        tugasData.put("taskId", newTaskId);

        db.collection("task").document(newTaskId)
                .set(tugasData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTask.this, "Tugas berhasil disimpan", Toast.LENGTH_SHORT).show();

                    setTaskReminder(tugas, deskripsi, dateString, startTime, userId, newTaskId);

                    Intent intent = new Intent(AddTask.this, Task.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.d("id : ", newTaskId);
                    Log.d("userId : ", userId);
                    Log.d("tugas : ", tugas);
                    Log.d("deskripsi : ", deskripsi);
                    Log.d("time : ", startTime);
                    Log.d("day : ", dateString);
                    Toast.makeText(AddTask.this, "Gagal menambahkan mata kuliah", Toast.LENGTH_SHORT).show();
                });
    }





    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDateCalendar = Calendar.getInstance();
                        selectedDateCalendar.set(Calendar.YEAR, year);
                        selectedDateCalendar.set(Calendar.MONTH, monthOfYear);
                        selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                        String dayOfWeek = dayFormat.format(selectedDateCalendar.getTime());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
                        dateButton.setText(dateFormat.format(selectedDateCalendar.getTime()));
                    }
                }, year, month, day);

        datePickerDialog.show();
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
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestExactAlarmPermission() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (!alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }
    private void setTaskReminder(String taskName, String taskDesc, String dateString, String startTime, String userId, String taskId) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateTimeFormat.parse(dateString + " " + startTime);
            if (date != null) {
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, -2);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
                intent.putExtra("taskName", taskName);
                intent.putExtra("taskDesc", taskDesc);
                intent.putExtra("userId", userId);
                intent.putExtra("taskId", taskId);
                intent.putExtra("taskTime",startTime);

                int requestCode = taskId.hashCode();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        requestExactAlarmPermission();
                    }
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}