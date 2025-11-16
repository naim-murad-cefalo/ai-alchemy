# Wish Tracker

A modern wish tracking application with Kanban board visualization and Google SSO authentication. Track your wishes, organize them by categories, and visualize your progress from wish to achievement.

## Features

- **Google SSO Authentication** - Secure login using your Google account
- **User-Scoped Data** - Complete data isolation between users
- **Category Management** - Organize wishes with custom categories and colors
- **Kanban Board** - Visual tracking with three status columns (Wish → In Progress → Achieved)
- **Status Transitions** - Structured workflow for wish progression
- **Responsive Design** - Works seamlessly on desktop and mobile devices
- **Real-time Filtering** - Filter wishes by category
- **Data Persistence** - H2 in-memory database (easily upgradeable to production database)

## Technology Stack

- **Java 21** (LTS)
- **Spring Boot 3.3.5** (Latest stable)
- **Spring Data JPA** (Database access)
- **Spring Security** with OAuth2 Client (Google SSO)
- **Thymeleaf** (Server-side rendering)
- **H2 Database** (In-memory)
- **Bootstrap 5** (UI framework)
- **JUnit 5 & Mockito** (Testing)
- **Maven** (Build tool)

## Prerequisites

Before running this application, ensure you have:

- **Java 21** installed ([Download](https://www.oracle.com/java/technologies/downloads/#java21))
- **Maven 3.6+** installed ([Download](https://maven.apache.org/download.cgi))
- **Google OAuth2 credentials** (see setup instructions below)

Verify installations:
```bash
java -version  # Should show Java 21
mvn -version   # Should show Maven 3.6+
```

## Google OAuth2 Setup

To enable Google SSO authentication, you need to obtain OAuth2 credentials from Google Cloud Console.

### Step 1: Create Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click on the project dropdown (top left) and click **"New Project"**
3. Enter project name (e.g., "Wish Tracker") and click **Create**
4. Wait for project creation and select your new project

### Step 2: Enable Google+ API

1. In your project, navigate to **APIs & Services > Library**
2. Search for **"Google+ API"** (or "People API")
3. Click on it and click **Enable**

### Step 3: Create OAuth2 Credentials

1. Navigate to **APIs & Services > Credentials**
2. Click **Create Credentials** → **OAuth client ID**
3. If prompted, configure the OAuth consent screen:
   - Choose **External** user type
   - Fill in:
     - App name: "Wish Tracker"
     - User support email: your email
     - Developer contact: your email
   - Click **Save and Continue**
   - Skip scopes (click **Save and Continue**)
   - Add test users (your email) and click **Save and Continue**
4. Back to credentials, click **Create Credentials** → **OAuth client ID**
5. Choose **Web application**
6. Configure:
   - Name: "Wish Tracker Web Client"
   - Authorized JavaScript origins: `http://localhost:8080`
   - Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
7. Click **Create**
8. **IMPORTANT**: Copy your **Client ID** and **Client Secret** - you'll need these!

### Step 4: Set Environment Variables

Set your Google OAuth2 credentials as environment variables:

**Linux/macOS:**
```bash
export GOOGLE_CLIENT_ID="your-client-id-here"
export GOOGLE_CLIENT_SECRET="your-client-secret-here"
```

**Windows (Command Prompt):**
```cmd
set GOOGLE_CLIENT_ID=your-client-id-here
set GOOGLE_CLIENT_SECRET=your-client-secret-here
```

**Windows (PowerShell):**
```powershell
$env:GOOGLE_CLIENT_ID="your-client-id-here"
$env:GOOGLE_CLIENT_SECRET="your-client-secret-here"
```

**Alternative: Create `.env` file** (for development only, don't commit!):
```bash
# Create .env file in project root
echo "export GOOGLE_CLIENT_ID=your-client-id-here" > .env
echo "export GOOGLE_CLIENT_SECRET=your-client-secret-here" >> .env

# Source it before running
source .env
```

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd wish-tracker
```

### 2. Set Environment Variables

Set your Google OAuth2 credentials (see Step 4 above).

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

You'll be redirected to the login page. Click "Sign in with Google" to authenticate.

## H2 Database Console (Development Only)

For debugging purposes, you can access the H2 database console:

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:wishtracker
Username: sa
Password: (leave blank)
```

**Note**: This is only available in development mode.

## Testing

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage Report

```bash
mvn test jacoco:report
```

Coverage report will be generated at: `target/site/jacoco/index.html`

### Test Coverage Goals

- **Unit Tests**: 80%+ coverage
- **Integration Tests**: All critical user flows
- **Security Tests**: User isolation and authentication

## Project Structure

```
src/
├── main/
│   ├── java/com/wishtracker/
│   │   ├── WishTrackerApplication.java  # Main application class
│   │   ├── controller/                  # Web controllers (user-scoped)
│   │   ├── model/                       # JPA entities (User, Wish, Category)
│   │   ├── repository/                  # Data access layer (user-filtered queries)
│   │   ├── service/                     # Business logic (user context)
│   │   ├── dto/                         # Data Transfer Objects
│   │   ├── config/                      # Spring configuration
│   │   └── security/                    # OAuth2 and security
│   └── resources/
│       ├── application.properties       # Configuration
│       ├── data.sql                     # Sample data
│       ├── templates/                   # Thymeleaf templates
│       │   ├── layout.html             # Base layout
│       │   ├── login.html              # Login page
│       │   ├── wishes/                 # Wish management views
│       │   └── categories/             # Category management views
│       └── static/                      # Static assets
│           ├── css/style.css           # Custom styles
│           └── js/app.js               # Client-side JavaScript
└── test/
    └── java/com/wishtracker/           # Test classes
```

## Usage Guide

### First Login

1. Navigate to `http://localhost:8080`
2. Click "Sign in with Google"
3. Authenticate with your Google account
4. You'll be redirected to the wishes dashboard

**Note**: A user account is automatically created on first login.

### Managing Categories

1. Navigate to **Categories** from the navbar
2. Click **Add Category**
3. Enter:
   - Category name (e.g., "Travel", "Books", "Fitness")
   - Description (optional)
   - Color (pick a color for visual organization)
4. Click **Save**

### Creating Wishes

1. Navigate to **Wishes** from the navbar
2. Click **Add New Wish**
3. Fill in:
   - Title (required)
   - Description (optional)
   - Category (required - must create categories first)
   - Status (defaults to "Wish")
   - Remarks (optional)
4. Click **Save**

### Tracking Progress

Your wishes appear on the Kanban board in three columns:

- **Wish**: New wishes waiting to be started
- **In Progress**: Wishes you're actively working on
- **Achieved**: Completed wishes

Move wishes through the workflow:
1. Click the **Start** button to move from Wish → In Progress
2. Click the **Complete** button to move from In Progress → Achieved

### Filtering Wishes

Use the category dropdown at the top of the Kanban board to filter wishes by category.

## Security Features

- **Google SSO Only**: No username/password authentication
- **User Data Isolation**: Users can only see and modify their own data
- **Ownership Verification**: All operations verify user ownership
- **Automatic User Creation**: User accounts created on first Google login
- **Session Management**: Secure session handling with Spring Security

## Design

The UI design follows the specifications from the Figma design:
- [Figma Design Reference](https://www.figma.com/proto/ZULiad73cd7g7FvpvBuTwE/Sensa-UI-UX?page-id=9130%3A3020&node-id=9136-3826)

Design principles:
- Clean, modern interface
- Consistent color palette
- Responsive layouts
- Accessibility considerations

## Development Approach

This project was developed using an **agentic development** approach with Claude Code:

### Plan → Execute → Review Cycles

1. **Plan Phase**: Breaking down requirements into clear, actionable tasks
2. **Execute Phase**: Implementing one layer at a time (entities → repositories → services → controllers → views)
3. **Review Phase**: Testing each component before moving to the next

### Key Practices

- User-scoped data access from the start
- Test-driven development with high coverage
- Continuous integration of security considerations
- Iterative design matching

## Future Enhancements

Potential features for future development:

- [ ] Drag-and-drop Kanban board
- [ ] Due dates and reminders
- [ ] Wish sharing with other users
- [ ] Data export (CSV, PDF)
- [ ] Wish attachments (images, links)
- [ ] Dashboard with statistics
- [ ] Email notifications
- [ ] Progressive Web App (PWA) support
- [ ] Production database support (PostgreSQL, MySQL)

## Troubleshooting

### OAuth2 Authentication Fails

**Issue**: "Error 401: invalid_client"

**Solution**:
1. Verify environment variables are set correctly
2. Check that redirect URI in Google Console matches exactly: `http://localhost:8080/login/oauth2/code/google`
3. Ensure OAuth consent screen is configured
4. Add your email as a test user in Google Console

### Application Won't Start

**Issue**: Port 8080 already in use

**Solution**:
```bash
# Change port in application.properties or:
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### H2 Console Not Accessible

**Issue**: 403 Forbidden on /h2-console

**Solution**: Ensure `spring.h2.console.enabled=true` in application.properties

## Contributing

This is a learning/demonstration project. Feel free to fork and modify for your own use.

## License

This project is developed for educational purposes as part of the AI Alchemy initiative.

## Credits

- Developed using agentic AI development practices with Claude Code
- UI/UX inspired by modern Kanban board applications
- Built with Spring Boot ecosystem

## Contact

For questions or feedback about this project, please refer to the documentation or create an issue in the repository.

---

**Happy Wish Tracking!**
