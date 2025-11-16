# Wish Tracker - Development Rules & Standards

## Technology Stack (MANDATORY)
- Java 21 (latest LTS features)
- Spring Boot 3.3.x (latest stable)
- Thymeleaf for server-side rendering
- H2 in-memory database
- Spring Data JPA
- Spring Security with OAuth2 Client (Google SSO)
- JUnit 5 + Mockito for testing
- Maven build tool
- Bootstrap 5 for UI styling

## Project Structure
```
src/main/java/com/wishtracker/
  ├── WishTrackerApplication.java
  ├── controller/
  │   ├── WishController.java
  │   ├── CategoryController.java
  │   └── AuthController.java
  ├── model/
  │   ├── User.java
  │   ├── Wish.java
  │   ├── Category.java
  │   └── WishStatus.java (enum)
  ├── repository/
  │   ├── UserRepository.java
  │   ├── WishRepository.java
  │   └── CategoryRepository.java
  ├── service/
  │   ├── UserService.java
  │   ├── WishService.java
  │   └── CategoryService.java
  ├── dto/
  │   ├── WishDTO.java
  │   └── CategoryDTO.java
  ├── config/
  │   └── SecurityConfig.java
  └── security/
      └── CustomOAuth2UserService.java

src/main/resources/
  ├── application.properties
  ├── data.sql (sample data)
  ├── templates/
  │   ├── layout.html (base template)
  │   ├── login.html
  │   ├── index.html
  │   ├── wishes/
  │   │   ├── list.html (Kanban board)
  │   │   ├── form.html
  │   │   └── view.html
  │   └── categories/
  │       ├── list.html
  │       └── form.html
  └── static/
      ├── css/
      │   └── style.css
      └── js/
          └── app.js

src/test/java/com/wishtracker/
  ├── controller/
  ├── service/
  └── repository/
```

## Domain Model Requirements

### User Entity
- id (Long, auto-generated)
- email (String, unique, required - from Google OAuth)
- name (String, required - from Google OAuth)
- pictureUrl (String, optional - from Google OAuth profile picture)
- createdDate (LocalDateTime, auto)
- lastLoginDate (LocalDateTime, updated on each login)
- categories (OneToMany relationship with Category, cascade all)
- wishes (OneToMany relationship with Wish, cascade all)

**IMPORTANT**: User is created automatically on first Google SSO login

### Category Entity
- id (Long, auto-generated)
- name (String, required, max 100)
- description (String, optional, max 500)
- color (String, hex color for UI - MUST match Figma design)
- user (ManyToOne relationship with User, required)
- wishes (OneToMany relationship with Wish, cascade all)
- createdDate (LocalDateTime, auto)

**UNIQUE CONSTRAINT**: (name, user_id) - same user cannot have duplicate category names

### Wish Entity
- id (Long, auto-generated)
- title (String, required, max 200)
- description (String, optional, max 1000)
- status (Enum: WISH, IN_PROGRESS, ACHIEVED)
- remarks (String, optional, max 500)
- category (ManyToOne relationship with Category, required)
- user (ManyToOne relationship with User, required)
- createdDate (LocalDateTime, auto)
- updatedDate (LocalDateTime, auto)
- achievedDate (LocalDateTime, nullable)

### WishStatus Enum
- WISH ("Wish")
- IN_PROGRESS ("In Progress")
- ACHIEVED ("Achieved")

## Security & Authentication (MANDATORY)

### Google SSO Configuration
- Use Spring Security OAuth2 Client
- Google is the ONLY authentication provider
- No username/password authentication
- All endpoints except /login require authentication
- Redirect unauthenticated users to /login

### User Creation Flow
1. User clicks "Sign in with Google"
2. Google OAuth flow completes
3. CustomOAuth2UserService intercepts:
   - Extract email, name, picture from OAuth2 response
   - Check if user exists by email
   - If NOT exists: Create new User entity
   - If exists: Update lastLoginDate
4. User is authenticated and redirected to /wishes

### Authorization Rules
- Users can ONLY see their own wishes and categories
- Users can ONLY modify their own wishes and categories
- All repository queries MUST filter by current authenticated user
- Use @PreAuthorize or manual checks in service layer

### Security Implementation
```java
// Get current authenticated user in controllers/services:
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String email = auth.getName(); // This is the user's email
User currentUser = userService.findByEmail(email);
```

## Coding Standards

### Controllers
- Use `@Controller` not `@RestController` (Thymeleaf needs views)
- ALL operations MUST use currently authenticated user
- Follow RESTful URL patterns:
  - GET /wishes - list current user's wishes
  - GET /wishes/new - show form
  - POST /wishes - create for current user
  - GET /wishes/{id} - view (verify ownership)
  - GET /wishes/{id}/edit - edit form (verify ownership)
  - POST /wishes/{id} - update (verify ownership)
  - POST /wishes/{id}/delete - delete (verify ownership)
  - POST /wishes/{id}/status - change status (verify ownership)
- Same pattern for /categories (all user-scoped)
- Use `Model` to pass data to views
- Pass current user info to all views (for navbar)
- Use `RedirectAttributes` for flash messages
- Implement proper validation with `@Valid`
- Return 403 Forbidden if user tries to access another user's data

### Services
- Use `@Service` annotation
- ALL methods must accept User parameter or userId
- Implement business logic here, not in controllers
- Use `@Transactional` where needed
- Handle exceptions properly (include ownership validation)
- Return DTOs, not entities directly
- Implement status transition validation (WISH → IN_PROGRESS → ACHIEVED)
- Set achievedDate automatically when status becomes ACHIEVED
- Prevent access to data not owned by current user

### Repositories
- Extend `JpaRepository<Entity, Long>`
- ALL queries MUST filter by user
- Custom queries: 
  - findByUser(User user)
  - findByUserAndStatus(User user, WishStatus status)
  - findByUserAndCategory(User user, Category category)
  - findByIdAndUser(Long id, User user) - for ownership verification

### Custom OAuth2 User Service
- Extend DefaultOAuth2UserService
- Override loadUser method
- Extract: email, name, picture from OAuth2User attributes
- Call UserService to find or create user
- Update lastLoginDate
- Return OAuth2User with authorities

### Validation
- Use Bean Validation annotations (@NotBlank, @Size, @NotNull)
- Custom validation for status transitions
- Validate category belongs to current user before assigning to wish
- Validate ownership before any update/delete operation

### Exception Handling
- Create custom exceptions:
  - WishNotFoundException
  - CategoryNotFoundException
  - InvalidStatusTransitionException
  - UnauthorizedAccessException (when user tries to access other's data)
- Use `@ControllerAdvice` for global exception handling
- Return appropriate error pages with user-friendly messages

## UI/UX Requirements (MUST Match Figma Design)

### Figma Design Reference
URL: https://www.figma.com/proto/ZULiad73cd7g7FvpvBuTwE/Sensa-UI-UX?page-id=9130%3A3020&node-id=9136-3826

### Color Palette (Extract from Figma)
- Primary Brand Color: [Extract from Figma]
- Secondary Color: [Extract from Figma]
- Background: [Extract from Figma]
- Card Background: [Extract from Figma]
- Text Primary: [Extract from Figma]
- Text Secondary: [Extract from Figma]
- Success (ACHIEVED): [Extract from Figma - likely green]
- In Progress: [Extract from Figma - likely blue/orange]
- Wish: [Extract from Figma - likely gray]
- Border Color: [Extract from Figma]

### Typography (Match Figma)
- Font Family: [Extract from Figma]
- Heading Sizes: [Extract from Figma]
- Body Text: [Extract from Figma]
- Button Text: [Extract from Figma]

### Spacing & Layout (Match Figma)
- Container Max Width: [From Figma]
- Card Padding: [From Figma]
- Grid Gaps: [From Figma]
- Button Sizes: [From Figma]
- Border Radius: [From Figma]

### Login Page (login.html)
- Clean, centered design
- App logo/name at top
- "Sign in with Google" button (styled per Figma)
- No other login options
- Simple tagline/description
- Footer with credits

### Layout (layout.html)
- Navigation bar with:
  - Brand logo/name (left)
  - Links: Home, Wishes, Categories (center/left)
  - User info: Profile picture (circular), Name, Logout button (right)
- Flash message area (success/error alerts)
- Main content area
- Footer
- Responsive design
- MUST match Figma navigation design exactly

### Wish List Page - Kanban Board (wishes/list.html)
**CRITICAL**: This is the main page. Must match Figma design pixel-perfect.

Layout:
- Page title: "My Wishes" or as per Figma
- Filter section: Category dropdown (with "All Categories" option)
- "Add New Wish" button (prominent, styled per Figma)
- Three-column Kanban board:
  - Column 1: "Wish" (with count badge)
  - Column 2: "In Progress" (with count badge)
  - Column 3: "Achieved" (with count badge)

Each Wish Card shows:
- Title (bold, prominent)
- Category badge (with user's chosen color, rounded)
- Description preview (truncated, lighter text)
- Remarks (if present, in italic or different style)
- Created date (small, secondary text)
- Action buttons/icons:
  - View (eye icon)
  - Edit (pencil icon)
  - Delete (trash icon with confirmation)
  - Status transition button (styled per Figma):
    - WISH: "Start" or right arrow
    - IN_PROGRESS: "Complete" or checkmark
    - ACHIEVED: No button

Card Styling (per Figma):
- Different background colors/borders for each status
- Shadow on hover
- Smooth transitions
- Proper spacing between cards
- Responsive card width

Empty States:
- Show friendly message if no wishes in a column
- "Get started" call-to-action

### Wish Form (wishes/form.html)
- Clean, centered form (match Figma design)
- Fields:
  - Title* (text input)
  - Description (textarea, auto-resize)
  - Category* (dropdown with user's categories)
  - Status* (radio buttons or segmented control per Figma)
  - Remarks (textarea)
- Buttons:
  - Cancel (secondary style)
  - Save (primary style)
- Show validation errors inline
- Pre-populate for edit mode

### Wish Details (wishes/view.html)
- Large, detailed view
- All wish information displayed
- Status badge prominent
- Category badge
- Action buttons: Edit, Delete, Back
- Match Figma card detail design

### Category List (categories/list.html)
- Grid or list view (per Figma)
- Each category shows:
  - Color preview (circle or square)
  - Name
  - Description
  - Wish count
  - Actions: Edit, Delete (with confirmation if has wishes)
- "Add Category" button
- Match Figma design for category cards

### Category Form (categories/form.html)
- Simple form
- Fields:
  - Name*
  - Description
  - Color* (color picker matching Figma palette)
- Cancel and Save buttons
- Validation errors inline

### Responsive Design
- Mobile-first approach
- Kanban columns stack vertically on mobile
- Hamburger menu on mobile
- Touch-friendly buttons
- Match Figma responsive breakpoints

## Testing Requirements

### Unit Tests (JUnit 5 + Mockito)
- Test all service methods WITH user context
- Test user isolation (user A cannot access user B's data)
- Mock repositories and user authentication
- Test status transition logic
- Test ownership validation
- Coverage target: 80%+

### Integration Tests
- Test repository queries with user filtering
- Use `@DataJpaTest` for repository tests
- Use `@SpringBootTest` for full integration tests
- Test OAuth2 user creation flow
- Test multi-user scenarios

### Controller Tests
- Use `@WebMvcTest` for controller testing
- Mock services and authentication
- Test all endpoints with authenticated user
- Test authorization (accessing other user's data)
- Verify view names and model attributes

### Security Tests
- Test unauthenticated access (should redirect to /login)
- Test cross-user access (should return 403)
- Test OAuth2 flow (mock Google response)

### Test Data
- Create multiple test users
- Create test data for each user
- Use `@BeforeEach` for test setup
- Create builder patterns for test entities

## Configuration (application.properties)

```properties
# Application
spring.application.name=wish-tracker
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:wishtracker
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.sql.init.mode=always

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Google OAuth2 (User must provide their own credentials)
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

# Logging
logging.level.com.wishtracker=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
```

## Development Workflow (Plan → Execute → Review)

### Plan Phase
1. Review requirements thoroughly
2. Design data model with User relationships
3. Plan user-scoped API endpoints
4. Plan OAuth2 integration flow
5. Review Figma design in detail
6. Extract exact colors, spacing, typography from Figma
7. Identify reusable components

### Execute Phase
1. Generate User entity first
2. Generate Category and Wish entities with User relationships
3. Generate repositories with user filtering
4. Generate OAuth2 service for user creation
5. Configure Spring Security
6. Generate services with user context
7. Generate controllers with authentication checks
8. Generate Thymeleaf templates matching Figma design exactly
9. Generate CSS matching Figma colors and spacing
10. Generate tests with multi-user scenarios

### Review Phase
- Test authentication flow
- Test user creation on first login
- Test data isolation between users
- Verify no user can access another's data
- Test each feature after generation
- Run all tests after each change
- Verify UI matches Figma design pixel-by-pixel
- Check validation works
- Test status transitions
- Verify error handling
- Test on multiple browsers
- Test responsive design

## Quality Checklist
- [ ] Google SSO works correctly
- [ ] User is created on first login
- [ ] User info (name, picture) displayed in navbar
- [ ] All wishes are user-scoped
- [ ] All categories are user-scoped
- [ ] Users cannot see other users' data
- [ ] All CRUD operations work for Wishes
- [ ] All CRUD operations work for Categories
- [ ] Status transitions work correctly
- [ ] Category filtering works
- [ ] Cannot delete category with wishes
- [ ] Form validation works (client and server)
- [ ] Error pages display properly
- [ ] UI matches Figma design exactly:
  - [ ] Colors match
  - [ ] Typography matches
  - [ ] Spacing matches
  - [ ] Layout matches
  - [ ] Button styles match
  - [ ] Card designs match
- [ ] Responsive design works on mobile
- [ ] All unit tests pass (80%+ coverage)
- [ ] Integration tests pass
- [ ] Security tests pass
- [ ] H2 console accessible at /h2-console
- [ ] README.md has complete setup instructions (including Google OAuth setup)
- [ ] Application runs with: mvn spring-boot:run

## Performance & Best Practices
- Use DTOs to avoid lazy loading issues
- Use `@Transactional` appropriately
- Implement pagination for wish list if grows large
- Use proper HTTP methods (POST for mutations)
- Add loading indicators for form submissions
- Implement proper error messages for user feedback
- Use Bootstrap components consistently
- Cache user information in session where appropriate
- Optimize database queries with proper indexes
- Use JPA batch operations where beneficial

## Agentic Development Notes
- Break work into small, testable increments
- Generate one layer at a time (entity → repository → service → controller → view)
- Always include User context from the start
- Review and test after each generation
- Use clear, specific prompts
- Reference these rules in each prompt
- Verify generated code before proceeding
- Keep context focused on current task
- Test multi-user scenarios early
- Verify Figma design adherence frequently

## Critical Reminders
1. **EVERY entity must have User relationship**
2. **EVERY repository query must filter by User**
3. **EVERY service method must validate user ownership**
4. **ALWAYS verify ownership before update/delete**
5. **UI must match Figma design exactly - colors, spacing, typography**
6. **OAuth2 is the ONLY authentication method**
7. **User is auto-created on first Google login**
8. **Test data isolation between users thoroughly**