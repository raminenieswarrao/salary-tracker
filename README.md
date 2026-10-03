# Salary Tracker

Personal client salary/payment tracking app built with Java 21, Spring Boot, Thymeleaf, and PostgreSQL.

## Features

- Add, edit, and delete clients
- Add, edit, and delete monthly payment records
- Track work month, hours, credited amount, status, paid month, and notes
- Payment statuses: Full Paid, Partially Paid, Not Paid
- Client profile calculations:
  - Total working hours
  - Total amount received (full + partial payments)
  - Total full-paid hours
  - Total not-paid hours (partial-month hours + unpaid-month hours)
  - Total partial-paid month hours
  - Total partial amount received
- Global transaction page with client/year/month/status filters
- Responsive mobile-friendly UI
- Client deletion cascades to its monthly records
- Duplicate client + work-month records are prevented

## PostgreSQL expected locally

Database: `salary_tracker`

User: `salary_app`

The project uses the PostgreSQL database you already created.

## IntelliJ run setup

1. Open this folder as a Maven project in IntelliJ IDEA.
2. Make sure Project SDK is Java 21.
3. Open **Run > Edit Configurations**.
4. Select `SalaryTrackerApplication`.
5. Add this environment variable:

   `SALARY_DB_PASSWORD=YOUR_salary_app_PASSWORD`

6. Run `SalaryTrackerApplication`.
7. Open http://localhost:8080

The application already defaults to:

- URL: `jdbc:postgresql://localhost:5432/salary_tracker`
- User: `salary_app`
- Port: `8080`

Optional environment variables:

- `SALARY_DB_URL`
- `SALARY_DB_USER`
- `SALARY_DB_PASSWORD`
- `SERVER_PORT`

## Database behavior

`spring.jpa.hibernate.ddl-auto=update` is enabled so the app can work with the existing two tables and can recreate the schema later on your Beelink PostgreSQL database if needed.

For production on the Beelink, keep PostgreSQL port 5432 private and expose only the web application through your chosen secure access method.
