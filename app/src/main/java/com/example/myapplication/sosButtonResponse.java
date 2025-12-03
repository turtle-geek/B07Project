package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import android.util.Log;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.ChildUI.TriageAndResponse.TriageActivity;

public class sosButtonResponse {

    private static final String TAG = "SOSResponse";
    private static final int CHECKUP_NOTIFICATION_REQUEST_CODE = 1;

    public sosButtonResponse(){};

    public void response(String id, Context context){
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "Error: User ID is missing for SOS.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(context, TriageActivity.class);
        intent.putExtra("id", id);

        if (!(context instanceof AppCompatActivity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);

        scheduleCheckupNotification(context);
    }

    @SuppressLint({"ScheduleExactAlarm"})
    private void scheduleCheckupNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {

                Log.e(TAG, "Missing SCHEDULE_EXACT_ALARM permission for API 31+. Directing user to settings.");
                Toast.makeText(context, "Please grant 'Alarms & reminders' permission in settings to schedule a follow-up check.", Toast.LENGTH_LONG).show();

                Intent settingsIntent = new Intent(
                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + context.getPackageName())
                );

                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(settingsIntent);

                return;
            }
        }

        long triggerTime = System.currentTimeMillis() + 10 * 60 * 1000;

        Intent intent = new Intent(context, com.example.myapplication.CheckupNotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                CHECKUP_NOTIFICATION_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Log.d(TAG, "Scheduled 10-minute checkup notification.");
        } else {
            Log.e(TAG, "AlarmManager service is null.");
            Toast.makeText(context, "Could not schedule checkup alarm.", Toast.LENGTH_SHORT).show();
        }
    }
}