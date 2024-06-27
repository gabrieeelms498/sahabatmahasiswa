package com.example.sahabatmahasiswa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderWorker extends Worker {

    private static final String TAG = "ReminderWorker";
    private static final String CHANNEL_ID = "MATKUL_REMINDER_CHANNEL";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Worker started");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference matkulRef = db.collection("matkul");

            matkulRef.whereEqualTo("userId", currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MatkulModel matkul = document.toObject(MatkulModel.class);
                        scheduleNotification(matkul);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
        } else {
            Log.w(TAG, "No current user found");
        }
        return Result.success();
    }

    private void scheduleNotification(MatkulModel matkul) {
        try {
            String day = matkul.getDay();
            String startTime = matkul.getStart_time();

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date startTimeDate = dateFormat.parse(startTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTimeDate);
            calendar.add(Calendar.HOUR_OF_DAY, -1);

            long notificationTime = calendar.getTimeInMillis();

            Calendar now = Calendar.getInstance();
            int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int targetDayOfWeek = getDayOfWeek(day);

            if (targetDayOfWeek < currentDayOfWeek) {
                calendar.add(Calendar.DAY_OF_YEAR, 7 - (currentDayOfWeek - targetDayOfWeek));
            } else if (targetDayOfWeek > currentDayOfWeek) {
                calendar.add(Calendar.DAY_OF_YEAR, targetDayOfWeek - currentDayOfWeek);
            }

            if (calendar.getTimeInMillis() < now.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 7);
            }

            notificationTime = calendar.getTimeInMillis();

            Log.d(TAG, "Scheduling notification for " + matkul.getMatkul() + " at " + new Date(notificationTime).toString());

            Intent intent = new Intent(getApplicationContext(), Matkul.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    intent,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                            PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.eduspendslct)
                    .setContentTitle("Matkul Reminder")
                    .setContentText("Your class " + matkul.getMatkul() + " is starting in an hour.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Matkul Reminder Channel", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify((int) notificationTime, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification", e);
        }
    }

    private int getDayOfWeek(String day) {
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
