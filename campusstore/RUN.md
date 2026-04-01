# RUN.md — Campus Store Application

## Java Version
Java 17 (or higher). The Codespace environment uses Java 25.0.1.

## How the Database is Created
The database is created manually in MySQL before running the application.
Hibernate auto-creates and updates all tables using `spring.jpa.hibernate.ddl-auto=update`.
Seed data (admin account, one customer, 3 categories, 8 products) is inserted automatically
on first startup using a `CommandLineRunner` bean in `DataSeeder.java`.

## Database Credentials
Database credentials are set in:
`src/main/resources/application.properties`
```
spring.datasource.url=jdbc:mysql://localhost:3306/campusstore
spring.datasource.username=root
spring.datasource.password=root
```

## Steps to Run the Application

### Step 1 — Start MySQL
```
sudo service mysql start
```

### Step 2 — Start the Application
From the project root folder:
```
./mvnw spring-boot:run
```

### Step 3 — Open in Browser
In GitHub Codespaces, go to the Ports tab, find port 8080,
and click the globe icon to open the forwarded URL.

## How to Log in as ADMIN
The admin account is created automatically by the seed script on first startup.

- Email: admin123@campusstore.com
- Password: Admin@123

## How to Log in as CUSTOMER
A default customer is seeded automatically. Additional customers can be
registered through the /register page.

- Default customer email: karan@campusstore.com
- Default customer password: Karan@123
- Or register a new account at /register

## Seed Data
The following seed data is inserted automatically on first startup
if no users exist in the database:
- 1 ADMIN account
- 1 CUSTOMER account
- 3 categories: Electronics, Clothing, Stationery
- 8 active products across all categories

## Security Note
CSRF protection is disabled in this application to simplify form
submissions during development and testing. This is configured in
`SecurityConfig.java` using `csrf.disable()`.