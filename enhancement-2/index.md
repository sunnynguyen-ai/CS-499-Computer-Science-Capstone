# Enhancement Two — Algorithms & Data Structures

## Overview

Enhancement Two focuses on improving the algorithmic efficiency and data-handling performance of the Android Event Tracker application. The primary artifact for this enhancement is the `DatabaseHelper.java` file, which manages all SQLite interactions including schema creation, data retrieval, and CRUD operations.

Before enhancement, the database logic was functional but inefficient. SQL queries performed full table scans, column index lookups were repeated inside loops, and no database indexes were defined. As the number of events grew, query performance degraded and the application became less scalable. This enhancement applies algorithmic principles, database indexing, and caching strategies to significantly improve performance.

---

## 1. Description of the Original Artifact

The original `DatabaseHelper.java` implementation had several inefficiencies:

- No database indexes, causing **O(n)** full table scans
- Repeated calls to `cursor.getColumnIndex()` inside loops
- Unoptimized query ordering
- No use of data-structures that improve retrieval time
- The database schema was not tuned for scalability

Although the application functioned for small datasets, performance decreased as the event table grew, especially when sorting or filtering data.

---

## 2. Summary of Enhancements

For this milestone, I applied algorithmic and data structure improvements, including:

### Added SQLite Indexes

Indexes were added for the most frequently queried columns:

- `date`
- `time`
- `recurrence_type`

Indexes convert searches from **O(n)** scans → **O(log n)** B-tree lookups, dramatically improving query performance.

### Cached Column Indexes

Before the enhancement, the code repeatedly called:

```java
c.getColumnIndexOrThrow(COL_NAME)
```

This lookup is expensive and happens every loop iteration.

I refactored the code to cache these values once, before looping:

```java
int idxName = c.getColumnIndexOrThrow(COL_NAME);
```

This reduces redundant work and improves efficiency.

### Improved Query Ordering

Queries were changed to:

```sql
ORDER BY date, time
```

This pairs naturally with index usage and improves result retrieval performance.

### Updated Database Version

Bumped `DATABASE_VERSION` to ensure proper schema updates across devices.

---

## 3. Course Outcomes Addressed

This enhancement directly supports the following Capstone outcome:

### Course Outcome 3 – Develop solutions informed by algorithmic principles and computational complexity

- Using B-tree indexes demonstrates algorithmic reasoning
- Converting O(n) scans into O(log n) lookups shows understanding of complexity
- Caching column indices eliminates unnecessary repeated operations
- Query optimization demonstrates practical application of algorithms in real systems

This enhancement also reinforces concepts from data structures (indexing trees), databases, and performance tuning.

---

## 4. Reflection on the Enhancement Process

This enhancement improved my understanding of how algorithmic concepts apply to real-world software systems. The most challenging part was determining which columns would benefit most from indexing and ensuring that schema changes remained backward compatible.

I also learned how much performance can improve by eliminating redundant work and using efficient data-access patterns. Inserting large batches of test events allowed me to verify measurable improvements in query time, confirming that algorithmic enhancements have a major impact even in smaller mobile applications.

Overall, this enhancement strengthened my skills in performance optimization, algorithm analysis, database indexing, and practical system scalability.

---

## Files Included in This Enhancement

```
original/
    (original DatabaseHelper.java file)

enhanced/
    (enhanced DatabaseHelper.java with indexing and caching)

index.md
    (this narrative)
```

---

This enhancement demonstrates my ability to apply algorithmic principles and optimize system performance, which is a critical skill for professional software engineers.
