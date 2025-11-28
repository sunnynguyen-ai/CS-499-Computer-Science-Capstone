# Enhancement One — Software Engineering & Design: Recurring Events Implementation

## Overview

Enhancement One transforms the Event Tracker Android application by implementing a comprehensive recurring events system, demonstrating advanced software engineering principles and algorithmic design. This enhancement adds the ability for users to create events that automatically repeat on daily, weekly, or monthly schedules—a fundamental feature expected in modern calendar and event management applications.

Before this enhancement, the application could only handle one-time events. Users had to manually create separate entries for events that occurred regularly (like weekly meetings or monthly appointments), leading to data redundancy, increased user effort, and potential inconsistencies. This enhancement implements automatic recurrence generation using algorithmic date manipulation, database schema evolution, and integrated notification scheduling.

---

## 1. Description of the Original Artifact (Before Enhancement One)

The original implementation (base application state) had these characteristics:

**Database Version:** 1
- Basic event storage (name, date, time, description)
- No recurrence support
- Single-occurrence events only
- User authentication system
- SMS/notification reminders
- Simple CRUD operations

**Key Limitations:**
- No way to create repeating events
- Users had to manually duplicate recurring appointments
- No automatic generation of future event instances
- Missing fundamental calendar application feature
- Reduced usability for real-world scheduling needs

**Specific Technical Gaps:**

**Database Schema (v1):**
```sql
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    date TEXT,
    time TEXT,
    description TEXT
);
```
- Missing recurrence information
- No way to track event series
- Each event completely independent

**Event Model:**
```java
public static class Event {
    public int id;
    public String name, date, time, desc;
    // No recurrence field
}
```

**EventReminderReceiver:**
- Triggered notification/SMS for single event
- No logic for generating next occurrence
- Alarm dismissed after firing

**UI (EventsGridActivity):**
- No recurrence selection option
- User creates one event at a time
- No indication that events could repeat

Although functional for one-time events, the application lacked a core feature that users expect from any scheduling system, limiting its practical utility and professional quality.

---

## 2. Summary of Enhancements

This enhancement implements a complete recurring events system through coordinated changes across multiple components:

### Database Schema Evolution (v1 → v2)

Added recurrence support to the events table:

**New Column:**
- **recurrence_type** (TEXT, default 'NONE') — Stores recurrence pattern

**Recurrence Constants:**
```java
public static final String RECURRENCE_NONE = "NONE";
public static final String RECURRENCE_DAILY = "DAILY";
public static final String RECURRENCE_WEEKLY = "WEEKLY";
public static final String RECURRENCE_MONTHLY = "MONTHLY";
```

**Schema Update:**
```sql
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    date TEXT,
    time TEXT,
    description TEXT,
    recurrence_type TEXT DEFAULT 'NONE'  -- NEW column
);
```

**Migration Strategy:**
- Bumped `DATABASE_VERSION` from 1 to 2
- Added DEFAULT value ensures backward compatibility
- Existing events automatically get RECURRENCE_NONE

### Algorithmic Recurrence Generation

Implemented automatic next-occurrence generation using date manipulation algorithms:

**Core Algorithm (generateNextRecurringEvent method):**
```java
private void generateNextRecurringEvent(String name, String dateStr, 
                                       String timeStr, String desc, 
                                       String recurrenceType) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date currentDate = sdf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        
        // Apply recurrence algorithm based on type
        switch (recurrenceType) {
            case DatabaseHelper.RECURRENCE_DAILY:
                calendar.add(Calendar.DAY_OF_YEAR, 1);  // Add 1 day
                break;
            case DatabaseHelper.RECURRENCE_WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, 1); // Add 1 week
                break;
            case DatabaseHelper.RECURRENCE_MONTHLY:
                calendar.add(Calendar.MONTH, 1);        // Add 1 month
                break;
            default:
                return; // No recurrence - exit
        }
        
        // Generate next date string
        String nextDateStr = sdf.format(calendar.getTime());
        
        // Insert next event occurrence
        DatabaseHelper db = new DatabaseHelper(context);
        db.insertEvent(name, nextDateStr, timeStr, desc, recurrenceType);
        
        // Schedule alarm for next occurrence
        scheduleAlarm(context, name, nextDateStr, timeStr, recurrenceType);
        
    } catch (ParseException e) {
        Log.e(TAG, "Error generating recurring event", e);
    }
}
```

**Algorithmic Approach:**
1. Parse current event date string to Date object
2. Convert to Calendar for date manipulation
3. Use Calendar.add() with appropriate field and value
4. Format back to string representation
5. Insert new event with same recurrence type
6. Schedule next alarm

**Design Choice: Calendar.add() vs. Simple Arithmetic**

Chose `Calendar.add()` over manual date arithmetic because:
- Handles month boundaries correctly (e.g., January 31 → February 28/29)
- Accounts for varying month lengths automatically
- Respects leap years without special logic
- Handles year rollovers seamlessly
- Industry-standard approach for date manipulation

### Event Notification Integration

Enhanced EventReminderReceiver to support recurring events:

**Modified onReceive() Method:**
```java
@Override
public void onReceive(Context context, Intent intent) {
    String name = intent.getStringExtra(EXTRA_NAME);
    String date = intent.getStringExtra(EXTRA_DATE);
    String time = intent.getStringExtra(EXTRA_TIME);
    String recurrenceType = intent.getStringExtra(EXTRA_RECURRENCE); // NEW
    
    // Send notification/SMS (existing logic)
    sendNotification(context, name, date, time);
    sendSMS(context, name, date, time);
    
    // NEW: Generate next occurrence for recurring events
    if (recurrenceType != null && 
        !recurrenceType.equals(DatabaseHelper.RECURRENCE_NONE)) {
        generateNextRecurringEvent(name, date, time, "", recurrenceType);
    }
}
```

**Recurrence Chain:**
1. Alarm fires for current event
2. Notification/SMS sent to user
3. Check if event has recurrence type
4. If recurring: generate next occurrence
5. Next occurrence gets its own alarm
6. Process repeats indefinitely (or until user deletes)

### User Interface Enhancement

Added recurrence selection to event creation:

**UI Component (Spinner):**
```java
// In EventsGridActivity
Spinner spRecurrence;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_events_grid);
    
    // Initialize recurrence spinner
    spRecurrence = findViewById(R.id.spRecurrence);
    
    // Populate with options
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        this,
        R.array.recurrence_options,  // {"Does not repeat", "Daily", "Weekly", "Monthly"}
        android.R.layout.simple_spinner_item
    );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spRecurrence.setAdapter(adapter);
}
```

**Add Event Logic:**
```java
private void addEvent() {
    String name = etName.getText().toString().trim();
    String date = etDate.getText().toString().trim();
    String time = etTime.getText().toString().trim();
    
    // Map spinner selection to recurrence constant
    String recurrenceType;
    int position = spRecurrence.getSelectedItemPosition();
    switch (position) {
        case 0: recurrenceType = DatabaseHelper.RECURRENCE_NONE; break;
        case 1: recurrenceType = DatabaseHelper.RECURRENCE_DAILY; break;
        case 2: recurrenceType = DatabaseHelper.RECURRENCE_WEEKLY; break;
        case 3: recurrenceType = DatabaseHelper.RECURRENCE_MONTHLY; break;
        default: recurrenceType = DatabaseHelper.RECURRENCE_NONE;
    }
    
    // Insert with recurrence type
    long id = db.insertEvent(name, date, time, "", recurrenceType);
    
    // Schedule alarm with recurrence info
    scheduleAlarm(this, name, date, time, recurrenceType);
    
    // Refresh UI
    loadAll();
}
```

**User Experience:**
- Clear dropdown menu with plain language options
- Default: "Does not repeat" (backward compatible UX)
- Visual feedback that events can recur
- Simple, intuitive interface

### Event Model Extension

Updated Event class to include recurrence:

**Enhanced Event Class:**
```java
public static class Event {
    public int id;
    public String name, date, time, desc;
    public String recurrenceType;  // NEW field
    
    public Event(int id, String name, String date, String time, 
                 String desc, String recurrenceType) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.desc = desc;
        this.recurrenceType = recurrenceType;  // Store recurrence info
    }
}
```

**Database Methods Updated:**
```java
// Overloaded insertEvent for backward compatibility
public long insertEvent(String name, String date, String time, String desc) {
    return insertEvent(name, date, time, desc, RECURRENCE_NONE);
}

public long insertEvent(String name, String date, String time, 
                       String desc, String recurrenceType) {
    ContentValues cv = new ContentValues();
    cv.put(COL_NAME, name);
    cv.put(COL_DATE, date);
    cv.put(COL_TIME, time);
    cv.put(COL_DESC, desc);
    cv.put(COL_RECURRENCE, recurrenceType);  // Include recurrence
    
    SQLiteDatabase db = this.getWritableDatabase();
    long id = db.insert(TABLE_EVENTS, null, cv);
    db.close();
    return id;
}

// getAllEvents() updated to retrieve recurrence
public List<EventsGridActivity.Event> getAllEvents() {
    List<EventsGridActivity.Event> list = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    
    Cursor c = db.rawQuery(
        "SELECT id, name, date, time, description, recurrence_type " +
        "FROM " + TABLE_EVENTS + " ORDER BY date, time",
        null
    );
    
    if (c.moveToFirst()) {
        do {
            int id = c.getInt(0);
            String name = c.getString(1);
            String date = c.getString(2);
            String time = c.getString(3);
            String desc = c.getString(4);
            String recurrence = c.getString(5);  // Retrieve recurrence
            
            list.add(new EventsGridActivity.Event(
                id, name, date, time, desc, recurrence
            ));
        } while (c.moveToNext());
    }
    
    c.close();
    db.close();
    return list;
}
```

---

## 3. Technical Implementation Details

### Date Manipulation Algorithm

**Calendar.add() Behavior:**

**Daily Recurrence:**
```java
calendar.add(Calendar.DAY_OF_YEAR, 1);

Examples:
12/31/2024 → 01/01/2025  (handles year rollover)
02/28/2024 → 02/29/2024  (leap year aware)
02/28/2025 → 03/01/2025  (non-leap year)
```

**Weekly Recurrence:**
```java
calendar.add(Calendar.WEEK_OF_YEAR, 1);

Examples:
Monday 12/30/2024 → Monday 01/06/2025  (same day of week)
Friday 12/27/2024 → Friday 01/03/2025  (crosses year boundary)
```

**Monthly Recurrence:**
```java
calendar.add(Calendar.MONTH, 1);

Examples:
01/15/2024 → 02/15/2024  (normal case)
01/31/2024 → 02/29/2024  (leap year, day adjustment)
01/31/2025 → 02/28/2025  (non-leap year, day adjustment)
03/31/2024 → 04/30/2024  (shorter month, day adjustment)
```

**Edge Case Handling:**

Calendar.add() automatically handles:
- **Month boundaries:** Correctly advances from month to month
- **Varying month lengths:** Adjusts day when target month is shorter
- **Leap years:** Respects February 29th in leap years
- **Year boundaries:** Seamlessly rolls over December → January
- **Day-of-week consistency:** Weekly recurrence maintains same weekday

### Alarm Scheduling Integration

**Passing Recurrence to Alarms:**
```java
private void scheduleAlarm(Context context, String name, String date, 
                          String time, String recurrenceType) {
    try {
        // Parse date and time
        SimpleDateFormat sdf = new SimpleDateFormat(
            "MM/dd/yyyy HH:mm", Locale.US
        );
        Date eventDate = sdf.parse(date + " " + time);
        
        // Create intent for EventReminderReceiver
        Intent intent = new Intent(context, EventReminderReceiver.class);
        intent.putExtra(EventReminderReceiver.EXTRA_NAME, name);
        intent.putExtra(EventReminderReceiver.EXTRA_DATE, date);
        intent.putExtra(EventReminderReceiver.EXTRA_TIME, time);
        intent.putExtra(EventReminderReceiver.EXTRA_RECURRENCE, recurrenceType); // NEW
        
        // Create PendingIntent
        PendingIntent pi = PendingIntent.getBroadcast(
            context,
            (int) System.currentTimeMillis(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Schedule alarm
        AlarmManager alarmManager = (AlarmManager) 
            context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            eventDate.getTime(),
            pi
        );
        
    } catch (ParseException e) {
        Log.e(TAG, "Error scheduling alarm", e);
    }
}
```

### Backward Compatibility Strategy

**Database Migration:**
```java
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 2) {
        // Add recurrence_type column with default value
        db.execSQL("ALTER TABLE " + TABLE_EVENTS + 
                  " ADD COLUMN " + COL_RECURRENCE + 
                  " TEXT DEFAULT '" + RECURRENCE_NONE + "'");
    }
}
```

**Method Overloading:**
```java
// Old code still works (calls new version with NONE)
db.insertEvent("Meeting", "12/15/2024", "14:00", "Team sync");

// New code can specify recurrence
db.insertEvent("Meeting", "12/15/2024", "14:00", "Team sync", "WEEKLY");
```

**UI Defaults:**
- Spinner defaults to "Does not repeat" (position 0)
- Existing workflows unchanged
- New feature discoverable but not intrusive

---

## 4. Course Outcomes Addressed

### Course Outcome 3 — Design and evaluate computing solutions using algorithmic principles and computer science practices

This enhancement addresses CO-03 through multiple dimensions:

**Algorithmic Design:**

**Date Manipulation Algorithm:**
- Selected Calendar.add() as the appropriate algorithm for date arithmetic
- Analyzed alternative approaches (manual day counting, third-party libraries)
- Chose solution that correctly handles edge cases without complex logic

**Design Trade-offs Evaluated:**

**Calendar.add() vs. setRepeating():**
- **Option 1:** Use AlarmManager.setRepeating() for built-in recurrence
- **Option 2:** Manual generation with Calendar.add()

**Decision:** Manual generation (Option 2)

**Reasoning:**
- setRepeating() limited to fixed intervals (not flexible for monthly with day adjustment)
- Manual generation allows database persistence of each occurrence
- Users can view/edit future occurrences individually
- Better integration with existing event management system
- Trade-off: More code complexity for better user control

**Extensibility vs. Simplicity:**
- **Simple approach:** Hard-code DAILY, WEEKLY, MONTHLY only
- **Extensible approach:** Use string constants, easy to add BIWEEKLY, YEARLY later

**Decision:** Extensible approach

**Reasoning:**
- Minimal additional complexity (string constants vs. enum)
- Easy to add new recurrence types in future
- Database schema supports arbitrary recurrence strings
- UI can be updated without database changes

**Modular Design:**
- Separated recurrence generation into dedicated method
- Can be unit tested independently
- Reusable across different notification triggers
- Follows single responsibility principle

**Computer Science Practices:**

**Design Patterns Applied:**
- **Factory pattern:** generateNextRecurringEvent() creates new event instances
- **Strategy pattern:** Switch statement selects algorithm based on recurrence type
- **Builder pattern:** Calendar construction and configuration

**Software Engineering Principles:**
- **DRY (Don't Repeat Yourself):** Single method generates all recurrence types
- **SRP (Single Responsibility):** Each method has one clear purpose
- **OCP (Open/Closed):** Easy to extend with new recurrence types without modifying existing code

### Course Outcome 4 — Demonstrate ability to use well-founded and innovative techniques

This enhancement addresses CO-04 by implementing:

**Industry-Standard Techniques:**
- Recurring events are fundamental to calendar applications (Google Calendar, Outlook, Apple Calendar)
- Date manipulation using Calendar API follows Java best practices
- Alarm scheduling uses Android's AlarmManager correctly
- Database schema evolution through version management

**Delivering Value:**
- Dramatically improves user experience (create once vs. manually duplicating events)
- Reduces data entry effort for users
- Eliminates inconsistencies from manual duplication
- Provides feature parity with commercial calendar apps

**Professional Implementation:**
- Backward compatible database migration
- Method overloading for API compatibility
- Clear UI labels matching user expectations
- Comprehensive error handling

---

## 5. Reflection on the Enhancement Process

### Learning and Growth

Implementing recurring events deepened my understanding of date/time manipulation algorithms and the complexity involved in seemingly simple features. Before this enhancement, I hadn't fully appreciated how many edge cases exist in calendar arithmetic—month boundaries, leap years, variable month lengths—and how the Calendar API abstracts these complexities.

The most valuable learning came from analyzing design trade-offs. The decision between using setRepeating() versus manual generation required evaluating user needs, system architecture, and future extensibility. Understanding that "simpler code" doesn't always mean "better solution" was an important lesson. Sometimes the technically simpler approach (setRepeating()) provides less value than a more complex implementation that better serves user needs.

Integrating the recurrence feature across multiple components (database, business logic, UI, notifications) taught me about system thinking and component coupling. Changes in one area (database schema) rippled through the entire application, requiring careful coordination to maintain consistency and backward compatibility.

### Challenges Encountered

**Date Parsing and Formatting:**  
The most significant challenge was ensuring date strings parsed correctly across different recurrence calculations. SimpleDateFormat is notorious for subtle bugs if formats don't match exactly. I had to standardize on "MM/dd/yyyy" format throughout the application and add extensive error handling for ParseException cases. This taught me the importance of consistent data formats across a system.

**Testing Recurrence Edge Cases:**  
Testing monthly recurrence with dates like January 31st required understanding how Calendar.add() handles overflow. When adding a month to January 31st, different Calendar implementations might produce February 28/29 or March 2/3 depending on configuration. I had to research Calendar.add() behavior documentation and test specific edge cases to verify correct handling.

**Backward Compatibility:**  
Ensuring existing events continued working after the schema change required careful migration planning. Initially, I considered dropping and recreating the table (losing data), but researched ALTER TABLE approaches to preserve existing records. Choosing DEFAULT 'NONE' for the new column ensured zero disruption to existing functionality while enabling new features.

**Infinite Recurrence Chain:**  
The automatic generation creates an infinite chain of events (current event triggers next, which triggers next, etc.). This raised questions about when to stop generating occurrences. I documented this as a known design decision—recurring events generate indefinitely until user deletion. A future enhancement could add "end after N occurrences" or "end by date" options, but the simple approach was appropriate for this scope.

**Alarm Persistence Across Reboots:**  
Android clears all alarms on device restart. I added RECEIVE_BOOT_COMPLETED permission and would need to implement a BootReceiver to reschedule alarms after reboot. This taught me that features often have hidden dependencies and edge cases that only appear with real-world usage.

### Skills Demonstrated

This enhancement showcases proficiency in:
- Algorithm design and date manipulation
- Software architecture and component integration
- Database schema evolution and migration
- User interface design (clear, intuitive controls)
- Android development (AlarmManager, BroadcastReceiver, Spinner)
- Backward compatibility strategies
- Trade-off analysis and design decisions
- Error handling and robustness
- Code organization and modularity

---

## 6. Future Enhancements for Production Deployment

To make this implementation production-ready, several enhancements would be valuable:

**Custom Recurrence Patterns:**  
Implement advanced patterns like "every 2 weeks," "first Monday of month," "last day of month," or "weekdays only." This would require a more sophisticated recurrence rule system, potentially using RRULE format from iCalendar standard (RFC 5545).

**End Conditions:**  
Add options to stop recurrence after N occurrences or by a specific end date. This prevents infinite event generation and gives users more control. Would require additional database columns (occurrences_count, end_date) and logic to check conditions before generating next occurrence.

**Edit Future vs. Single Instance:**  
Allow users to edit just one occurrence or all future occurrences of a recurring event. This requires tracking which events belong to the same series (series_id column) and implementing logic to break series or update all related events.

**Exception Dates:**  
Support skipping specific occurrences (e.g., skip meeting on holiday). Would require an exceptions table linking to series_id with dates to skip, checked before generating each occurrence.

**Alarm Persistence:**  
Implement BootReceiver to reschedule all future alarms after device restart. Query database for upcoming events and reschedule their alarms. This ensures users don't miss notifications after reboots.

**Timezone Support:**  
Add timezone awareness for recurrence calculations. Current implementation uses device local time, which can cause issues with daylight saving time transitions or when users travel. Would require storing timezone with each event and using ZonedDateTime instead of Calendar.

**Performance Optimization:**  
For users with many recurring events, generating occurrences on-demand might be expensive. Consider pre-generating next N occurrences (e.g., next 90 days) during off-peak times or implement lazy generation as user scrolls calendar view.

---

## 7. Files Included in This Enhancement

```
enhancement-1/
├── Original/                              (Before Enhancement One - base app)
│   ├── DatabaseHelper.java                (Version 1, no recurrence)
│   ├── EventsGridActivity.java            (No recurrence UI)
│   ├── EventReminderReceiver.java         (Single-fire only)
│   ├── activity_events_grid.xml  
│
├── Enhanced/                              (After Enhancement One - v2)
│   ├── DatabaseHelper.java                (Version 2 with recurrence) ✨
│   ├── EventsGridActivity.java            (Spinner for recurrence) ✨
│   ├── EventReminderReceiver.java         (Auto-generates next) ✨
│   ├── activity_events_grid.xml
│
├── Milestone_3-2_Enhancement_One_Sunny.docx  (Official narrative)
├── README.md                              (Technical documentation)
└── index.md                               (This web-friendly narrative)
```

---

## Conclusion

Enhancement One successfully implements a comprehensive recurring events system that transforms the Event Tracker from a simple one-time event manager into a fully-featured calendar application. The implementation demonstrates algorithmic design through date manipulation algorithms, software engineering through modular architecture and backward compatibility, and user-centered design through intuitive UI controls.

Through this enhancement, I gained practical experience with date/time algorithms, database schema evolution, component integration across complex systems, and the critical skill of analyzing design trade-offs. The ability to evaluate competing approaches—automatic vs. manual generation, simplicity vs. extensibility, immediate implementation vs. future flexibility—is essential for professional software engineering.

This enhancement showcases my ability to design and implement substantial features that deliver real user value while maintaining code quality, backward compatibility, and professional software engineering standards.

---

**View the complete technical documentation:** [README.md](README.md)  
**View the official narrative:** [Milestone 3-2 Document](Milestone_3-2_Enhancement_One_Sunny.docx)  
**Return to main portfolio:** [CS-499 Capstone Portfolio](../index.md)
