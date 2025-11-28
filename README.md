# CS-499 Computer Science Capstone Portfolio

**Sunny Nguyen**  
Southern New Hampshire University  
Bachelor of Science in Computer Science  
October 27, 2025 - December 21, 2025

---

## 🎓 Overview

This repository contains the complete capstone portfolio for CS-499, demonstrating mastery of computer science principles through a comprehensive code review and three substantial enhancements to a single software artifact. The project showcases professional-grade software engineering, algorithmic optimization, and distributed systems design.

**Selected Artifact:** Event Tracker Android Application  
**Original Course:** CS-360 Mobile Architecture & Programming  
**Final State:** Cloud-enabled event management system with recurring events, optimized database performance, and bidirectional synchronization

---

## 🚀 Project Evolution

### Base Application (v1)
- Basic event CRUD operations
- Local SQLite storage
- User authentication
- SMS notification system

### After Enhancement One (v2) - Recurring Events
- Automatic recurrence generation (daily, weekly, monthly)
- Calendar.add() date manipulation algorithm
- Database schema evolution with recurrence_type column
- Enhanced notification chain for recurring events

### After Enhancement Two (v3) - Database Optimization
- Three B-tree indexes for O(log n) query performance
- Column index caching (eliminated redundant operations)
- 16-43x performance improvement for large datasets
- Query optimization with strategic indexing

### After Enhancement Three (v4) - Cloud Synchronization
- REST API integration with JSON serialization
- Bidirectional data synchronization
- Hybrid local-cloud architecture
- Background threading with conflict resolution
- Network communication with error handling

---

## 📂 Repository Structure

```
CS-499-Computer-Science-Capstone/
│
├── README.md                           # This file - repository overview
├── index.md                            # Portfolio homepage (GitHub Pages)
│
├── code-review/
│   ├── index.md                        # Code review documentation
│   └── README.md                       # Code review overview
│
├── enhancement-1/                      # SOFTWARE ENGINEERING & DESIGN
│   ├── Original/                       # Starting point (v1 - base app)
│   ├── Enhanced/                       # After Enhancement One (v2)
│   ├── Milestone_3-2_Enhancement_One_Sunny.docx
│   ├── index.md                        # Web-friendly narrative (3,500 words)
│   └── README.md                       # Technical documentation (3,500 words)
│
├── enhancement-2/                      # ALGORITHMS & DATA STRUCTURES
│   ├── Original/                       # Starting point (v2 - with recurring events)
│   ├── Enhanced/                       # After Enhancement Two (v3)
│   ├── Milestone_4-2_Enhancement_Two_Sunny.docx
│   ├── index.md                        # Web-friendly narrative (2,500 words)
│   └── README.md                       # Technical documentation (3,000 words)
│
├── enhancement-3/                      # DATABASES
│   ├── Original/                       # Starting point (v3 - with indexing)
│   ├── Enhanced/                       # After Enhancement Three (v4)
│   ├── Milestone_5-2_Enhancement_Three_Sunny.docx
│   ├── index.md                        # Web-friendly narrative (3,500 words)
│   └── README.md                       # Technical documentation (3,500 words)
│
└── professional-self-assessment/
    ├── index.md                        # Professional self-assessment
    └── README.md                       # Assessment overview
```

**Total Documentation:** 23,000+ words of comprehensive technical and professional content

---

## 🌐 Live Portfolio Website

**GitHub Pages URL:**  
[https://sunnynguyen-ai.github.io/CS-499-Computer-Science-Capstone](https://sunnynguyen-ai.github.io/CS-499-Computer-Science-Capstone)

The portfolio website presents all capstone work in a professional, employer-friendly format with:
- Comprehensive project documentation
- Technical implementation details
- Code examples and architecture diagrams
- Performance metrics and analysis
- Professional self-assessment

---

## 🎯 Enhancement Summaries

### Enhancement One: Software Engineering & Design

**Focus:** Recurring Events Implementation  
**Database Evolution:** v1 → v2  
**Course Outcomes:** CO-03 (Algorithms), CO-04 (Industry Techniques)

**Key Achievements:**
- Implemented comprehensive recurring events system (daily, weekly, monthly patterns)
- Designed and implemented Calendar.add() algorithm for automatic next-occurrence generation
- Evolved database schema with backward-compatible migration strategy
- Created intuitive UI with Spinner component for recurrence selection
- Integrated automatic event chain generation into notification system

**Technical Highlights:**
- Handles complex date edge cases (leap years, month boundaries, year rollovers)
- Method overloading for API compatibility
- String constants for extensibility (easy to add BIWEEKLY, YEARLY patterns)
- ALTER TABLE with DEFAULT for zero-disruption migration

**Impact:** Eliminated manual event duplication, provided feature parity with commercial calendar apps

📄 [View Enhancement One Documentation](./enhancement-1/index.md)

---

### Enhancement Two: Algorithms & Data Structures

**Focus:** Database Indexing & Query Optimization  
**Database Evolution:** v2 → v3  
**Course Outcomes:** CO-03 (Algorithmic Principles)

**Key Achievements:**
- Implemented three strategic B-tree indexes (date, time, recurrence_type)
- Optimized cursor operations through column index caching
- Transformed query complexity from O(n) → O(log n)
- Achieved 16-43x performance improvement for large datasets

**Performance Results:**
| Dataset Size | Before (v2) | After (v3) | Improvement |
|--------------|-------------|------------|-------------|
| 100 events | 18ms | 4ms | 4.5x faster |
| 1,000 events | 195ms | 12ms | 16x faster |
| 5,000 events | 1,200ms | 28ms | 43x faster |

**Technical Highlights:**
- Applied B-tree data structures for self-balancing logarithmic search
- Eliminated O(n × m) redundant column lookups → O(m) single lookup
- Analyzed query patterns to identify high-value indexing targets
- Evaluated trade-offs: storage overhead vs. query performance

**Impact:** Maintains app responsiveness with 10,000+ events, demonstrates practical algorithm analysis

📄 [View Enhancement Two Documentation](./enhancement-2/index.md)

---

### Enhancement Three: Databases

**Focus:** Cloud Synchronization & Distributed Architecture  
**Database Evolution:** v3 → v4  
**Course Outcomes:** CO-04 (Industry Techniques), CO-03 (Algorithms)

**Key Achievements:**
- Transformed application from local-only to hybrid local-cloud architecture
- Implemented REST API layer with JSON serialization/deserialization
- Built synchronization orchestrator with background threading
- Added conflict resolution algorithm (remote wins strategy)
- Created duplicate detection using indexed lookups (O(log n))

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

**Technical Highlights:**
- Three-phase sync: upload → download → update metadata
- ExecutorService for background operations, Handler for UI updates
- Network error handling with proper resource cleanup
- Strategic indexing for sync queries (sync_status, remote_id)

**Impact:** Multi-device access, automatic backup, data persistence beyond device storage

📄 [View Enhancement Three Documentation](./enhancement-3/index.md)

---

## 🏆 Course Outcomes Demonstrated

### CO-01: Employ strategies for building collaborative environments
- Comprehensive code review with constructive analysis
- Well-documented code with comments for team readability
- Professional GitHub repository organization
- Technical narratives supporting stakeholder decisions

### CO-02: Design, develop, and deliver professional-quality communications
- Three detailed enhancement narratives (2,000-3,500 words each)
- Technical README files with architecture diagrams
- Professional self-assessment
- Code review video with clear explanations
- Comprehensive ePortfolio website

### CO-03: Design and evaluate computing solutions using algorithmic principles
- Date manipulation algorithm (Calendar.add())
- B-tree indexing for O(log n) performance
- Duplicate detection algorithm
- Conflict resolution strategy
- Time complexity analysis and trade-off evaluation

### CO-04: Demonstrate ability to use well-founded and innovative techniques
- Recurring events (industry-standard feature)
- Database optimization (B-tree best practices)
- REST API integration (modern architecture)
- Hybrid local-cloud design (mobile app pattern)
- Background threading (Android best practices)

### CO-05: Develop a security mindset
- Parameterized SQL queries (injection prevention)
- Input validation in user interfaces
- Secure network communication
- Proper authentication mechanisms
- Defensive programming throughout

---

## 🛠 Technologies & Tools

**Programming Languages:**
- Java (Android development)
- SQL (database queries and optimization)
- XML (Android layouts and configuration)

**Frameworks & Libraries:**
- Android SDK (API 21+)
- SQLite Database
- HttpURLConnection (network communication)
- org.json (JSON parsing)
- AlarmManager (scheduling)

**Development Tools:**
- Android Studio
- Git/GitHub (version control)
- GitHub Pages (portfolio hosting)
- SQLite Database Browser (debugging)

**Design Patterns:**
- Strategy Pattern (recurrence generation)
- Factory Pattern (event instance creation)
- Builder Pattern (Calendar configuration)

---

## 📊 Project Timeline

**Module 1** (October 27 - November 2, 2025)
- Artifact selection from CS-360 coursework
- Enhancement planning and design

**Module 2** (November 3-9, 2025)
- Code review video creation
- Comprehensive analysis of original artifact
- Identification of improvement opportunities

**Module 3** (November 10-16, 2025)
- Enhancement One: Recurring events implementation
- Database v1 → v2 migration
- **Result:** MAX SCORE on Milestone 3-2

**Module 4** (November 17-23, 2025)
- Enhancement Two: Database indexing optimization
- Database v2 → v3 migration
- **Result:** MAX SCORE on Milestone 4-2

**Module 5** (November 24-30, 2025)
- Enhancement Three: Cloud synchronization
- Database v3 → v4 migration
- Milestone 5-2 submitted

**Module 6** (December 1-7, 2025)
- Documentation enhancement and polish
- GitHub Pages setup and publication
- Portfolio organization and presentation

**Module 7** (December 8-14, 2025)
- Professional self-assessment completion
- Final ePortfolio polish and review

**Module 8** (December 15-21, 2025)
- Final capstone submission
- Portfolio presentation and completion

---

## 💡 Skills Demonstrated

### Technical Proficiency
- Mobile application development (Android)
- Database design and optimization
- RESTful API integration
- Algorithm analysis and implementation
- Network programming
- Concurrent programming (threading)
- Version control (Git/GitHub)

### Software Engineering Practices
- Code review and analysis
- Refactoring and optimization
- Design pattern application
- Documentation and technical writing
- Testing and debugging
- Migration strategies
- Backward compatibility

### Problem-Solving & Analysis
- Algorithmic complexity evaluation (Big O)
- Performance profiling and benchmarking
- Edge case identification
- Trade-off analysis and decision-making
- Security vulnerability assessment
- Architecture design

### Professional Development
- Technical communication
- Project planning and execution
- Self-directed learning
- Quality assurance
- Stakeholder presentation
- Portfolio development

---

## 📈 Key Metrics

**Code Quality:**
- 23,000+ words of documentation
- 8 Java source files enhanced
- 100% backward compatibility maintained
- Zero breaking changes across versions

**Performance Improvements:**
- 43x faster query performance (5,000 events)
- O(n) → O(log n) complexity reduction
- Eliminated redundant operations
- Optimized network communication

**Feature Additions:**
- Recurring events (3 patterns)
- Cloud synchronization
- REST API integration
- Database optimization
- Enhanced UI controls

---

## 🎯 Learning Outcomes

This capstone project enhanced my abilities in:

1. **Algorithmic Thinking:** Understanding time complexity, selecting appropriate data structures, and optimizing performance
2. **System Design:** Architecting distributed systems, managing state, and handling data synchronization
3. **Software Engineering:** Writing maintainable code, documenting thoroughly, and planning migrations
4. **Technical Communication:** Explaining complex systems clearly to technical and non-technical audiences
5. **Professional Practice:** Delivering production-quality work, meeting deadlines, and maintaining high standards

---

## 📧 Contact

**Sunny Nguyen**  
**Institution:** Southern New Hampshire University  
**Program:** Bachelor of Science in Computer Science  
**Graduation:** 2025

**GitHub:** [github.com/sunnynguyen-ai](https://github.com/sunnynguyen-ai)  
**Repository:** [CS-499-Computer-Science-Capstone](https://github.com/sunnynguyen-ai/CS-499-Computer-Science-Capstone)  
**Portfolio:** [GitHub Pages Site](https://sunnynguyen-ai.github.io/CS-499-Computer-Science-Capstone)

---

## 📄 License & Usage

This project is part of my SNHU Computer Science Capstone and is intended for:
- Academic demonstration
- Professional portfolio presentation
- Employer/recruiter review
- Educational reference

All code, documentation, and materials are original work completed for CS-499 unless otherwise cited.

---

## 🙏 Acknowledgments

**Southern New Hampshire University** - For providing comprehensive Computer Science education and capstone structure

**Course Instructors** - For guidance, feedback, and support throughout the program

**Android Development Community** - For excellent documentation and open-source tools

**GitHub Pages** - For providing free portfolio hosting

---

**Thank you for visiting my Computer Science Capstone portfolio!**

This repository represents the culmination of years of learning, growth, and dedication to mastering software development principles and professional practices. Each enhancement demonstrates not just technical skill, but the ability to analyze problems, design solutions, implement effectively, and communicate clearly—the hallmarks of a professional software engineer.

---

*Last Updated: November 2025*  
*CS-499 Computer Science Capstone*  
*Southern New Hampshire University*
