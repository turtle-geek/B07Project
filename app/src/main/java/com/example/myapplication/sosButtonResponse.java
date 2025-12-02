package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.ChildUI.TriageAndResponse.TriageActivity;

public class sosButtonResponse extends AppCompatActivity {
    public sosButtonResponse(){};
    public void response(String id, Context context){
        Intent intent = new Intent(context, TriageActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        scheduleCheckupNotification(context);
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleCheckupNotification(Context context) {
        long triggerTime = System.currentTimeMillis() + 10 * 60 * 1000; // 10 minutes
        Intent intent = new Intent(context, CheckupNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}
