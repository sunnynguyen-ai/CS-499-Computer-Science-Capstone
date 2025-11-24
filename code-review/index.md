# Code Review — CS-499 Computer Science Capstone

## Overview

This section contains my formal code review for the CS-499 Computer Science Capstone.

The purpose of this code review is to evaluate the structure, design, readability, maintainability, and security of the original Android application before any enhancements were made.

The review identifies weaknesses, outdated design decisions, inefficiencies, and security concerns. This analysis directly informed the three enhancements completed throughout the capstone.

---

## Code Review Video

Because GitHub cannot host large video files, the full recorded video code review is available at the link below:

**[Code Review Video (External Link)](PASTE_YOUR_VIDEO_LINK_HERE)**

*(Replace the text above with your Google Drive / YouTube / OneDrive video link.)*

---

## Purpose of the Code Review

The original application was developed earlier in the SNHU Computer Science program (CS-360). At that time, the project met functional requirements, but had several structural and security issues that needed analysis before enhancement.

The code review allowed me to:

- Identify areas of poor separation of concerns
- Find opportunities to apply modern software engineering principles
- Detect unoptimized algorithms and database queries
- Recognize weak security practices, such as plaintext passwords
- Map out exactly which files required enhancement
- Document improvement opportunities for the capstone project

This review serves as the foundation for all three enhancements.

---

## Summary of Findings

### Software Engineering & Design Issues

- Activities mixed UI logic, data logic, and validation
- Duplicate code patterns reduced maintainability
- Several methods were too large and violated single-responsibility best practices
- Naming conventions were inconsistent in some classes
- Minimal documentation and comments

These findings led to **Enhancement 1 (Software Engineering & Design)**.

### Algorithm and Performance Issues

- SQLite tables had **no indexes**, resulting in O(n) full table scans
- Cursor column indices were retrieved repeatedly inside loops
- Queries were not optimized for ordering or lookup
- No caching or reuse of expensive operations

These findings led to **Enhancement 2 (Algorithms & Data Structures)**.

### Security Issues

- User passwords stored in plaintext
- Input validation was minimal or missing
- Database operations assumed trusted input
- No defensive programming patterns
- Error messages exposed internal logic

These findings led to **Enhancement 3 (Databases & Security)**.

---

## Reflection

Completing this code review improved my ability to analyze existing codebases from a professional perspective. I learned how to systematically evaluate:

- Maintainability
- Testability
- Scalability
- Data integrity
- Secure coding practices
- Algorithmic efficiency
- Architectural design decisions

This review served as a roadmap that guided every enhancement in the capstone.

---

## Files Included in This Section

```
code-review/
    index.md                  # This narrative
    code-review-video-link    # External link to video file
    supporting-notes/         # (Optional)
```

---

## Conclusion

This code review established the foundation for all enhancements made in CS-499.

It documented the weaknesses of the original artifact and ensured each enhancement was purposeful, measurable, and tied directly to capstone outcomes.

It demonstrates my ability to analyze, critique, and plan improvements for complex software systems — a critical skill for professional computer scientists.
