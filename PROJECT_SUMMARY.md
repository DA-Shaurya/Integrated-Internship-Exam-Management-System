# IEMS — Integrated Internship & Exam Management System
### Project Summary (as of 25 April 2026)

---

## 📌 Overview

IEMS is a full-stack web application for managing student internship applications and online examinations.

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.x (Java 17/25), Spring Data JPA, Spring Security |
| Database | MySQL (`iems_db`) |
| Auth | JWT (JJWT library, HS256) |
| Frontend | React 19 (Create React App), React Router v7, Axios |
| Port — Backend | `8081` |
| Port — Frontend | `3000` |

---

## 🗄️ Database Schema (`iems_db`)

### Tables & Relationships

```
users           → stores all users (STUDENT / ADMIN role)
students        → extended student profile
companies       → company info for internships
internships     → internship listings linked to companies
applications    → student internship applications (status: APPLIED / SHORTLISTED / REJECTED / SELECTED)
exams           → exam definitions (name, duration)
questions       → MCQ questions linked to an exam
options         → answer options for each question (with isCorrect flag)
exam_attempts   → tracks each student's exam session (status: IN_PROGRESS / COMPLETED)
answers         → auto-saved student answers per attempt
results         → final score after exam submission
audit_logs      → tracks security events (e.g. tab switches)
```

### Seeded Data
- **Users:** `shaurya@gmail.com / 1234` (STUDENT), `test@gmail.com / 1234` (STUDENT)
- **Companies:** 1 company (company_id = 1)
- **Internships:** `Software Intern` (₹20,000/mo, deadline 2026-05-01), `Backend Intern` (₹15,000/mo, deadline 2026-06-01)
- **Exam:** `exam_id = 1` — Software Engineering MCQ
- **Questions (5 total):**
  1. What is Java?
  2. What is OOP?
  3. Which keyword is used to inherit a class in Java?
  4. What does SQL stand for?
  5. Which data structure follows LIFO order?
- Each question has 3–4 MCQ options with one correct answer marked.

---

## ⚙️ Backend — Spring Boot

### Package Structure
```
com.example.demo/
├── DemoApplication.java          # Entry point
├── config/
│   ├── JwtUtil.java              # JWT generation & validation (HS256, 10hr expiry)
│   └── SecurityConfig.java       # Spring Security — all routes permitted (dev mode)
├── model/
│   ├── User.java                 # id, email, password, name, role (enum)
│   ├── Role.java                 # Enum: STUDENT, ADMIN
│   ├── Student.java
│   ├── Company.java
│   ├── Internship.java
│   ├── Application.java          # status: APPLIED/SHORTLISTED/REJECTED/SELECTED
│   ├── Exam.java
│   ├── Question.java             # + options List<Option> (EAGER fetch) ← FIXED
│   ├── Option.java               # optionText, isCorrect
│   ├── ExamAttempt.java          # userId, exam, status
│   ├── Answer.java               # attemptId, questionId, selectedOption (optionId)
│   ├── Result.java               # obtainedMarks, totalMarks
│   └── AuditLog.java             # userId, action, details
├── repository/                   # JpaRepository interfaces for all 13 models
│   └── QuestionRepository.java   # + findByExam_ExamId(int) ← ADDED
├── service/
│   ├── AuthService.java          # register, login (case-insensitive email lookup)
│   ├── InternshipService.java    # list all internships
│   ├── ApplicationService.java   # apply (duplicate check), updateStatus
│   ├── ExamService.java          # startExam, submitExam (auto-score)
│   └── AnswerService.java        # saveAnswer (auto-save)
└── controller/
    ├── AuthController.java        # POST /api/auth/register, /api/auth/login
    ├── InternshipController.java  # GET  /api/internships
    ├── ApplicationController.java # POST /api/applications/apply
    ├── ExamController.java        # POST /api/exam/start, /api/exam/submit
    ├── QuestionController.java    # GET  /api/questions/exam/{examId} ← CREATED
    ├── AnswerController.java      # POST /api/answers/save
    ├── AuditController.java       # POST /api/audit/
    └── StudentController.java
```

### Key API Endpoints

| Method | URL | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user, returns JWT + user |
| POST | `/api/auth/login` | Login with email + password, returns JWT + user |
| GET  | `/api/internships` | List all internships |
| POST | `/api/applications/apply` | Apply to an internship (duplicate-guarded) |
| POST | `/api/exam/start` | Start an exam attempt |
| GET  | `/api/questions/exam/{id}` | Fetch all questions + options for an exam |
| POST | `/api/answers/save` | Auto-save an answer during exam |
| POST | `/api/exam/submit` | Submit exam and get score |
| POST | `/api/audit/` | Log a security event |

---

## 🖥️ Frontend — React

### Page Structure
```
src/
├── App.js                  # Router — 4 routes defined
├── index.css               # Global dark-theme design system
├── services/
│   └── api.js              # Axios instance → http://localhost:8081/api
└── pages/
    ├── Login.js            # Email/password login form
    ├── Internships.js      # Lists internships, Apply + Take Exam buttons, Logout
    └── Exam.js             # MCQ exam with timer, auto-save, tab-switch detection
```

### Routes

| Path | Component | Description |
|---|---|---|
| `/` | `Login` | Login page |
| `/internships` | `Internships` | Internship listings dashboard |
| `/exam` | `Exam` | Online exam module |
| `/dashboard` | `Internships` | Post-exam redirect destination |

### Exam Features
- ⏱️ **60-minute countdown timer** — auto-submits on expiry
- 💾 **Auto-save** — each answer is saved to backend immediately on selection
- 🚨 **Tab-switch detection** — warns 3 times, then force-submits exam
- 📊 **Score display** — obtained marks shown on submission

---

## 🐛 Bugs Fixed During Development

| # | Bug | Fix |
|---|---|---|
| 1 | Login alert showed `[object Object]` on error | Changed `err.response?.data` → `err.response?.data?.message` |
| 2 | `/exam` route was declared outside `<Routes>`, page was unreachable | Moved `<Route path="/exam">` inside the `<Routes>` block in `App.js` |
| 3 | `/dashboard` route didn't exist, post-exam navigation crashed | Added `<Route path="/dashboard" element={<Internships />} />` |
| 4 | `GET /api/questions/exam/1` returned 404 — no controller existed | Created `QuestionController.java` with the missing endpoint |
| 5 | Questions loaded but no options appeared in the exam UI | Added `@OneToMany(fetch = EAGER)` `options` field to `Question.java` |
| 6 | `QuestionRepository` had no method to query by exam | Added `findByExam_ExamId(int examId)` method |

---

## 🔐 Auth Flow

```
1. User enters email + password → POST /api/auth/login
2. Backend looks up user (case-insensitive), verifies plain-text password
3. JWT token generated (email + role + userId, 10hr expiry, HS256)
4. Frontend stores { token, user } in localStorage under key "user"
5. All subsequent pages read user from localStorage
```

> ⚠️ **Note:** Passwords are stored as plain text. For production, implement BCrypt hashing via PasswordEncoder.

---

## 🚀 How to Run

### Backend
```powershell
cd project/demo
.\mvnw.cmd spring-boot:run
# Starts on http://localhost:8081
```

### Frontend
```powershell
cd project/frontend
npm start
# Starts on http://localhost:3000
```

### Login Credentials
| Email | Password | Role |
|---|---|---|
| shaurya@gmail.com | 1234 | STUDENT |
| test@gmail.com | 1234 | STUDENT |

---

## 🔮 What's Not Yet Implemented

- [ ] Admin dashboard (manage internships, view applications, set exam results)
- [ ] CGPA-based filtering for internship applications
- [ ] JWT token sent as `Authorization` header on API calls (currently unauthenticated)
- [ ] BCrypt password hashing
- [ ] Student registration UI (currently only API exists)
- [ ] Application status tracking UI for students
- [ ] Exam result history page
