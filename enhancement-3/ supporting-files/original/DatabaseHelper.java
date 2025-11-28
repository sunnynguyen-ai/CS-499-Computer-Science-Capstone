package com.example.projectthree_sunnynguyen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 3; // bumped for optimization changes

    // Event Table
    private static final String TABLE_EVENTS = "events";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_DESC = "description";
    private static final String COL_RECURRENCE = "recurrence_type";

    // Recurrence constants
    public static final String RECURRENCE_NONE = "NONE";
    public static final String RECURRENCE_DAILY = "DAILY";
    public static final String RECURRENCE_WEEKLY = "WEEKLY";
    public static final String RECURRENCE_MONTHLY = "MONTHLY";

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create events table
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_RECURRENCE + " TEXT DEFAULT '" + RECURRENCE_NONE + "')");

        // Add performance indexes
        db.execSQL("CREATE INDEX idx_events_date ON " + TABLE_EVENTS + " (" + COL_DATE + ")");
        db.execSQL("CREATE INDEX idx_events_time ON " + TABLE_EVENTS + " (" + COL_TIME + ")");
        db.execSQL("CREATE INDEX idx_events_recurrence ON " + TABLE_EVENTS + " (" + COL_RECURRENCE + ")");

        // Create users table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Rebuild database for changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Insert event (optimized)
    public long insertEvent(String name, String date, String time,
                            String description, String recurrenceType) {

        SQLiteDatabase db = this.getWritableDatabase();

        if (recurrenceType == null || recurrenceType.trim().isEmpty()) {
            recurrenceType = RECURRENCE_NONE;
        }

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_DESC, description);
        values.put(COL_RECURRENCE, recurrenceType);

        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    public long insertEvent(String name, String date, String time, String description) {
        return insertEvent(name, date, time, description, RECURRENCE_NONE);
    }

    // Get all events (optimized)
    public List<EventsGridActivity.Event> getAllEvents() {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_DATE + ", " + COL_TIME + ", " +
                        COL_DESC + ", " + COL_RECURRENCE +
                        " FROM " + TABLE_EVENTS +
                        " ORDER BY " + COL_DATE + ", " + COL_TIME,
                null
        );

        if (c.moveToFirst()) {
            int idxId = c.getColumnIndexOrThrow(COL_ID);
            int idxName = c.getColumnIndexOrThrow(COL_NAME);
            int idxDate = c.getColumnIndexOrThrow(COL_DATE);
            int idxTime = c.getColumnIndexOrThrow(COL_TIME);
            int idxDesc = c.getColumnIndexOrThrow(COL_DESC);
            int idxRecurrence = c.getColumnIndexOrThrow(COL_RECURRENCE);

            do {
                int id = c.getInt(idxId);
                String name = c.getString(idxName);
                String date = c.getString(idxDate);
                String time = c.getString(idxTime);
                String desc = c.getString(idxDesc);
                String recurrence = c.getString(idxRecurrence);

                if (recurrence == null || recurrence.isEmpty()) {
                    recurrence = RECURRENCE_NONE;
                }

                list.add(new EventsGridActivity.Event(id, name, date, time, desc, recurrence));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // Get events for a specific date (optimized)
    public List<EventsGridActivity.Event> getEventsForDate(String date) {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_TIME + ", "
                        + COL_DESC + ", " + COL_RECURRENCE +
                        " FROM " + TABLE_EVENTS +
                        " WHERE " + COL_DATE + "=? ORDER BY " + COL_TIME,
                new String[]{date}
        );

        if (c.moveToFirst()) {
            int idxId = c.getColumnIndexOrThrow(COL_ID);
            int idxName = c.getColumnIndexOrThrow(COL_NAME);
            int idxTime = c.getColumnIndexOrThrow(COL_TIME);
            int idxDesc = c.getColumnIndexOrThrow(COL_DESC);
            int idxRecurrence = c.getColumnIndexOrThrow(COL_RECURRENCE);

            do {
                int id = c.getInt(idxId);
                String name = c.getString(idxName);
                String time = c.getString(idxTime);
                String desc = c.getString(idxDesc);
                String recurrence = c.getString(idxRecurrence);

                if (recurrence == null || recurrence.isEmpty()) {
                    recurrence = RECURRENCE_NONE;
                }

                list.add(new EventsGridActivity.Event(id, name, date, time, desc, recurrence));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // Delete event (unchanged)
    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Create user
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Validate user
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_USER_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password}
        );

        boolean valid = c.moveToFirst();

        c.close();
        db.close();
        return valid;
    }
}