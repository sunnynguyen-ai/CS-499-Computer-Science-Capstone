# Enhancement Two — Algorithms & Data Structures

## 📘 Overview
Enhancement Two focuses on improving the algorithmic efficiency and data-handling performance of the Android Event Tracker application. The primary artifact for this enhancement is the `DatabaseHelper.java` file, which manages all SQLite interactions including schema creation, data retrieval, and CRUD operations.

Before enhancement, the database logic was functional but inefficient. SQL queries performed full table scans, column index lookups were repeated inside loops, and no database indexes were defined. As the number of events grew, query performance degraded and the application became less scalable. This enhancement applies algorithmic principles, database indexing, and caching strategies to significantly improve performance.

---

## 🔍 **1. Description of the Original Artifact**
The original `DatabaseHelper.java` implementation had several inefficiencies:

- No database indexes, causing **O(n)** full table scans  
- Repeated calls to `cursor.getColumnIndex()` inside loops  
- Unoptimized query ordering  
- No use of data-structures that improve retrieval time  
- The database schema was not tuned for scalability  

Although the application functioned for small datasets, performance decreased as the event table grew, especially when sorting or filtering data.

---

## 🛠 **2. Summary of Enhancements**
For this milestone, I applied algorithmic and data structure improvements, including:

### **✔ Added SQLite Indexes**
Indexes were added for the most frequently queried columns:

- `date`
- `time`
- `recurrence_type`

Indexes convert searches from **O(n)** scans → **O(log n)** B-tree lookups, dramatically improving query performance.

### **✔ Cached Column Indexes**
Before the enhancement, the code repeatedly called:

```java
c.getColumnIndexOrThrow(COL_NAME)
