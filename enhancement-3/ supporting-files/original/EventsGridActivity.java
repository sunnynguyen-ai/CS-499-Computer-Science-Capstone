package com.example.projectthree_sunnynguyen;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsGridActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etName, etDate, etTime;
    private Spinner spRecurrence;
    private Button btnAdd;

    private DatabaseHelper db;
    private EventsAdapter adapter;
    private final List<Event> events = new ArrayList<>();

    private final SimpleDateFormat DF = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private final SimpleDateFormat TF = new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_grid);

        // Toolbar (enables 3-dot overflow menu)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ask for notification permission on Android 13+
        requestPostNotificationsIfNeeded();

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewEvents);
        etName = findViewById(R.id.editTextEventName);
        etDate = findViewById(R.id.editTextEventDate);
        etTime = findViewById(R.id.editTextEventTime);
        spRecurrence = findViewById(R.id.spinnerRecurrence);
        btnAdd = findViewById(R.id.buttonAddEvent);

        // Setup recurrence spinner
        String[] recurrenceOptions = new String[]{
                "Does not repeat",
                "Daily",
                "Weekly",
                "Monthly"
        };
        ArrayAdapter<String> recurrenceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                recurrenceOptions
        );
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRecurrence.setAdapter(recurrenceAdapter);

        adapter = new EventsAdapter(events, e -> {
            db.deleteEvent(e.id);
            events.remove(e);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> addEvent());

        loadAll();
    }

    private void requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        2002
                );
            }
        }
    }

    private void addEvent() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim(); // MM/DD/YYYY
        String time = etTime.getText().toString().trim(); // HH:MM (24h)

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Enter name, date, and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Map spinner selection to recurrence constants
        String recurrenceType;
        int pos = spRecurrence.getSelectedItemPosition();
        switch (pos) {
            case 1:
                recurrenceType = DatabaseHelper.RECURRENCE_DAILY;
                break;
            case 2:
                recurrenceType = DatabaseHelper.RECURRENCE_WEEKLY;
                break;
            case 3:
                recurrenceType = DatabaseHelper.RECURRENCE_MONTHLY;
                break;
            default:
                recurrenceType = DatabaseHelper.RECURRENCE_NONE;
                break;
        }

        Date d, t;
        try {
            d = DF.parse(date);
            t = TF.parse(time);
        } catch (ParseException e) {
            Toast.makeText(this, "Use MM/DD/YYYY and HH:MM (24-hour)", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = db.insertEvent(name, date, time, "", recurrenceType);
        Event ev = new Event((int) id, name, date, time, "", recurrenceType);
        events.add(ev);
        adapter.notifyItemInserted(events.size() - 1);

        Calendar when = merge(d, t);
        scheduleAlarm(id, name, when, recurrenceType);

        etName.setText("");
        etDate.setText("");
        etTime.setText("");
        spRecurrence.setSelection(0);
        Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
    }

    private Calendar merge(Date d, Date t) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(d);
        Calendar ct = Calendar.getInstance();
        ct.setTime(t);
        Calendar out = Calendar.getInstance();
        out.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),
                cd.get(Calendar.DAY_OF_MONTH),
                ct.get(Calendar.HOUR_OF_DAY), ct.get(Calendar.MINUTE), 0);
        return out;
    }

    /**
     * Schedules a reminder for the event.
     * - API 31+ (Android 12+): use INEXACT alarm to avoid SCHEDULE_EXACT_ALARM requirement.
     * - API 23â€“30: use setExactAndAllowWhileIdle.
     * - API <23: use setExact.
     */
    private void scheduleAlarm(long id, String name, Calendar when, String recurrenceType) {
        Intent i = new Intent(this, EventReminderReceiver.class);
        i.putExtra("name", name);
        i.putExtra("time", TF.format(when.getTime()));
        i.putExtra("recurrence_type", recurrenceType);

        PendingIntent pi = PendingIntent.getBroadcast(
                this, (int) id, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am == null) return;

        long triggerAt = when.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= 31) {
            // No special permission needed; slightly inexact but perfect for the course demo
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            // Optionally, tighten the window:
            // am.setWindow(AlarmManager.RTC_WAKEUP, triggerAt, 60_000L, pi);
        } else if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    private void loadAll() {
        events.clear();
        events.addAll(db.getAllEvents());
        adapter.notifyDataSetChanged();
    }

    // ===== Menu (overflow / 3-dot) =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sms_settings) {
            startActivity(new Intent(this, SmsPermissionActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_send_alerts) {
            sendTodaysAlerts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendTodaysAlerts() {
        String today = DF.format(new Date());
        List<Event> list = db.getEventsForDate(today);
        if (list.isEmpty()) {
            Toast.makeText(this, "No events for today", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Event e : list) {
            Intent i = new Intent(this, EventReminderReceiver.class);
            i.putExtra("name", e.name);
            i.putExtra("time", e.time);
            i.putExtra("recurrence_type", e.recurrenceType);
            sendBroadcast(i);
        }
        Toast.makeText(this, "Today's alerts sent", Toast.LENGTH_SHORT).show();
    }

    // ===== Simple Event model =====
    public static class Event {
        public int id;
        public String name, date, time, desc;
        // New: recurrence type for this event
        public String recurrenceType;

        public Event(int id, String name, String date, String time,
                     String desc, String recurrenceType) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.time = time;
            this.desc = desc;
            this.recurrenceType = recurrenceType;
        }
    }
}