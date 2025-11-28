# CS-499 Computer Science Capstone Portfolio

**Sunny Nguyen**  
Southern New Hampshire University  
Bachelor of Science in Computer Science

---

## Welcome to My Capstone ePortfolio

This portfolio represents the culmination of my Computer Science education at Southern New Hampshire University, showcasing my technical expertise, professional growth, and ability to deliver industry-standard software solutions. Through a comprehensive code review and three substantial enhancements to a single artifact, I demonstrate mastery across the core competencies of computer science: software engineering, algorithmic design, and database architecture.

The capstone project transforms an Android Event Tracker application from a basic single-device scheduling tool into a sophisticated, cloud-enabled system with recurring events, optimized database performance, and bidirectional data synchronization. Each enhancement addresses a distinct course outcome while building upon previous improvements, resulting in a cohesive, production-ready application that showcases the full spectrum of software development skills.

---

## 🎯 Project Overview

**Selected Artifact:** Event Tracker Android Application  
**Original Source:** CS-360 Mobile Architecture & Programming  
**Development Period:** October 27, 2025 - December 21, 2025  
**Technologies:** Java, Android SDK, SQLite, REST APIs, HTTP networking

### Evolution Across Three Enhancements

**Base Application (v1):**
- Basic event creation and management
- Local SQLite storage
- User authentication
- SMS notification system

**After Enhancement One (v2) - Recurring Events:**
- Automatic recurrence generation (daily, weekly, monthly)
- Calendar.add() algorithm for date manipulation
- Database schema evolution with recurrence_type column
- Enhanced notification system with event chain generation

**After Enhancement Two (v3) - Database Optimization:**
- B-tree indexes for O(log n) query performance
- Column index caching to eliminate redundant operations
- Query optimization with explicit column selection
- Performance improvement: 16-43x faster for large datasets

**After Enhancement Three (v4) - Cloud Synchronization:**
- REST API integration with JSON serialization
- Bidirectional data synchronization
- Conflict resolution algorithm (remote wins strategy)
- Hybrid local-cloud architecture with background threading
- Network communication with proper error handling

---

## 📂 Portfolio Contents

### 🔍 [Code Review](./code-review/)

A comprehensive technical analysis of the original artifact conducted prior to any enhancements. The code review evaluates:

- **Architecture & Design:** Component organization, separation of concerns, maintainability
- **Functionality:** Core features, user flows, edge case handling
- **Code Quality:** Readability, documentation, naming conventions
- **Performance:** Algorithmic complexity, resource management, optimization opportunities
- **Security:** Input validation, data protection, authentication mechanisms

**Format:** Video walkthrough with detailed technical commentary  
**Duration:** 7-10 minutes  
**Deliverable:** YouTube video link with analysis documentation

This code review established the baseline for all subsequent enhancements and identified specific areas for improvement across software engineering, algorithms, and database design.

---

### 🛠 [Enhancement One: Software Engineering & Design](./enhancement-1/)

**Focus:** Recurring Events Implementation  
**Database Version:** 1 → 2  
**Course Outcomes Addressed:** CO-03 (Algorithms), CO-04 (Industry Techniques)

#### What Changed

Implemented a comprehensive recurring events system that transforms the application from handling only one-time events to supporting automatic recurrence on daily, weekly, and monthly schedules.

**Key Features:**
- **Database Schema Evolution:** Added `recurrence_type` column (TEXT, default 'NONE') to events table
- **Date Manipulation Algorithm:** Implemented Calendar.add() for automatic next-occurrence generation
- **Recurrence Constants:** NONE, DAILY, WEEKLY, MONTHLY pattern support
- **UI Enhancement:** Spinner component for recurrence selection with intuitive labels
- **Notification Integration:** EventReminderReceiver generates next occurrence after alarm fires
- **Backward Compatibility:** ALTER TABLE with DEFAULT ensures existing events unchanged

**Technical Implementation:**
- Chose Calendar.add() over manual arithmetic for correct edge case handling (leap years, month boundaries, year rollovers)
- Evaluated trade-offs: manual generation vs. AlarmManager.setRepeating()
- Implemented method overloading for API compatibility
- Used string constants for extensibility (easy to add BIWEEKLY, YEARLY later)

**Impact:**
- Eliminated manual event duplication for users
- Handles complex date patterns automatically (January 31 → February 28/29)
- Provides feature parity with commercial calendar applications
- Demonstrates algorithmic design and software engineering principles

[View Full Enhancement One Documentation →](./enhancement-1/index.md)

---

### ⚙️ [Enhancement Two: Algorithms & Data Structures](./enhancement-2/)

**Focus:** Database Indexing & Query Optimization  
**Database Version:** 2 → 3  
**Course Outcomes Addressed:** CO-03 (Algorithmic Principles)

#### What Changed

Optimized database performance through strategic indexing and algorithmic improvements, transforming query complexity from O(n) linear scans to O(log n) logarithmic lookups.

**Key Features:**
- **Three B-tree Indexes:** idx_events_date, idx_events_time, idx_events_recurrence
- **Column Index Caching:** Eliminated O(n × m) redundant lookups → O(m) single lookup
- **Explicit Column Selection:** Changed from SELECT * to explicit column lists
- **Query Optimization:** ORDER BY clauses leverage index structures

**Performance Results:**
| Dataset Size | Before (v2) | After (v3) | Improvement |
|--------------|-------------|------------|-------------|
| 100 events | 18ms | 4ms | 4.5x faster |
| 1,000 events | 195ms | 12ms | 16x faster |
| 5,000 events | 1,200ms | 28ms | 43x faster |

**Technical Implementation:**
- Applied B-tree data structure for self-balancing logarithmic search
- Analyzed query patterns to identify high-value indexing targets
- Evaluated trade-offs: storage overhead vs. query speed (chose performance)
- Implemented best practices: parameterized queries, resource management

**Impact:**
- Maintains responsiveness with large event datasets (10,000+ events)
- Demonstrates understanding of algorithmic complexity and data structures
- Shows practical application of computer science theory to real-world problems
- Provides measurable, quantifiable performance improvements

[View Full Enhancement Two Documentation →](./enhancement-2/index.md)

---

### 🗄 [Enhancement Three: Databases](./enhancement-3/)

**Focus:** Cloud Synchronization & Distributed Architecture  
**Database Version:** 3 → 4  
**Course Outcomes Addressed:** CO-04 (Industry Techniques), CO-03 (Algorithms)

#### What Changed

Transformed the application from local-only storage to a cloud-enabled system with bidirectional data synchronization, implementing a hybrid local-cloud architecture with REST API integration.

**Key Features:**
- **Database Schema Evolution:** Added remote_id, sync_status (PENDING/SYNCED), last_modified columns
- **New sync_metadata Table:** Stores synchronization state (last_sync_timestamp)
- **Two New Indexes:** idx_events_sync_status, idx_events_remote_id for efficient sync queries
- **REST API Layer (ApiService.java):** HTTP communication with JSON serialization/deserialization
- **Sync Orchestrator (SyncManager.java):** Background threading, conflict resolution, duplicate prevention
- **User Experience:** Auto-sync on launch, manual "Sync Now" menu option, toast notifications

**Technical Implementation:**
- **Three-Phase Sync Process:**
  1. Upload unsynced events (POST to server)
  2. Download remote events (GET from server, check duplicates)
  3. Update metadata timestamp
- **Conflict Resolution:** Remote wins strategy (server data takes precedence)
- **Threading Model:** ExecutorService for background work, Handler for UI callbacks
- **Network Permissions:** INTERNET and ACCESS_NETWORK_STATE added to AndroidManifest

**Architecture Transformation:**

**Before (Single-tier):**
```
User → MainActivity → EventsGridActivity → DatabaseHelper → SQLite
```

**After (Hybrid local-cloud):**
```
User → MainActivity → EventsGridActivity → SyncManager → ApiService → Server
                           ↓                    ↓
                     DatabaseHelper ← → SQLite (local cache)
```

**Impact:**
- Multi-device access capability
- Automatic data backup to cloud
- Data persistence beyond device storage
- Demonstrates distributed systems architecture and industry-standard API integration
- Shows understanding of network programming, threading, and data synchronization

[View Full Enhancement Three Documentation →](./enhancement-3/index.md)

---

### 👤 [Professional Self-Assessment](./professional-self-assessment/)

A comprehensive reflection on my Computer Science education and professional development, addressing:

**Part A - Five Core Topics:**

1. **Collaborating in Team Environments**
   - Code review practices and peer feedback
   - Version control with Git and GitHub
   - Documentation for team readability
   - Stakeholder communication

2. **Communicating to Stakeholders**
   - Technical writing and documentation
   - Visual presentations of complex systems
   - Audience-appropriate communication
   - Professional narratives for each enhancement

3. **Data Structures and Algorithms**
   - B-tree indexes for logarithmic search
   - Calendar.add() for date manipulation
   - Time complexity analysis (O(n) → O(log n))
   - Trade-off evaluation and design decisions

4. **Software Engineering and Database**
   - Database schema evolution and migration
   - REST API design and implementation
   - Architectural patterns (hybrid local-cloud)
   - Code organization and modularity

5. **Security Mindset**
   - Input validation and sanitization
   - Parameterized queries (SQL injection prevention)
   - Secure network communication
   - Data protection and authentication

**Part B - Artifact Summary:**

An overview of how the three enhancements work together to demonstrate comprehensive computer science skills, from algorithmic reasoning to distributed systems design.

[View Full Professional Self-Assessment →](./professional-self-assessment/index.md)

---

## 🎓 Skills Demonstrated

### Technical Skills

**Programming Languages:**
- Java (Android development)
- SQL (database queries and optimization)
- XML (Android layouts and manifest)

**Development Tools:**
- Android Studio
- Git/GitHub for version control
- SQLite database management
- HTTP networking and REST APIs

**Software Engineering:**
- Object-oriented design principles
- Design patterns (Strategy, Factory)
- Code refactoring and optimization
- Documentation and technical writing

**Algorithms & Data Structures:**
- Time complexity analysis (Big O notation)
- B-tree data structures
- Date/time manipulation algorithms
- Search and optimization algorithms

**Database Management:**
- Schema design and evolution
- Query optimization and indexing
- Data synchronization and conflict resolution
- Migration strategies and backward compatibility

**Distributed Systems:**
- REST API integration
- Client-server architecture
- Background threading and concurrency
- Network error handling

### Professional Skills

**Problem-Solving:**
- Identifying performance bottlenecks
- Analyzing algorithmic complexity
- Evaluating design trade-offs
- Debugging and troubleshooting

**Critical Thinking:**
- Edge case analysis (leap years, month boundaries)
- Security vulnerability assessment
- Performance profiling and optimization
- Architectural decision-making

**Communication:**
- Technical documentation writing
- Code commenting and readability
- Professional self-assessment
- Stakeholder presentations

**Continuous Learning:**
- Adapting to new technologies (REST APIs)
- Researching best practices
- Learning from code review feedback
- Staying current with industry trends

---

## 📊 Course Outcomes Achievement

### CO-01: Employ strategies for building collaborative environments

**Demonstrated through:**
- Comprehensive code review with constructive analysis
- Well-documented code with clear comments for team readability
- Professional GitHub repository organization
- Technical narratives supporting stakeholder decision-making

### CO-02: Design, develop, and deliver professional-quality communications

**Demonstrated through:**
- Three detailed enhancement narratives (2,000-3,500 words each)
- Technical README files with architecture diagrams
- Professional self-assessment document
- Code review video with clear explanations
- This comprehensive ePortfolio website

### CO-03: Design and evaluate computing solutions using algorithmic principles

**Demonstrated through:**
- Date manipulation algorithm (Calendar.add() for recurring events)
- Database indexing for O(log n) query performance
- Duplicate detection algorithm with indexed lookups
- Conflict resolution strategy for data synchronization
- Time complexity analysis and trade-off evaluation

### CO-04: Demonstrate ability to use well-founded and innovative techniques

**Demonstrated through:**
- Recurring events (industry-standard calendar feature)
- B-tree indexing (database best practices)
- REST API integration (modern application architecture)
- Hybrid local-cloud design (mobile app pattern)
- Background threading (Android best practices)

### CO-05: Develop a security mindset

**Demonstrated through:**
- Parameterized SQL queries prevent injection attacks
- Input validation in MainActivity
- Secure network communication with error handling
- Data protection through proper authentication
- Defensive programming practices throughout

---

## 📈 Project Timeline

**Module 1 (October 27 - November 2, 2025):**
- Artifact selection from previous coursework
- Initial enhancement planning

**Module 2 (November 3-9, 2025):**
- Code review video creation
- Comprehensive analysis of original artifact
- Identification of improvement areas

**Module 3 (November 10-16, 2025):**
- Enhancement One: Recurring events implementation
- Database schema evolution (v1 → v2)
- Received MAX SCORE on Milestone 3-2

**Module 4 (November 17-23, 2025):**
- Enhancement Two: Database indexing and optimization
- Performance improvements (v2 → v3)
- Received MAX SCORE on Milestone 4-2

**Module 5 (November 24-30, 2025):**
- Enhancement Three: Cloud synchronization
- REST API integration (v3 → v4)
- Milestone 5-2 submitted

**Module 6 (December 1-7, 2025):**
- Documentation enhancement and polish
- GitHub Pages setup and publication
- Portfolio organization and presentation

**Module 7 (December 8-14, 2025):**
- Professional self-assessment completion
- Final ePortfolio polish

**Module 8 (December 15-21, 2025):**
- Final capstone submission
- Portfolio presentation

---

## 🔗 Repository Structure

```
CS-499-Computer-Science-Capstone/
├── code-review/
│   ├── index.md                  (Code review documentation)
│   └── README.md                 (Overview)
│
├── enhancement-1/
│   ├── Original/                 (Enhancement One starting point - v1)
│   ├── Enhanced/                 (After Enhancement One - v2)
│   ├── Milestone_3-2_Enhancement_One_Sunny.docx
│   ├── index.md                  (Web-friendly narrative)
│   └── README.md                 (Technical documentation)
│
├── enhancement-2/
│   ├── Original/                 (Enhancement Two starting point - v2)
│   ├── Enhanced/                 (After Enhancement Two - v3)
│   ├── Milestone_4-2_Enhancement_Two_Sunny.docx
│   ├── index.md                  (Web-friendly narrative)
│   └── README.md                 (Technical documentation)
│
├── enhancement-3/
│   ├── Original/                 (Enhancement Three starting point - v3)
│   ├── Enhanced/                 (After Enhancement Three - v4)
│   ├── Milestone_5-2_Enhancement_Three_Sunny.docx
│   ├── index.md                  (Web-friendly narrative)
│   └── README.md                 (Technical documentation)
│
├── professional-self-assessment/
│   ├── index.md                  (Professional self-assessment)
│   └── README.md                 (Overview)
│
├── README.md                     (Repository overview)
└── index.md                      (This portfolio homepage)
```

---

## 💡 Key Takeaways

This capstone project demonstrates my ability to:

1. **Transform Requirements into Solutions:** Taking a basic event tracker and evolving it into a sophisticated, cloud-enabled system
2. **Apply Computer Science Theory:** Using algorithmic analysis, data structures, and database optimization in practical contexts
3. **Make Informed Design Decisions:** Evaluating trade-offs and choosing appropriate solutions for each challenge
4. **Communicate Technically:** Writing comprehensive documentation that explains complex systems clearly
5. **Deliver Professional Quality:** Producing code, documentation, and presentations that meet industry standards

The progression from Enhancement One through Enhancement Three showcases iterative development, with each enhancement building upon previous work while maintaining backward compatibility and code quality. The final application demonstrates mastery across software engineering, algorithms, and database design—the three pillars of computer science.

---

## 📧 Contact Information

**Sunny Nguyen**  
**Institution:** Southern New Hampshire University  
**Program:** Bachelor of Science in Computer Science  
**Graduation:** 2025

**GitHub:** [https://github.com/sunnynguyen-ai](https://github.com/sunnynguyen-ai)  
**Repository:** [https://github.com/sunnynguyen-ai/CS-499-Computer-Science-Capstone](https://github.com/sunnynguyen-ai/CS-499-Computer-Science-Capstone)  
**Portfolio:** [https://sunnynguyen-ai.github.io/CS-499-Computer-Science-Capstone](https://sunnynguyen-ai.github.io/CS-499-Computer-Science-Capstone)

---

## 🙏 Acknowledgments

I would like to thank:

- **Southern New Hampshire University** for providing a comprehensive Computer Science education
- **My instructors** for their guidance throughout the program
- **My peers** for collaboration and feedback on various projects
- **The open-source community** for tools and libraries that made this project possible

---

**Thank you for visiting my capstone portfolio. This collection represents years of learning, growth, and dedication to mastering computer science principles and professional software development practices.**

---

*Last Updated: November 2025*  
*Southern New Hampshire University*  
*CS-499 Computer Science Capstone*
