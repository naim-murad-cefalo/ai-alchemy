# Database Seeding Guide

This document explains how to run the Wish Tracker application with seed data for testing and development.

## Quick Start

### Option 1: Production Mode (Build JAR first)
```bash
./run-with-seed.sh
```

### Option 2: Development Mode (Faster, with hot reload)
```bash
./dev-run-with-seed.sh
```

### Option 3: Using Maven directly
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seed
```

## What Gets Seeded?

When you run the application with the `seed` profile, the following demo data is automatically created:

### Demo User
- **Email:** demo@wishtracker.com
- **Name:** Demo User
- **Avatar:** Generated avatar

### Categories (6 total)
1. **Travel** - Travel plans and destinations (Red: #FF6B6B)
2. **Books** - Reading list and literary goals (Teal: #4ECDC4)
3. **Fitness** - Health and fitness goals (Mint: #95E1D3)
4. **Career** - Professional development (Pink: #F38181)
5. **Hobbies** - Leisure activities and hobbies (Purple: #AA96DA)
6. **Learning** - Educational pursuits (Light Pink: #FCBAD3)

### Wishes (19 total)

#### WISH Status (7 wishes)
- Visit Japan
- Backpack through Europe
- Read 50 books this year
- Start a book club
- Learn Spanish
- Master Machine Learning
- Learn to play guitar

#### IN_PROGRESS Status (6 wishes)
- Run a marathon
- Lose 10 kg
- Get AWS certification
- Launch side project
- Build a vegetable garden
- Learn photography

#### ACHIEVED Status (6 wishes)
- Visit Iceland
- Road trip across California
- Read all Harry Potter books
- Read 'The Lord of the Rings' trilogy
- Run a 5K race
- Do 100 push-ups in a row

## Scripts Explained

### `run-with-seed.sh`
- Builds the application (creates JAR file)
- Runs in production mode with seed profile
- **Use when:** Testing production build with seed data

### `dev-run-with-seed.sh`
- Skips build step
- Uses Maven Spring Boot plugin
- Enables hot reload for development
- **Use when:** Developing with seed data (faster startup)

### `seed-data.sh`
- Information script
- Shows available seeding options
- **Use when:** You need guidance on seeding options

## How It Works

The seeding is implemented in `src/main/java/com/wishtracker/config/DatabaseSeeder.java`:

1. **Profile-based:** Only runs when `seed` profile is active
2. **Safe:** Checks if data exists before seeding (won't duplicate)
3. **Comprehensive:** Creates users, categories, and wishes with realistic data
4. **Timestamped:** Uses varied creation dates for realistic testing

## Database Considerations

### H2 Database (Default)
- Data is stored in memory or file (check `application.properties`)
- Data persists between restarts if using file-based H2
- To reset: Delete the H2 database file or restart with in-memory mode

### PostgreSQL/MySQL
- Data persists in the database
- To reset: Drop and recreate the database, or delete all records
- Seeder won't run if data already exists

## Clearing and Reseeding

### For H2 In-Memory Database
Just restart the application - data is cleared automatically.

### For H2 File-Based Database
```bash
# Delete the database file (check application.properties for location)
rm wishtracker.db*
# Then run with seed
./dev-run-with-seed.sh
```

### For PostgreSQL/MySQL
```sql
-- Connect to your database and run:
DELETE FROM wishes;
DELETE FROM categories;
DELETE FROM users;

-- Then run the application with seed profile
```

## Running Without Seed Data

To run the application **without** seed data, simply start it normally:

```bash
# Development
mvn spring-boot:run

# Production
mvn clean package
java -jar target/*.jar
```

## Customizing Seed Data

To customize the seed data:

1. Open `src/main/java/com/wishtracker/config/DatabaseSeeder.java`
2. Modify the methods:
   - `createDemoUser()` - Change user details
   - `createCategory()` - Add/modify categories
   - `createWishesInWishStatus()` - Add/modify wishes
   - `createWishesInProgressStatus()` - Add/modify in-progress wishes
   - `createWishesAchievedStatus()` - Add/modify achieved wishes
3. Restart the application with seed profile

## Troubleshooting

### "Database already contains data"
The seeder detects existing data and skips seeding. Clear the database first (see above).

### Maven not found
```bash
# Install Maven
# macOS
brew install maven

# Ubuntu/Debian
sudo apt install maven

# Windows
# Download from https://maven.apache.org/download.cgi
```

### Permission denied when running scripts
```bash
chmod +x run-with-seed.sh dev-run-with-seed.sh seed-data.sh
```

### Port already in use
Another instance might be running. Check `application.properties` for the port (default: 8080).
```bash
# Find and kill the process using port 8080
lsof -ti:8080 | xargs kill -9
```

## Environment Variables

You can also use environment variables:

```bash
# Run with seed profile
SPRING_PROFILES_ACTIVE=seed mvn spring-boot:run

# Or for JAR
SPRING_PROFILES_ACTIVE=seed java -jar target/*.jar
```

## Notes

- **OAuth2:** The demo user uses a local provider. For OAuth2 testing, you'll need to authenticate through Google OAuth.
- **Security:** Seed data is for development/testing only. Don't use in production.
- **Performance:** Seeding creates ~20 records. Should complete in under 1 second.

## Next Steps

After seeding:
1. Open your browser to `http://localhost:8080`
2. Authenticate (OAuth2 or use demo user if local auth is enabled)
3. Explore the seeded wishes and categories
4. Test the application with realistic data!
