# Code Review: Event Tracker Android Application

**Sunny Nguyen**  
Southern New Hampshire University  
CS-499 Computer Science Capstone  
**Conducted:** November 3-9, 2025 (Module 2)

---

## Overview

This comprehensive code review analyzes the Event Tracker Android application developed for CS-360 Mobile Architecture & Programming. The review evaluates the artifact's structure, design, readability, maintainability, performance, and security prior to any capstone enhancements. Through systematic analysis, this review identifies specific weaknesses, outdated design decisions, algorithmic inefficiencies, and security vulnerabilities that directly informed the three substantial enhancements completed throughout the capstone project.

The code review serves as the foundation for all enhancement work, establishing a clear baseline and documenting improvement opportunities that align with CS-499 course outcomes. This professional analysis demonstrates my ability to critically evaluate existing codebases, identify areas for improvement, and plan systematic enhancements—essential skills for software engineering practice.

---

## Code Review Video

**Video Recording:** [Access via Brightspace Submission](https://learn.snhu.edu/d2l/le/dropbox/2074363/3919904/DownloadSubmissionFile?fid=225970781&sid=58884450)

**Duration:** 7-10 minutes  
**Date Recorded:** November 2025  
**Submitted:** Module 2 (Milestone 2-2)

> **Note for Portfolio Visitors:** This video is hosted on Brightspace (SNHU's learning management system) and requires institutional login access. The video was submitted for Module 2 coursework evaluation. A comprehensive written summary of findings is provided below.

> **For Employers/Recruiters:** If you would like to view the code review video, please contact me and I can provide alternative access or a detailed written analysis.

---

## Purpose of the Code Review

The Event Tracker Android application was originally developed for CS-360 Mobile Architecture & Programming. While the application met functional requirements and demonstrated competency in mobile development fundamentals, it contained several structural, performance, and security issues that presented opportunities for significant improvement during the capstone.

### Objectives of This Review

**Systematic Analysis:**
- Evaluate overall architecture and design patterns
- Assess code organization and separation of concerns
- Identify algorithmic inefficiencies and performance bottlenecks
- Examine database design and query optimization opportunities
- Analyze security practices and vulnerability exposure
- Document maintainability and scalability concerns

**Enhancement Planning:**
- Map specific weaknesses to capstone enhancement opportunities
- Prioritize improvements based on impact and course outcomes
- Establish measurable goals for each enhancement category
- Create a roadmap for systematic improvement across three enhancements

**Professional Development:**
- Practice industry-standard code review techniques
- Develop critical analysis skills for legacy codebases
- Learn to communicate technical issues clearly and constructively
- Demonstrate ability to plan and execute improvements systematically

This code review established the foundation for all enhancement work in CS-499, ensuring each improvement was purposeful, measurable, and directly tied to course outcomes.

---

## Original Artifact State (Before Enhancements)

### Application Overview

**Platform:** Android (API 21+)  
**Language:** Java  
**Database:** SQLite (local storage only)  
**Primary Features:**
- User authentication (username/password)
- Event creation, reading, updating, deletion (CRUD)
- SMS notification system for event reminders
- Date and time selection for events
- Basic event listing in grid view

**Database Version:** 1 (Base schema)

**Core Components:**
- **MainActivity:** Login screen and authentication
- **EventsGridActivity:** Main event management interface
- **DatabaseHelper:** SQLite database operations
- **EventsAdapter:** RecyclerView adapter for event display
- **EventReminderReceiver:** BroadcastReceiver for notifications
- **SmsPermissionActivity:** SMS permission management

---

## Summary of Findings

### Category 1: Software Engineering & Design Issues

**Identified Problems:**

**1. Limited Feature Set**
- Application only supported one-time events
- No recurring event functionality (daily, weekly, monthly patterns)
- Users had to manually create duplicate entries for regular events
- Missing fundamental calendar application feature
- Reduced practical utility for real-world scheduling needs

**2. Poor Code Organization**
- Activities mixed UI logic, data access, and business logic
- Violation of separation of concerns principle
- Difficult to test individual components
- Hard to modify one aspect without affecting others

**3. Minimal Documentation**
- Few comments explaining complex logic
- No JavaDoc documentation for public methods
- Unclear variable and method names in some areas
- Difficult for other developers to understand intent

**4. Duplicate Code Patterns**
- Similar validation logic repeated across activities
- Redundant database query patterns
- No reusable helper methods for common operations
- Increased maintenance burden

**5. Large Methods**
- Some methods exceeded 50 lines
- Multiple responsibilities in single methods
- Violated single-responsibility principle
- Reduced readability and testability

**Impact:** These issues made the codebase harder to maintain, extend, and collaborate on. They represented opportunities to apply modern software engineering principles and design patterns.

**Led to Enhancement One:** Software Engineering & Design - Recurring Events Implementation
- Implemented comprehensive recurring events system
- Added Calendar.add() date manipulation algorithm
- Evolved database schema with recurrence_type column
- Created intuitive UI for recurrence selection
- Demonstrated software architecture and algorithmic design

---

### Category 2: Algorithm and Performance Issues

**Identified Problems:**

**1. No Database Indexes**
- Events table had no indexes beyond primary key
- All queries performed O(n) full table scans
- Performance degraded linearly as event count increased
- No optimization for common query patterns (date lookups, sorting)

**2. Inefficient Cursor Operations**
- Column indexes retrieved repeatedly inside loops
- `cursor.getColumnIndexOrThrow()` called on every iteration
- For 100 events with 6 columns: 600 unnecessary method calls per query
- Wasted CPU cycles and memory allocations

**3. Unoptimized Queries**
- Used `SELECT *` instead of explicit column lists
- No consideration of query execution plans
- Ordering performed in memory rather than leveraging indexes
- Missed opportunities for database-level optimization

**4. No Caching Strategy**
- Expensive operations repeated unnecessarily
- No reuse of computed values
- Inefficient data structure usage
- No algorithmic complexity consideration

**Performance Impact:**
- Acceptable with 10-50 events (small datasets)
- Noticeable lag with 100+ events
- Unacceptable with 1,000+ events
- Would not scale for power users with years of event history

**Led to Enhancement Two:** Algorithms & Data Structures - Database Indexing
- Implemented three strategic B-tree indexes
- Cached column indexes to eliminate redundancy
- Optimized queries for explicit column selection
- Achieved 16-43x performance improvement
- Demonstrated algorithmic analysis and optimization

---

### Category 3: Database Architecture Issues

**Identified Problems:**

**1. Local-Only Storage**
- All data stored exclusively in device SQLite database
- Single point of failure (device loss = data loss)
- No backup or recovery mechanism
- No multi-device synchronization
- Data tied to single device permanently

**2. No Cloud Integration**
- Missing modern application architecture
- Users expect data to follow them across devices
- No collaboration features possible
- Limited to single-device use case
- Not competitive with commercial applications

**3. Missing Distributed Systems Features**
- No data synchronization mechanism
- No conflict resolution strategy
- No network communication layer
- No API integration
- Limited scalability

**4. Insufficient Schema for Sync**
- No way to track remote identifiers
- No synchronization status tracking
- No last-modified timestamps
- No metadata for sync state management
- Would require significant rework for cloud features

**Impact:** The application could not compete with modern calendar apps that offer cloud backup and multi-device access. Data was vulnerable to loss and users were locked to a single device.

**Led to Enhancement Three:** Databases - Cloud Synchronization
- Implemented REST API integration layer
- Built synchronization orchestrator with threading
- Added hybrid local-cloud architecture
- Created conflict resolution algorithm
- Demonstrated distributed systems design and network programming

---

## Detailed Analysis by Component

### DatabaseHelper.java (Critical Component)

**Original State (v1):**
```java
// Simple schema - no optimization
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    date TEXT,
    time TEXT,
    description TEXT
);

// No indexes
// No recurrence support
// No sync columns
```

**Issues Identified:**
- No recurrence_type column (Enhancement One adds this)
- No indexes on date, time columns (Enhancement Two adds these)
- No remote_id, sync_status, last_modified (Enhancement Three adds these)
- Column lookups repeated in loops (Enhancement Two fixes this)
- Full table scans for all queries (Enhancement Two fixes this)

**After All Enhancements (v4):**
```java
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    date TEXT,
    time TEXT,
    description TEXT,
    recurrence_type TEXT DEFAULT 'NONE',     -- Enhancement One
    remote_id TEXT,                          -- Enhancement Three
    sync_status TEXT DEFAULT 'PENDING',      -- Enhancement Three
    last_modified INTEGER DEFAULT 0          -- Enhancement Three
);

-- Enhancement Two adds:
CREATE INDEX idx_events_date ON events (date);
CREATE INDEX idx_events_time ON events (time);
CREATE INDEX idx_events_recurrence ON events (recurrence_type);

-- Enhancement Three adds:
CREATE INDEX idx_events_sync_status ON events (sync_status);
CREATE INDEX idx_events_remote_id ON events (remote_id);

-- Enhancement Three adds metadata table:
CREATE TABLE sync_metadata (
    meta_key TEXT PRIMARY KEY,
    meta_value TEXT
);
```

This shows the complete evolution from v1 → v4 across all three enhancements.

### EventsGridActivity.java (UI Component)

**Issues Identified:**
- No UI for recurrence selection (Enhancement One adds Spinner)
- No sync functionality (Enhancement Three adds SyncManager integration)
- No performance optimization for large lists (Enhancement Two helps via faster queries)
- Cursor operations inefficient (Enhancement Two caches indexes)

### EventReminderReceiver.java (Notification Component)

**Issues Identified:**
- Single-fire notifications only (Enhancement One adds next-occurrence generation)
- No recurrence logic (Enhancement One implements Calendar.add() algorithm)
- Missing recurrence parameter passing (Enhancement One adds EXTRA_RECURRENCE)

### MainActivity.java (Authentication)

**Issues Identified:**
- Minimal input validation (Enhancement Three improves this)
- Basic authentication (functional but could be more robust)

---

## Code Review Methodology

### Analysis Techniques Used

**1. Static Code Analysis**
- Line-by-line review of all Java source files
- Examination of XML layout and configuration files
- Database schema analysis
- Architecture and design pattern evaluation

**2. Functional Testing**
- Manual testing of all features
- Edge case identification
- Performance testing with varying dataset sizes
- User experience evaluation

**3. Algorithmic Analysis**
- Time complexity evaluation (Big O notation)
- Space complexity consideration
- Query performance profiling
- Identification of optimization opportunities

**4. Security Assessment**
- Input validation review
- SQL injection vulnerability check
- Authentication mechanism analysis
- Data protection evaluation

**5. Best Practices Comparison**
- Comparison to Android development guidelines
- Evaluation against industry standards
- Assessment of code maintainability
- Documentation completeness review

### Tools and Resources

- **Android Studio:** IDE with built-in code analysis
- **SQLite Database Browser:** Database schema inspection
- **Android Profiler:** Performance measurement
- **Git/GitHub:** Version control and history analysis
- **Course Materials:** CS-499 enhancement guidelines and rubrics

---

## Enhancement Roadmap Established

The code review directly informed the three-enhancement strategy:

### Enhancement One: Software Engineering & Design
**Goal:** Add recurring events functionality  
**Why:** Missing fundamental calendar feature, limits usability  
**Approach:** Database schema evolution, date manipulation algorithm, UI enhancement  
**Course Outcomes:** CO-03 (Algorithms), CO-04 (Industry Techniques)

### Enhancement Two: Algorithms & Data Structures
**Goal:** Optimize database performance through indexing  
**Why:** O(n) scans unacceptable for large datasets  
**Approach:** B-tree indexes, column caching, query optimization  
**Course Outcomes:** CO-03 (Algorithmic Principles)

### Enhancement Three: Databases
**Goal:** Implement cloud synchronization  
**Why:** Single-device limitation, no backup, lacks modern architecture  
**Approach:** REST API integration, sync orchestrator, hybrid architecture  
**Course Outcomes:** CO-04 (Industry Techniques), CO-03 (Algorithms)

Each enhancement builds upon previous work, demonstrating iterative development and systems thinking.

---

## Skills Demonstrated Through Code Review

### Technical Analysis Skills
- Ability to evaluate code quality objectively
- Understanding of performance characteristics
- Recognition of security vulnerabilities
- Identification of architectural weaknesses
- Knowledge of best practices and standards

### Critical Thinking
- Systematic problem identification
- Prioritization of improvements
- Cost-benefit analysis of potential changes
- Understanding of trade-offs in design decisions
- Planning of implementation strategies

### Professional Communication
- Clear articulation of technical issues
- Constructive criticism without defensiveness
- Documentation of findings for future reference
- Ability to explain complex problems simply
- Professional video presentation skills

### Project Planning
- Translation of weaknesses into actionable enhancements
- Alignment of improvements with course outcomes
- Realistic scoping of enhancement work
- Consideration of dependencies between changes
- Timeline and milestone planning

---

## Reflection on the Code Review Process

### Learning Outcomes

Completing this code review significantly enhanced my professional development. The process required me to approach my own previous work with objectivity and critical analysis—a challenging but essential skill for software engineers. Rather than defending original design decisions, I had to honestly evaluate what could be improved and why.

The most valuable aspect was learning to systematically analyze a codebase across multiple dimensions: functionality, performance, maintainability, security, and scalability. This holistic approach mirrors real-world code reviews where engineers must balance competing concerns and prioritize improvements based on impact and effort.

Understanding the "why" behind each identified issue was crucial. It's not enough to say "this is slow"—I had to understand the algorithmic complexity (O(n) scans), identify the root cause (lack of indexes), and propose specific solutions (B-tree indexes on date and time columns). This depth of analysis demonstrates true comprehension rather than superficial observation.

### Challenges Encountered

**Objectivity:** Reviewing my own code required setting aside ego and acknowledging flaws in previous work. Initially defensive, I learned to view criticism as opportunity rather than judgment.

**Prioritization:** With many potential improvements identified, determining which to pursue for the capstone required careful consideration of course outcomes, time constraints, and learning goals.

**Communication:** Articulating technical issues clearly in the video format was challenging. I had to balance depth of analysis with conciseness and clarity, ensuring viewers could follow my reasoning.

**Completeness:** Ensuring the review covered all relevant aspects without becoming overwhelming required careful planning and organization. I had to decide what level of detail was appropriate for each issue.

### Professional Growth

This code review prepared me for real-world software engineering by:
- **Practicing industry-standard review techniques** used in professional development teams
- **Developing critical analysis skills** applicable to any codebase evaluation
- **Learning to communicate technical issues** constructively and professionally
- **Understanding the importance of documentation** for tracking decisions and rationale
- **Experiencing iterative improvement** through planned, systematic enhancements

The ability to analyze existing code, identify improvements, and plan systematic enhancements is fundamental to professional software engineering. Most development involves maintaining and improving existing systems rather than building from scratch. This code review demonstrates I can effectively work with legacy code—a critical real-world skill.

---

## Files Included in This Section

```
code-review/
├── index.md              # This comprehensive code review narrative
└── README.md             # Code review overview and video access
```

**Original Artifact Files (Reviewed):**
- MainActivity.java
- EventsGridActivity.java
- DatabaseHelper.java
- EventsAdapter.java
- EventReminderReceiver.java
- SmsPermissionActivity.java
- AndroidManifest.xml
- activity_events_grid.xml
- (Additional XML layout and resource files)

---

## Conclusion

This comprehensive code review established the foundation for all enhancement work in CS-499 Computer Science Capstone. By systematically analyzing the Event Tracker Android application across multiple dimensions—software engineering, algorithms, and databases—I identified specific, measurable opportunities for improvement aligned with course outcomes.

The review demonstrates my ability to:
- **Critically evaluate** existing codebases from professional perspectives
- **Identify weaknesses** across multiple technical domains simultaneously
- **Plan systematic improvements** with clear goals and measurable outcomes
- **Communicate technical issues** clearly and constructively
- **Think holistically** about software quality beyond just functionality

Each finding in this review directly informed one of the three substantial enhancements:
1. **Recurring events implementation** addresses software design and algorithmic limitations
2. **Database indexing** resolves performance and algorithmic complexity issues
3. **Cloud synchronization** overcomes architectural and scalability constraints

The progression from code review → planned enhancements → implemented improvements demonstrates professional software engineering practice: analyze, plan, execute, and validate. This systematic approach to software improvement is essential for success in the computing industry.

---

**Navigation:**
- **Return to Portfolio Homepage:** [CS-499 Capstone Portfolio](../index.md)
- **View Enhancement One:** [Software Engineering & Design](../enhancement-1/index.md)
- **View Enhancement Two:** [Algorithms & Data Structures](../enhancement-2/index.md)
- **View Enhancement Three:** [Databases](../enhancement-3/index.md)
- **View Professional Self-Assessment:** [Professional Self-Assessment](../professional-self-assessment/index.md)

---

*Last Updated: November 2025*  
*CS-499 Computer Science Capstone*  
*Southern New Hampshire University*
