package com.example.projectthree_sunnynguyen;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class EventReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get event data from the intent
        String eventName = intent.getStringExtra("name");
        String eventTime = intent.getStringExtra("time");

        // Load user preference for SMS notifications
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean smsEnabled = prefs.getBoolean("sms_enabled", false);

        if (smsEnabled &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
            // ✅ Send SMS reminder
            try {
                SmsManager smsManager = SmsManager.getDefault();
                // Replace "5555555555" with your test phone number or dynamic user number
                smsManager.sendTextMessage(
                        "5555555555",
                        null,
                        "Reminder: " + eventName + " at " + eventTime,
                        null,
                        null
                );
            } catch (Exception e) {
                e.printStackTrace();
                showNotification(context, "SMS failed",
                        "Could not send SMS for event: " + eventName);
            }
        } else {
            // 🚨 Fallback to app notification
            showNotification(context, "Event Today",
                    eventName + " at " + eventTime);
        }
    }

    private void showNotification(Context context, String title, String message) {
        String channelId = "event_channel";
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Event Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .build();

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}
