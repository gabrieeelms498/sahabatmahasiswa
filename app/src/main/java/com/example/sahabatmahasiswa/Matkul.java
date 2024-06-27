package com.example.sahabatmahasiswa;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Matkul extends AppCompatActivity {

    private static final String TAG = "Matkul";
    private static final int REQUEST_SCHEDULE_EXACT_ALARM = 1;

    private RecyclerView recyclerView;
    private MatkulAdapter matkulAdapter;
    private List<MatkulModel> matkulList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matkul);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btn_back);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        matkulList = new ArrayList<>();
        matkulAdapter = new MatkulAdapter(this, matkulList);
        recyclerView.setAdapter(matkulAdapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Matkul.this, Home.class);
                startActivity(back);
            }
        });
        Button addButton = findViewById(R.id.btn_AddMatkul);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Matkul.this, AddMatkul.class);
                startActivity(add);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission()) {
            Log.d(TAG, "Requesting exact alarm permission");
            requestExactAlarmPermission();
        } else {
            fetchDataFromFirestore();
        }
    }

    private boolean hasExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivityForResult(intent, REQUEST_SCHEDULE_EXACT_ALARM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCHEDULE_EXACT_ALARM) {
            if (hasExactAlarmPermission()) {
                Log.d(TAG, "Exact alarm permission granted");
                fetchDataFromFirestore();
            } else {
                Log.e(TAG, "Exact alarm permission not granted");
            }
        }
    }

    private void fetchDataFromFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            CollectionReference matkulRef = db.collection("matkul");
            matkulRef.whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                matkulList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MatkulModel matkul = document.toObject(MatkulModel.class);
                                    matkulList.add(matkul);
                                    scheduleNotification(Matkul.this, matkul, currentUserId);
                                }
                                matkulAdapter.notifyDataSetChanged();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    public static void scheduleNotification(Context context, MatkulModel matkul, String userId) {
        try {
            String day = matkul.getDay();
            String startTime = matkul.getStart_time();

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date startTimeDate = dateFormat.parse(startTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, startTimeDate.getHours());
            calendar.set(Calendar.MINUTE, startTimeDate.getMinutes());
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.HOUR_OF_DAY, -1);

            int targetDayOfWeek = getDayOfWeek(day);
            calendar.set(Calendar.DAY_OF_WEEK, targetDayOfWeek);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            long notificationTime = calendar.getTimeInMillis();

            Log.d(TAG, "Notification scheduled for: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(notificationTime)));

            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("matkulName", matkul.getMatkul());
            intent.putExtra("day", matkul.getDay());
            intent.putExtra("start_time", matkul.getStart_time());
            intent.putExtra("userId", userId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) notificationTime,
                    intent,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                            PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            }

            Log.d(TAG, "Exact notification scheduled for: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(notificationTime)));

        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification", e);
        }
    }

    private static int getDayOfWeek(String day) {
        switch (day.toLowerCase(Locale.ROOT)) {
            case "minggu":
                return Calendar.SUNDAY;
            case "senin":
                return Calendar.MONDAY;
            case "selasa":
                return Calendar.TUESDAY;
            case "rabu":
                return Calendar.WEDNESDAY;
            case "kamis":
                return Calendar.THURSDAY;
            case "jumat":
                return Calendar.FRIDAY;
            case "sabtu":
                return Calendar.SATURDAY;
            default:
                return Calendar.MONDAY;
        }
    }
}