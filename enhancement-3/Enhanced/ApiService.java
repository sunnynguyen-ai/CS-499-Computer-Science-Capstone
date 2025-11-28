package com.example.projectthree_sunnynguyen;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ApiService handles HTTP communication with the remote backend API.
 * 
 * NOTE: This is a demonstration/mock implementation for CS 499.
 * In production, you would replace the mock API URL with a real backend service.
 */
public class ApiService {

    private static final String TAG = "ApiService";
    
    // MOCK API URL - Replace with actual backend endpoint in production
    // For demo purposes, this uses a mock/test endpoint
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    
    // Timeout values
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 10000;    // 10 seconds

    /**
     * Upload (POST) events to the remote server.
     * Returns list of remote IDs assigned to each event.
     */
    public static List<String> uploadEvents(List<EventsGridActivity.Event> events) {
        List<String> remoteIds = new ArrayList<>();

        for (EventsGridActivity.Event event : events) {
            try {
                String remoteId = uploadSingleEvent(event);
                remoteIds.add(remoteId);
                Log.d(TAG, "Uploaded event: " + event.name + " -> remoteId: " + remoteId);
            } catch (Exception e) {
                Log.e(TAG, "Error uploading event: " + event.name, e);
                remoteIds.add(null); // Failed upload
            }
        }

        return remoteIds;
    }

    /**
     * Upload a single event to the server.
     * Returns the remote ID assigned by the server.
     */
    private static String uploadSingleEvent(EventsGridActivity.Event event) throws Exception {
        // MOCK IMPLEMENTATION: Using JSONPlaceholder for demonstration
        // In production, replace with your actual API endpoint
        URL url = new URL(BASE_URL + "/posts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            // Build JSON payload
            JSONObject json = new JSONObject();
            json.put("title", event.name);
            json.put("body", event.date + " " + event.time);
            json.put("userId", 1); // Mock user ID

            // Write request body
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                // Parse response to get remote ID
                JSONObject responseJson = new JSONObject(response.toString());
                String remoteId = responseJson.optString("id", null);
                
                // MOCK: Generate a unique ID if the mock API doesn't return one
                if (remoteId == null || remoteId.isEmpty()) {
                    remoteId = "remote_" + System.currentTimeMillis() + "_" + event.id;
                }
                
                return remoteId;
            } else {
                throw new Exception("Upload failed with response code: " + responseCode);
            }

        } finally {
            conn.disconnect();
        }
    }

    /**
     * Download (GET) events from the remote server.
     * Returns list of events from the server.
     */
    public static List<RemoteEvent> downloadEvents() {
        List<RemoteEvent> remoteEvents = new ArrayList<>();

        try {
            // MOCK IMPLEMENTATION: Using JSONPlaceholder for demonstration
            // In production, replace with your actual API endpoint
            URL url = new URL(BASE_URL + "/posts?userId=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    // Parse JSON array response
                    JSONArray jsonArray = new JSONArray(response.toString());
                    
                    // MOCK: Limit to 5 events for demo purposes
                    int limit = Math.min(jsonArray.length(), 5);
                    
                    for (int i = 0; i < limit; i++) {
                        JSONObject eventJson = jsonArray.getJSONObject(i);
                        RemoteEvent remoteEvent = parseRemoteEvent(eventJson);
                        if (remoteEvent != null) {
                            remoteEvents.add(remoteEvent);
                        }
                    }

                    Log.d(TAG, "Downloaded " + remoteEvents.size() + " events from server");
                } else {
                    Log.e(TAG, "Download failed with response code: " + responseCode);
                }

            } finally {
                conn.disconnect();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error downloading events", e);
        }

        return remoteEvents;
    }

    /**
     * Parse a JSON object into a RemoteEvent.
     * Handles the mock API format.
     */
    private static RemoteEvent parseRemoteEvent(JSONObject json) {
        try {
            String remoteId = json.optString("id", null);
            String title = json.optString("title", "Remote Event");
            String body = json.optString("body", "");

            // MOCK: Parse mock date/time from body or use defaults
            // In production, your API would return structured date/time fields
            String date = "01/01/2025"; // Default date
            String time = "12:00";      // Default time
            String description = body;
            String recurrence = DatabaseHelper.RECURRENCE_NONE;

            return new RemoteEvent(remoteId, title, date, time, description, recurrence);

        } catch (Exception e) {
            Log.e(TAG, "Error parsing remote event", e);
            return null;
        }
    }

    /**
     * Simple data class for remote events.
     */
    public static class RemoteEvent {
        public String remoteId;
        public String name;
        public String date;
        public String time;
        public String description;
        public String recurrenceType;

        public RemoteEvent(String remoteId, String name, String date, String time,
                           String description, String recurrenceType) {
            this.remoteId = remoteId;
            this.name = name;
            this.date = date;
            this.time = time;
            this.description = description;
            this.recurrenceType = recurrenceType;
        }
    }
}
