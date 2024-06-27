package com.example.sahabatmahasiswa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "MATKUL_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String matkulName = intent.getStringExtra("matkulName");
        String day = intent.getStringExtra("day");
        String userId = intent.getStringExtra("userId");
        String startTime = intent.getStringExtra("start_time");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getUid().equals(userId)) {
            MatkulModel matkul = new MatkulModel();
            matkul.setMatkul(matkulName);
            matkul.setDay(day);
            matkul.setStart_time(startTime);

            Toast.makeText(context, "Your class " + matkulName + " is starting in an hour.", Toast.LENGTH_SHORT).show();

            Intent notificationIntent = new Intent(context, Matkul.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE :
                            PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logonobg)
                    .setContentTitle("Pengingat Mata Kuliah")
                    .setContentText("Kelas " + matkulName + " Akan dimulai dalam satu jam.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Matkul Reminder Channel", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}