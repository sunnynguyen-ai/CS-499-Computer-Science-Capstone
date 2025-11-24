package com.example.projectthree_sunnynguyen;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventReminderReceiver extends BroadcastReceiver {

    private static final String EXTRA_RECURRENCE = "recurrence_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get event data from the intent
        String eventName = intent.getStringExtra("name");
        String eventTime = intent.getStringExtra("time");
        String recurrenceType = intent.getStringExtra(EXTRA_RECURRENCE);
        if (recurrenceType == null) {
            recurrenceType = DatabaseHelper.RECURRENCE_NONE;
        }

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

        // 🔁 Auto-generate next occurrence if this is a recurring event
        if (!DatabaseHelper.RECURRENCE_NONE.equals(recurrenceType)) {
            generateNextRecurringEvent(context, eventName, eventTime, recurrenceType);
        }
    }

    /**
     * Creates the next event row and schedules the next alarm based on recurrence type.
     * This matches the "generateNextRecurringEvent" idea from your script.
     */
    private void generateNextRecurringEvent(Context context,
                                            String eventName,
                                            String eventTime,
                                            String recurrenceType) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm", Locale.US);

        Calendar now = Calendar.getInstance();

        // Parse the time (HH:mm) into a Calendar
        Calendar timeCal = Calendar.getInstance();
        try {
            Date parsedTime = tf.parse(eventTime);
            if (parsedTime != null) {
                timeCal.setTime(parsedTime);
            }
        } catch (ParseException e) {
            // If time can't be parsed, just stop (no new alarm)
            return;
        }

        // Base = "now" date + original time
        Calendar next = Calendar.getInstance();
        next.set(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                timeCal.get(Calendar.HOUR_OF_DAY),
                timeCal.get(Calendar.MINUTE),
                0
        );
        next.set(Calendar.MILLISECOND, 0);

        // Move to the next recurrence slot
        switch (recurrenceType) {
            case DatabaseHelper.RECURRENCE_DAILY:
                next.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case DatabaseHelper.RECURRENCE_WEEKLY:
                next.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case DatabaseHelper.RECURRENCE_MONTHLY:
                next.add(Calendar.MONTH, 1);
                break;
            default:
                return;
        }

        String nextDateString = df.format(next.getTime());
        String nextTimeString = tf.format(next.getTime());

        // Insert next event row in SQLite
        DatabaseHelper helper = new DatabaseHelper(context);
        long newId = helper.insertEvent(
                eventName,
                nextDateString,
                nextTimeString,
                "",
                recurrenceType
        );

        // Schedule new alarm for the next occurrence
        Intent i = new Intent(context, EventReminderReceiver.class);
        i.putExtra("name", eventName);
        i.putExtra("time", nextTimeString);
        i.putExtra(EXTRA_RECURRENCE, recurrenceType);

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                (int) newId,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        long triggerAt = next.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= 31) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
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
