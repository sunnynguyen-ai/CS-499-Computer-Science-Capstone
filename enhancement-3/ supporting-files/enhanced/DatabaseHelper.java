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
    private static final int DATABASE_VERSION = 4; // bumped for cloud sync support

    // Event Table
    private static final String TABLE_EVENTS = "events";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_DESC = "description";
    private static final String COL_RECURRENCE = "recurrence_type";
    // NEW: Cloud sync columns
    private static final String COL_REMOTE_ID = "remote_id";
    private static final String COL_SYNC_STATUS = "sync_status";
    private static final String COL_LAST_MODIFIED = "last_modified";

    // Sync status constants
    public static final String SYNC_STATUS_PENDING = "PENDING";
    public static final String SYNC_STATUS_SYNCED = "SYNCED";
    public static final String SYNC_STATUS_LOCAL_ONLY = "LOCAL_ONLY";

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

    // Sync metadata table
    private static final String TABLE_SYNC_META = "sync_metadata";
    private static final String COL_META_KEY = "meta_key";
    private static final String COL_META_VALUE = "meta_value";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create events table with sync columns
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_RECURRENCE + " TEXT DEFAULT '" + RECURRENCE_NONE + "', " +
                COL_REMOTE_ID + " TEXT, " +
                COL_SYNC_STATUS + " TEXT DEFAULT '" + SYNC_STATUS_PENDING + "', " +
                COL_LAST_MODIFIED + " INTEGER DEFAULT 0)");

        // Add performance indexes
        db.execSQL("CREATE INDEX idx_events_date ON " + TABLE_EVENTS + " (" + COL_DATE + ")");
        db.execSQL("CREATE INDEX idx_events_time ON " + TABLE_EVENTS + " (" + COL_TIME + ")");
        db.execSQL("CREATE INDEX idx_events_recurrence ON " + TABLE_EVENTS + " (" + COL_RECURRENCE + ")");
        // NEW: Index for sync operations
        db.execSQL("CREATE INDEX idx_events_sync_status ON " + TABLE_EVENTS + " (" + COL_SYNC_STATUS + ")");
        db.execSQL("CREATE INDEX idx_events_remote_id ON " + TABLE_EVENTS + " (" + COL_REMOTE_ID + ")");

        // Create users table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)");

        // NEW: Create sync metadata table
        db.execSQL("CREATE TABLE " + TABLE_SYNC_META + " (" +
                COL_META_KEY + " TEXT PRIMARY KEY, " +
                COL_META_VALUE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Rebuild database for changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC_META);
        onCreate(db);
    }

    // Insert event (with sync support)
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
        values.put(COL_SYNC_STATUS, SYNC_STATUS_PENDING);
        values.put(COL_LAST_MODIFIED, System.currentTimeMillis());

        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    public long insertEvent(String name, String date, String time, String description) {
        return insertEvent(name, date, time, description, RECURRENCE_NONE);
    }

    // NEW: Insert event from remote sync (with remote_id and synced status)
    public long insertEventFromRemote(String remoteId, String name, String date, String time,
                                      String description, String recurrenceType) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (recurrenceType == null || recurrenceType.trim().isEmpty()) {
            recurrenceType = RECURRENCE_NONE;
        }

        ContentValues values = new ContentValues();
        values.put(COL_REMOTE_ID, remoteId);
        values.put(COL_NAME, name);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_DESC, description);
        values.put(COL_RECURRENCE, recurrenceType);
        values.put(COL_SYNC_STATUS, SYNC_STATUS_SYNCED);
        values.put(COL_LAST_MODIFIED, System.currentTimeMillis());

        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    // Get all events (optimized with sync data)
    public List<EventsGridActivity.Event> getAllEvents() {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_DATE + ", " + COL_TIME + ", " +
                        COL_DESC + ", " + COL_RECURRENCE + ", " + COL_REMOTE_ID + ", " +
                        COL_SYNC_STATUS +
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
            int idxRemoteId = c.getColumnIndexOrThrow(COL_REMOTE_ID);
            int idxSyncStatus = c.getColumnIndexOrThrow(COL_SYNC_STATUS);

            do {
                int id = c.getInt(idxId);
                String name = c.getString(idxName);
                String date = c.getString(idxDate);
                String time = c.getString(idxTime);
                String desc = c.getString(idxDesc);
                String recurrence = c.getString(idxRecurrence);
                String remoteId = c.getString(idxRemoteId);
                String syncStatus = c.getString(idxSyncStatus);

                if (recurrence == null || recurrence.isEmpty()) {
                    recurrence = RECURRENCE_NONE;
                }
                if (syncStatus == null || syncStatus.isEmpty()) {
                    syncStatus = SYNC_STATUS_LOCAL_ONLY;
                }

                list.add(new EventsGridActivity.Event(id, name, date, time, desc, recurrence,
                        remoteId, syncStatus));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // Get events for a specific date (with sync data)
    public List<EventsGridActivity.Event> getEventsForDate(String date) {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_TIME + ", "
                        + COL_DESC + ", " + COL_RECURRENCE + ", " + COL_REMOTE_ID + ", " +
                        COL_SYNC_STATUS +
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
            int idxRemoteId = c.getColumnIndexOrThrow(COL_REMOTE_ID);
            int idxSyncStatus = c.getColumnIndexOrThrow(COL_SYNC_STATUS);

            do {
                int id = c.getInt(idxId);
                String name = c.getString(idxName);
                String time = c.getString(idxTime);
                String desc = c.getString(idxDesc);
                String recurrence = c.getString(idxRecurrence);
                String remoteId = c.getString(idxRemoteId);
                String syncStatus = c.getString(idxSyncStatus);

                if (recurrence == null || recurrence.isEmpty()) {
                    recurrence = RECURRENCE_NONE;
                }
                if (syncStatus == null || syncStatus.isEmpty()) {
                    syncStatus = SYNC_STATUS_LOCAL_ONLY;
                }

                list.add(new EventsGridActivity.Event(id, name, date, time, desc, recurrence,
                        remoteId, syncStatus));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // NEW: Get unsynced events (for uploading to server)
    public List<EventsGridActivity.Event> getUnsyncedEvents() {
        List<EventsGridActivity.Event> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_DATE + ", " + COL_TIME + ", " +
                        COL_DESC + ", " + COL_RECURRENCE + ", " + COL_REMOTE_ID + ", " +
                        COL_SYNC_STATUS +
                        " FROM " + TABLE_EVENTS +
                        " WHERE " + COL_SYNC_STATUS + "=? OR " + COL_SYNC_STATUS + "=?",
                new String[]{SYNC_STATUS_PENDING, SYNC_STATUS_LOCAL_ONLY}
        );

        if (c.moveToFirst()) {
            int idxId = c.getColumnIndexOrThrow(COL_ID);
            int idxName = c.getColumnIndexOrThrow(COL_NAME);
            int idxDate = c.getColumnIndexOrThrow(COL_DATE);
            int idxTime = c.getColumnIndexOrThrow(COL_TIME);
            int idxDesc = c.getColumnIndexOrThrow(COL_DESC);
            int idxRecurrence = c.getColumnIndexOrThrow(COL_RECURRENCE);
            int idxRemoteId = c.getColumnIndexOrThrow(COL_REMOTE_ID);
            int idxSyncStatus = c.getColumnIndexOrThrow(COL_SYNC_STATUS);

            do {
                int id = c.getInt(idxId);
                String name = c.getString(idxName);
                String date = c.getString(idxDate);
                String time = c.getString(idxTime);
                String desc = c.getString(idxDesc);
                String recurrence = c.getString(idxRecurrence);
                String remoteId = c.getString(idxRemoteId);
                String syncStatus = c.getString(idxSyncStatus);

                if (recurrence == null) recurrence = RECURRENCE_NONE;
                if (syncStatus == null) syncStatus = SYNC_STATUS_LOCAL_ONLY;

                list.add(new EventsGridActivity.Event(id, name, date, time, desc, recurrence,
                        remoteId, syncStatus));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    // NEW: Mark event as synced
    public void markEventAsSynced(int localId, String remoteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REMOTE_ID, remoteId);
        values.put(COL_SYNC_STATUS, SYNC_STATUS_SYNCED);
        values.put(COL_LAST_MODIFIED, System.currentTimeMillis());

        db.update(TABLE_EVENTS, values, COL_ID + "=?", new String[]{String.valueOf(localId)});
        db.close();
    }

    // NEW: Check if remote event already exists
    public boolean eventExistsByRemoteId(String remoteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_ID + " FROM " + TABLE_EVENTS +
                        " WHERE " + COL_REMOTE_ID + "=?",
                new String[]{remoteId}
        );
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        return exists;
    }

    // Delete event
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

    // NEW: Sync metadata methods
    public void setSyncMetadata(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_META_KEY, key);
        values.put(COL_META_VALUE, value);

        db.insertWithOnConflict(TABLE_SYNC_META, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getSyncMetadata(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_META_VALUE + " FROM " + TABLE_SYNC_META +
                        " WHERE " + COL_META_KEY + "=?",
                new String[]{key}
        );

        String value = null;
        if (c.moveToFirst()) {
            value = c.getString(0);
        }

        c.close();
        db.close();
        return value;
    }

    // NEW: Get last sync timestamp
    public long getLastSyncTimestamp() {
        String value = getSyncMetadata("last_sync_timestamp");
        return value != null ? Long.parseLong(value) : 0;
    }

    // NEW: Update last sync timestamp
    public void updateLastSyncTimestamp() {
        setSyncMetadata("last_sync_timestamp", String.valueOf(System.currentTimeMillis()));
    }
}
