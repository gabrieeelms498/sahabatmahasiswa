package com.example.sahabatmahasiswa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskName = intent.getStringExtra("taskName");
        String taskDesc = intent.getStringExtra("taskDesc");
        String userId = intent.getStringExtra("userId");
        String startTime = intent.getStringExtra("taskTime");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(userId)) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("TASK_REMINDER_CHANNEL", "Task Reminder", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TASK_REMINDER_CHANNEL")
                    .setSmallIcon(R.drawable.logonobg)
                    .setContentTitle("Task Reminder")
                    .setContentText("Tenggat waktu tugas "+taskName + " : " + taskDesc+" pada jam "+startTime)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify((taskName + taskDesc).hashCode(), builder.build());
        } else {
            Log.d("ReminderBroadcast", "User not authenticated or user ID does not match");
        }
    }
}