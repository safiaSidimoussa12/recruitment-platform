# CareerBridge 🌉

A full-stack recruitment platform built with Spring Boot, Thymeleaf, and MySQL.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Database Setup](#database-setup)
- [Project Structure](#project-structure)
- [Hiring Pipeline](#hiring-pipeline)
- [License](#license)

---

## Overview

CareerBridge is a multi-role recruitment platform that connects candidates with recruiters through a structured hiring pipeline. It supports three roles: **Candidate**, **Recruiter**, and **Admin**.

---

## ✨ Features

### Candidate

- Register and manage profile (photo, skills, CV upload)
- Search and filter job listings (keyword, city, domain, contract, salary)
- Apply to jobs with cover letter
- Track application status through hiring pipeline
- Save jobs to favorites with undo feature
- Real-time messaging with recruiters
- Global messages page with unread count

### Recruiter

- Register with admin approval workflow
- Publish, edit and archive job listings
- Manage hiring pipeline per job offer:
  - Applied → Shortlisted → Interview Scheduled → Interview Completed → Hired / Rejected
- Schedule interviews with date, time, location and notes
- Global messages page with unread count
- Recruiter dashboard with stats (active jobs, applications, unread messages, shortlisted, hired)

### Admin

- Approve or reject recruiter registrations
- Manage users (activate / suspend)
- Manage companies (activate / suspend)
- Platform statistics dashboard (total users, companies, active jobs, pending recruiters)

---

## 🛠 Tech Stack

| Layer    | Technology                                  |
| -------- | ------------------------------------------- |
| Backend  | Spring Boot 4.1.0                           |
| Security | Spring Security 6.x                         |
| ORM      | Spring Data JPA / Hibernate                 |
| Frontend | Thymeleaf + Bootstrap 5.3 + Bootstrap Icons |
| Database | MySQL 8.x                                   |
| Build    | Maven                                       |
| Java     | Java 21                                     |
| IDE      | VS Code                                     |

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven
- MySQL 8.x
- VS Code (recommended)

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/safiaSidimoussa12/recruitment-platform.git
cd recruitment-platform
```

**2. Create the database**

```sql
CREATE DATABASE jobboard_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**3. Configure environment variables**

Create a `.env` file at the root of the project:

```env
DB_URL=jdbc:mysql://localhost:3306/jobboard_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=yourpassword
APP_PORT=8080
```

**4. Run the required SQL commands** (see Database Setup section below)

**5. Run the application**

```bash
mvn spring-boot:run
```

**6. Access the application**

```
http://localhost:8080
```

---

## 👤 Default Accounts

> These accounts are inserted automatically via `data.sql` on first run.

| Role      | Email              | Password    |
| --------- | ------------------ | ----------- |
| Candidate | candidat@test.com  | password123 |
| Recruiter | recruteur@test.com | password123 |

> Admin account must be created manually — register as a candidate then update the role in MySQL.

---

## 🗄 Database Setup

### Required ALTER TABLE commands

Run these SQL commands after the first launch to ensure all enum values are correctly set:

```sql
-- Add EN_ATTENTE status for recruiter approval workflow
ALTER TABLE utilisateur
MODIFY COLUMN statut ENUM('ACTIF', 'SUSPENDU', 'EN_ATTENTE') NOT NULL DEFAULT 'ACTIF';

-- Update candidature pipeline statuses
ALTER TABLE candidature
MODIFY COLUMN statut ENUM(
    'APPLIED',
    'REVIEWED',
    'SHORTLISTED',
    'INTERVIEW_SCHEDULED',
    'INTERVIEW_COMPLETED',
    'HIRED',
    'REJECTED'
) NOT NULL DEFAULT 'APPLIED';

-- Allow nullable entreprise for rejected recruiters
ALTER TABLE recruteur MODIFY COLUMN entreprise_id BIGINT NULL;
```

### Admin Account

The admin account is created automatically on first launch using environment variables.

Add these variables to your `.env` file:

```env
ADMIN_EMAIL=admin@careerbridge.com
ADMIN_PASSWORD=yourSecurePassword
```

> **Never commit your `.env` file to Git.**

## 📁 Project Structure

```
src/main/java/com/jobboard/jobboard/
│
├── module/
│   ├── auth/           # Authentication & registration
│   ├── candidat/       # Candidate profile & CV
│   ├── recruteur/      # Recruiter profile & dashboard
│   ├── entreprise/     # Company management
│   ├── offre/          # Job listings
│   ├── candidature/    # Applications & pipeline
│   ├── messagerie/     # Messaging system
│   ├── favori/         # Saved jobs
│   └── admin/          # Admin panel
│
└── shared/
    ├── domain/         # Base entities & enums
    ├── exception/      # Global exception handler
    └── config/         # Security & global config

src/main/resources/
├── templates/
│   ├── layout/         # Base navbar fragment
│   ├── auth/           # Login & register pages
│   ├── offre/          # Job listing & detail pages
│   ├── candidat/       # Candidate pages
│   ├── recruteur/      # Recruiter pages
│   ├── messagerie/     # Conversation page
│   ├── admin/          # Admin pages
│   └── error/          # Error pages (400, 404)
├── static/
│   ├── css/            # Custom styles
│   └── js/             # Custom scripts
├── application.yaml    # App configuration
└── data.sql            # Initial data
```

---

## 🔐 Hiring Pipeline

```
APPLIED → SHORTLISTED → INTERVIEW_SCHEDULED → INTERVIEW_COMPLETED → HIRED
                                                                   ↘ REJECTED
```

- Rejection is possible at any stage before HIRED
- Each stage transition is logged and visible to the candidate
- Interview scheduling includes date, time, location, interviewer and notes
- Pipeline is per job offer — each job has its own independent pipeline

---

## 🔑 Role-Based Access Control

| Feature            | Candidate | Recruiter | Admin |
| ------------------ | --------- | --------- | ----- |
| Browse jobs        | ✅        | ✅        | ✅    |
| Apply to jobs      | ✅        | ❌        | ❌    |
| Publish jobs       | ❌        | ✅        | ❌    |
| Manage pipeline    | ❌        | ✅        | ❌    |
| Approve recruiters | ❌        | ❌        | ✅    |
| Manage users       | ❌        | ❌        | ✅    |
| Messaging          | ✅        | ✅        | ❌    |

---

## 📄 License

This project is for educational purposes.
