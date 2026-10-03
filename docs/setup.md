# Salary Tracker Setup Guide

This guide explains how to clone, configure, and run Salary Tracker locally.

## 1. Prerequisites

Install the following before starting:

- Java 21
- PostgreSQL 17 or another compatible PostgreSQL version
- Git
- Maven, or IntelliJ IDEA with bundled Maven

You can verify the installations with:

```bash
java -version
git --version
mvn -version
```

If you use IntelliJ IDEA with bundled Maven, a separate Maven installation is optional.

---

## 2. Clone the Repository

Clone the project from GitHub:

```bash
git clone https://github.com/raminenieswarrao/salary-tracker.git
```

Move into the project directory:

```bash
cd salary-tracker
```

---

## 3. Create the PostgreSQL Database

Open PostgreSQL `psql`.

For example:

```bash
psql -U postgres
```

Create the application database:

```sql
CREATE DATABASE salary_tracker;
```

Create the application database user:

```sql
CREATE USER salary_app WITH PASSWORD 'your_password_here';
```

Grant access to the database:

```sql
GRANT ALL PRIVILEGES ON DATABASE salary_tracker TO salary_app;
```

Connect to the new database:

```sql
\c salary_tracker
```

Grant access to the public schema:

```sql
GRANT ALL ON SCHEMA public TO salary_app;
```

Set the application user as the owner of the public schema:

```sql
ALTER SCHEMA public OWNER TO salary_app;
```

Exit PostgreSQL:

```sql
\q
```

---

## 4. Database Tables

You do not need to manually create the application tables.

The application uses:

```text
spring.jpa.hibernate.ddl-auto=update
```

Spring Boot and Hibernate automatically create or update the required tables when the application starts.

The main tables are:

```text
clients
monthly_payment_records
```

---

## 5. Default Database Configuration

The application uses the following defaults:

```text
Database host: localhost
Database port: 5432
Database name: salary_tracker
Database user: salary_app
Application port: 8080
```

Default JDBC connection:

```text
jdbc:postgresql://localhost:5432/salary_tracker
```

The database password is intentionally not stored in source code.

---

## 6. Configure the Database Password

The application expects the PostgreSQL password through this environment variable:

```text
SALARY_DB_PASSWORD
```

### Windows PowerShell

```powershell
$env:SALARY_DB_PASSWORD="your_password_here"
```

Start the application from the same PowerShell session.

---

## 7. Configure IntelliJ IDEA

Open the cloned project in IntelliJ IDEA.

Use:

```text
File
→ Open
→ salary-tracker
```

Open the project as a Maven project.

Make sure the Project SDK is Java 21:

```text
File
→ Project Structure
→ Project
→ SDK
→ Java 21
```

Then open:

```text
Run
→ Edit Configurations
```

Select:

```text
SalaryTrackerApplication
```

Add:

```text
SALARY_DB_PASSWORD=your_password_here
```

Apply the configuration.

---

## 8. Run from IntelliJ IDEA

Open:

```text
src/main/java/com/eswar/salarytracker/SalaryTrackerApplication.java
```

Run:

```text
SalaryTrackerApplication
```

When the application starts successfully, open:

```text
http://localhost:8080
```

---

## 9. Run with Maven

If Maven is installed, first set the database password:

```powershell
$env:SALARY_DB_PASSWORD="your_password_here"
```

Then run:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

---

## 10. Optional Environment Variables

The following environment variables can override the default configuration:

| Variable | Purpose | Default |
| --- | --- | --- |
| `SALARY_DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/salary_tracker` |
| `SALARY_DB_USER` | PostgreSQL user | `salary_app` |
| `SALARY_DB_PASSWORD` | PostgreSQL password | No default |
| `SERVER_PORT` | Web application port | `8080` |

Example:

```powershell
$env:SALARY_DB_URL="jdbc:postgresql://localhost:5432/salary_tracker"
$env:SALARY_DB_USER="salary_app"
$env:SALARY_DB_PASSWORD="your_password_here"
$env:SERVER_PORT="8080"

mvn spring-boot:run
```

---

## 11. Application Features

Salary Tracker currently supports:

- Multiple clients
- Client hourly rate tracking
- Vendor percentage tracking
- Effective hourly rate calculation
- Actual employment start and end dates
- Payment start and end months
- Monthly working-hours tracking
- Full-paid payment status
- Partially-paid payment status
- Not-paid payment status
- Amount-received tracking
- Paid-month tracking
- Client-level payment summaries
- Gross salary calculation
- Transaction filtering
- Work year filtering
- Work month filtering
- Paid year filtering
- Paid month filtering
- Payment-status filtering
- Filtered total working hours
- Filtered total earned amount
- Add, edit, and delete clients
- Add, edit, and delete payment records
- Responsive desktop and mobile interface
- iPhone home-screen application icon

---

## 12. Application Data

The GitHub repository contains application source code only.

Personal salary and payment data is stored inside the PostgreSQL database running on the user's own machine.

Cloning this repository does not provide access to another user's salary data.

Each user creates and maintains their own PostgreSQL database.

---

## 13. Important Security Notice

The current version of Salary Tracker does not include user authentication.

Do not expose this application directly to the public internet without adding authentication or placing it behind a secure private-access solution.

PostgreSQL port:

```text
5432
```

should remain private and should not be exposed directly to the internet.

Never commit:

- PostgreSQL passwords
- Environment files containing secrets
- Database backups containing personal salary data
- Private credentials
- API keys

The project uses environment variables so database passwords do not need to be stored in source code.

---

## 14. Troubleshooting

### Application cannot connect to PostgreSQL

Verify PostgreSQL is running.

On Windows:

```powershell
Get-Service *postgres*
```

Make sure the PostgreSQL service is running.

Verify:

```text
Database: salary_tracker
User: salary_app
Port: 5432
```

Also confirm that:

```text
SALARY_DB_PASSWORD
```

contains the correct password.

### Port 8080 is already in use

Use another application port:

```powershell
$env:SERVER_PORT="8081"
mvn spring-boot:run
```

Then open:

```text
http://localhost:8081
```

### Maven command is not available

Check:

```bash
mvn -version
```

If Maven is not installed, either install Maven or use IntelliJ IDEA's bundled Maven support.

### Java version is incorrect

Check:

```bash
java -version
```

The application requires Java 21.

---

## 15. Repository

GitHub:

https://github.com/raminenieswarrao/salary-tracker