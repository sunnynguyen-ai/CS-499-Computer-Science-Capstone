# Enhancement Three — Databases & Security

## Overview

Enhancement Three focuses on improving the security and data-handling practices of the Android Event Tracker application. The artifact selected for this enhancement is the portion of the project that handles user authentication, input validation, and secure database interactions.

Before enhancement, the system stored user information and event data using functional—but insecure—methods. Input validation was minimal, user authentication was not hardened against common vulnerabilities, and some database operations accepted raw input without defensive safeguards. This enhancement applies secure coding, defensive programming, and best practices in data handling to make the application safer and more resilient.

---

## 1. Description of the Original Artifact

The original implementation contained several security risks:

- User passwords were stored in plaintext
- Input fields lacked strong validation
- No protection against malformed or malicious input
- SQL queries did not sanitize inputs beyond basic Android APIs
- Database operations assumed trusted data
- Error handling was minimal and not security-aware

Although the application worked, it did not meet industry expectations for secure mobile application development.

---

## 2. Summary of Enhancements

This enhancement strengthens the system's security posture by improving database safety, input validation, and user authentication.

### Improved Input Validation

All event and user fields now undergo strict validation:

- Ensures proper date and time format
- Prevents blank or malformed input
- Rejects unsafe characters
- Protects against unexpected application states

This eliminates invalid data from entering the database.

### Hardened User Authentication

Security improvements include:

- More structured login validation
- Better handling of incorrect credentials
- Cleaner logic that prevents unintended authentication bypass
- Reduced exposure to sensitive data

Future-ready hooks for hashed passwords were added so secure hashing can be implemented without redesign.

### Secure Handling of Database Operations

Database handling was strengthened by:

- Avoiding string concatenation
- Using parameterized queries everywhere
- Ensuring consistent closing of database resources
- Adding safer defaults for missing/empty data
- Implementing better error handling patterns

### Improved Data Integrity

Validation and secure defaults ensure that:

- No incomplete event records are stored
- Recurrence type defaults safely rather than allowing null
- Data retrieval gracefully handles missing fields

---

## 3. Course Outcomes Addressed

This enhancement directly supports the following Capstone outcome:

### Course Outcome 4 – Design, develop, and deliver secure computing solutions

Security principles implemented:

- Defensive programming
- Data validation
- Error handling
- Safe input processing
- Secure interaction with databases
- Authentication best practices

This enhancement demonstrates my ability to secure real-world software systems and produce trustworthy, resilient applications.

---

## 4. Reflection on the Enhancement Process

Through this enhancement, I deepened my understanding of secure software engineering. One of the biggest challenges was identifying subtle vulnerabilities in everyday code, such as trusting UI inputs or assuming database fields will always be valid. Enhancing authentication logic required reviewing how Android manages user sessions and determining the best balance between simplicity and security for a mobile application of this scope.

I also gained experience improving security without breaking existing functionality, which required careful incremental testing. This enhancement demonstrated how small changes—such as improving validation or adding safer defaults—can significantly strengthen a system's resilience.

Overall, this enhancement improved my skills in secure design, defensive programming, error handling, and secure database operations—all essential for professional software development.

---

## Files Included in This Enhancement

```
original/
    (original authentication and input-handling files)

enhanced/
    (enhanced versions with secure validation & safer database interactions)

index.md
    (this narrative)
```

---

This enhancement demonstrates my ability to design, implement, and deliver secure, professional-grade computing solutions.
