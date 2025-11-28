package com.example.projectthree_sunnynguyen;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SyncManager orchestrates bidirectional synchronization between local SQLite and remote API.
 * 
 * This class handles:
 * 1. Uploading local events to the server
 * 2. Downloading remote events from the server
 * 3. Merging remote events into local database
 * 4. Conflict resolution (simple: remote wins)
 */
public class SyncManager {

    private static final String TAG = "SyncManager";
    
    private final Context context;
    private final DatabaseHelper db;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface SyncCallback {
        void onSyncComplete(boolean success, String message);
    }

    public SyncManager(Context context) {
        this.context = context;
        this.db = new DatabaseHelper(context);
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Perform full bidirectional sync.
     * This method runs on a background thread and calls the callback on the main thread.
     */
    public void performSync(SyncCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Starting sync operation...");

                // Step 1: Upload unsynced local events to server
                boolean uploadSuccess = uploadLocalEvents();
                Log.d(TAG, "Upload step: " + (uploadSuccess ? "SUCCESS" : "FAILED"));

                // Step 2: Download remote events from server
                boolean downloadSuccess = downloadRemoteEvents();
                Log.d(TAG, "Download step: " + (downloadSuccess ? "SUCCESS" : "FAILED"));

                // Step 3: Update last sync timestamp
                if (uploadSuccess || downloadSuccess) {
                    db.updateLastSyncTimestamp();
                    Log.d(TAG, "Updated last sync timestamp");
                }

                // Report success
                boolean overallSuccess = uploadSuccess && downloadSuccess;
                String message = overallSuccess
                        ? "Sync completed successfully"
                        : "Sync completed with some errors";

                // Call callback on main thread
                mainHandler.post(() -> callback.onSyncComplete(overallSuccess, message));

            } catch (Exception e) {
                Log.e(TAG, "Sync failed with exception", e);
                mainHandler.post(() ->
                        callback.onSyncComplete(false, "Sync failed: " + e.getMessage()));
            }
        });
    }

    /**
     * Upload unsynced local events to the server.
     * Marks successfully uploaded events as SYNCED.
     * 
     * @return true if upload was successful
     */
    private boolean uploadLocalEvents() {
        try {
            // Get all unsynced events
            List<EventsGridActivity.Event> unsyncedEvents = db.getUnsyncedEvents();
            
            if (unsyncedEvents.isEmpty()) {
                Log.d(TAG, "No unsynced events to upload");
                return true; // Nothing to upload is considered success
            }

            Log.d(TAG, "Uploading " + unsyncedEvents.size() + " unsynced events...");

            // Upload events to server
            List<String> remoteIds = ApiService.uploadEvents(unsyncedEvents);

            // Mark successfully uploaded events as synced
            int successCount = 0;
            for (int i = 0; i < unsyncedEvents.size(); i++) {
                EventsGridActivity.Event event = unsyncedEvents.get(i);
                String remoteId = remoteIds.get(i);

                if (remoteId != null && !remoteId.isEmpty()) {
                    db.markEventAsSynced(event.id, remoteId);
                    successCount++;
                    Log.d(TAG, "Marked event " + event.id + " as synced with remote ID: " + remoteId);
                } else {
                    Log.w(TAG, "Failed to upload event " + event.id);
                }
            }

            Log.d(TAG, "Successfully uploaded " + successCount + "/" + unsyncedEvents.size() + " events");
            return successCount > 0 || unsyncedEvents.isEmpty();

        } catch (Exception e) {
            Log.e(TAG, "Error during upload", e);
            return false;
        }
    }

    /**
     * Download remote events from server and merge into local database.
     * Avoids duplicates by checking remote_id.
     * 
     * @return true if download was successful
     */
    private boolean downloadRemoteEvents() {
        try {
            Log.d(TAG, "Downloading events from server...");

            // Download events from server
            List<ApiService.RemoteEvent> remoteEvents = ApiService.downloadEvents();

            if (remoteEvents.isEmpty()) {
                Log.d(TAG, "No remote events to download");
                return true; // Nothing to download is considered success
            }

            Log.d(TAG, "Downloaded " + remoteEvents.size() + " remote events");

            // Insert new remote events into local database
            int insertedCount = 0;
            for (ApiService.RemoteEvent remoteEvent : remoteEvents) {
                // Check if event already exists locally
                if (!db.eventExistsByRemoteId(remoteEvent.remoteId)) {
                    long localId = db.insertEventFromRemote(
                            remoteEvent.remoteId,
                            remoteEvent.name,
                            remoteEvent.date,
                            remoteEvent.time,
                            remoteEvent.description,
                            remoteEvent.recurrenceType
                    );
                    
                    if (localId > 0) {
                        insertedCount++;
                        Log.d(TAG, "Inserted remote event: " + remoteEvent.name +
                                " (remote_id: " + remoteEvent.remoteId + ")");
                    }
                } else {
                    Log.d(TAG, "Event already exists: " + remoteEvent.remoteId);
                }
            }

            Log.d(TAG, "Inserted " + insertedCount + " new events from server");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error during download", e);
            return false;
        }
    }

    /**
     * Get last sync timestamp from database.
     * 
     * @return timestamp in milliseconds, or 0 if never synced
     */
    public long getLastSyncTimestamp() {
        return db.getLastSyncTimestamp();
    }

    /**
     * Clean up resources.
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
