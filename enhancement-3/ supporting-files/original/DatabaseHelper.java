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
    private static final int DATABASE_VERSION = 1;

    // Event Table
    private static final String TABLE_EVENTS = "events";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_DESC = "description";

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
                COL_DESC + " TEXT)");

        // Create users table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ✅ Insert event
    public long insertEvent(String name, String date, String time, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_DESC, description);
        return db.insert(TABLE_EVENTS, null, values);
    }

    // ✅ Get all events
    public List<EventsGridActivity.Event> getAllEvents() {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " ORDER BY " + COL_DATE + ", " + COL_TIME, null);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
                String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
                String date = c.getString(c.getColumnIndexOrThrow(COL_DATE));
                String time = c.getString(c.getColumnIndexOrThrow(COL_TIME));
                String desc = c.getString(c.getColumnIndexOrThrow(COL_DESC));
                list.add(new EventsGridActivity.Event(id, name, date, time, desc));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // ✅ Get events for a specific date (for “Send Today’s Alerts”)
    public List<EventsGridActivity.Event> getEventsForDate(String date) {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE " + COL_DATE + "=? ORDER BY " + COL_TIME,
                new String[]{date});

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
                String name = c.getString(c.getColumnIndexOrThrow(COL_NAME));
                String time = c.getString(c.getColumnIndexOrThrow(COL_TIME));
                String desc = c.getString(c.getColumnIndexOrThrow(COL_DESC));
                list.add(new EventsGridActivity.Event(id, name, date, time, desc));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // ✅ Delete event
    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ✅ Register new user
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // ✅ Validate user for login
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        boolean ok = c.moveToFirst();
        c.close();
        db.close();
        return ok;
    }
}
