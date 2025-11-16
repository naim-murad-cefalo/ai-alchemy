markdown
# Wish Tracker - Prompt Templates for Claude Code

## IMPORTANT: Reference Rules
In every prompt, add: "Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching."

---

## Phase 1: Project Setup

### Prompt 1.1: Initialize Project
```
Create a new Spring Boot 3.3.x project with Java 21 for Wish Tracker application.

Requirements:
- Use Maven as build tool
- Include dependencies:
  - Spring Web
  - Spring Data JPA
  - H2 Database
  - Thymeleaf
  - Spring Security
  - OAuth2 Client
  - Spring Boot DevTools
  - Validation
  - Lombok
  - JUnit 5
  - Mockito
- Package structure: com.wishtracker
- Create application.properties with H2 and OAuth2 placeholders
- Create README.md with:
  - Project description
  - Google OAuth setup instructions (how to get client ID/secret)
  - Environment variables needed
  - Basic setup instructions

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 2: Domain Model with User

### Prompt 2.1: Create User Entity
```
Create the User entity for Wish Tracker application:

User entity with fields:
- id (Long, auto-generated)
- email (String, unique, required, indexed)
- name (String, required)
- pictureUrl (String, optional)
- createdDate (LocalDateTime, auto with @PrePersist)
- lastLoginDate (LocalDateTime)
- categories (OneToMany with Category, cascade ALL, orphanRemoval true)
- wishes (OneToMany with Wish, cascade ALL, orphanRemoval true)

Include:
- @Entity, @Table with unique constraint on email
- Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- Proper JPA annotations
- toString exclude collections to avoid circular reference

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 2.2: Create Category and Wish Entities
```
Create Category and Wish entities with User relationships:

1. WishStatus enum with values: WISH, IN_PROGRESS, ACHIEVED

2. Category entity with fields:
   - id (Long, auto-generated)
   - name (String, required, max 100)
   - description (String, optional, max 500)
   - color (String, hex color, required, default "#6B7280")
   - user (ManyToOne with User, required, FETCH LAZY)
   - wishes (OneToMany with Wish, cascade ALL, mappedBy category)
   - createdDate (LocalDateTime, auto with @PrePersist)
   - Add @Table with unique constraint on (name, user)
   - Add validation annotations
   - Add Lombok annotations
   - toString exclude wishes and user

3. Wish entity with fields:
   - id (Long, auto-generated)
   - title (String, required, max 200)
   - description (String, optional, max 1000)
   - status (WishStatus enum, default WISH, required)
   - remarks (String, optional, max 500)
   - category (ManyToOne with Category, required, FETCH LAZY)
   - user (ManyToOne with User, required, FETCH LAZY)
   - createdDate (LocalDateTime, auto with @PrePersist)
   - updatedDate (LocalDateTime, auto with @PreUpdate)
   - achievedDate (LocalDateTime, nullable)
   - Add validation annotations
   - Add Lombok annotations
   - toString exclude category and user

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 3: Repositories with User Filtering

### Prompt 3.1: Create Repositories
```
Create Spring Data JPA repositories with USER FILTERING for Wish Tracker:

1. UserRepository extending JpaRepository<User, Long>:
   - findByEmail(String email): Optional<User>
   - existsByEmail(String email): boolean

2. CategoryRepository extending JpaRepository<Category, Long>:
   - findByUser(User user): List<Category>
   - findByIdAndUser(Long id, User user): Optional<Category>
   - findByUserOrderByNameAsc(User user): List<Category>
   - existsByNameAndUser(String name, User user): boolean

3. WishRepository extending JpaRepository<Wish, Long>:
   - findByUser(User user): List<Wish>
   - findByIdAndUser(Long id, User user): Optional<Wish>
   - findByUserOrderByCreatedDateDesc(User user): List<Wish>
   - findByUserAndStatus(User user, WishStatus status): List<Wish>
   - findByUserAndCategory(User user, Category category): List<Wish>
   - findByUserAndCategoryAndStatus(User user, Category category, WishStatus status): List<Wish>
   - countByCategory(Category category): long

IMPORTANT: ALL queries MUST include User parameter for data isolation.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 4: OAuth2 Integration

### Prompt 4.1: Create Custom OAuth2 User Service
```
Create CustomOAuth2UserService for automatic user creation on Google SSO login:

Requirements:
- Extend DefaultOAuth2UserService
- Override loadUser(OAuth2UserRequest userRequest) method
- Extract from OAuth2User attributes:
  - email (required)
  - name (required)
  - picture (optional, profile picture URL)
- Call UserService to findOrCreateUser:
  - If user exists by email: update lastLoginDate
  - If user doesn't exist: create new User with extracted info
- Return OAuth2User with proper authorities
- Handle exceptions appropriately
- Add @Service annotation
- Inject UserService

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 4.2: Create Security Configuration
```
Create SecurityConfig for Google OAuth2 authentication:

Requirements:
- @Configuration and @EnableWebSecurity
- Configure SecurityFilterChain bean with:
  - Permit all: /login, /error, /css/**, /js/**, /images/**
  - Require authentication for all other pages
  - oauth2Login configuration:
    - Default success URL: /wishes
    - Login page: /login
    - Custom userInfoEndpoint with CustomOAuth2UserService
  - logout configuration:
    - Logout URL: /logout
    - Success URL: /login?logout
    - Invalidate session
    - Clear authentication
- Disable CSRF for H2 console
- Allow H2 console in frames

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 5: Services with User Context

### Prompt 5.1: Create UserService
```
Create UserService for user management:

Methods:
- findByEmail(String email): Optional<User>
- findOrCreateUser(String email, String name, String pictureUrl): User
  - Check if user exists by email
  - If exists: update lastLoginDate and return
  - If not: create new User and return
- getCurrentUser(): User
  - Get email from SecurityContextHolder
  - Find user by email
  - Throw exception if not found
- updateLastLogin(String email): void

Include:
- @Service annotation
- @Transactional where needed
- Inject UserRepository
- Proper exception handling (UserNotFoundException)
- Logging for user creation

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 5.2: Create CategoryService with User Context
```
Create CategoryService with USER CONTEXT:

Methods (ALL must use User parameter):
- findAllByUser(User user): List<CategoryDTO>
- findByIdAndUser(Long id, User user): CategoryDTO
  - Throw exception if not found or doesn't belong to user
- create(CategoryDTO dto, User user): CategoryDTO
  - Validate name uniqueness for this user
  - Set user on entity
  - Create category
- update(Long id, CategoryDTO dto, User user): CategoryDTO
  - Verify ownership (id and user match)
  - Validate name uniqueness for this user (excluding current)
  - Update
- delete(Long id, User user): void
  - Verify ownership
  - Check if category has wishes (prevent deletion)
  - Delete

Include:
- @Service annotation
- @Transactional where needed
- Inject CategoryRepository and WishRepository
- Custom exceptions: CategoryNotFoundException, CategoryHasWishesException, UnauthorizedAccessException
- Convert between Entity and DTO
- ALWAYS verify user ownership

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 5.3: Create WishService with User Context
```
Create WishService with USER CONTEXT:

Methods (ALL must use User parameter):
- findAllByUser(User user): List<WishDTO>
  - Order by createdDate DESC
- findByIdAndUser(Long id, User user): WishDTO
  - Throw exception if not found or doesn't belong to user
- findByUserAndStatus(User user, WishStatus status): List<WishDTO>
- findByUserAndCategory(User user, Long categoryId): List<WishDTO>
  - Verify category belongs to user
- create(WishDTO dto, User user): WishDTO
  - Verify category belongs to user
  - Set user on entity
  - Create wish
- update(Long id, WishDTO dto, User user): WishDTO
  - Verify ownership (wish and category belong to user)
  - Update
- delete(Long id, User user): void
  - Verify ownership
  - Delete
- changeStatus(Long id, WishStatus newStatus, User user): WishDTO
  - Verify ownership
  - Validate status transition:
    - WISH → IN_PROGRESS only
    - IN_PROGRESS → ACHIEVED only
    - Cannot go backwards
  - Set achievedDate when status becomes ACHIEVED
  - Update

Include:
- @Service annotation
- @Transactional where needed
- Inject WishRepository and CategoryRepository
- Custom exceptions: WishNotFoundException, InvalidStatusTransitionException, UnauthorizedAccessException
- Convert between Entity and DTO
- ALWAYS verify user ownership
- Status transition validation logic

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 6: DTOs

### Prompt 6.1: Create DTOs
```
Create Data Transfer Objects:

1. CategoryDTO with fields:
   - id (Long)
   - name (String, @NotBlank, @Size max 100)
   - description (String, @Size max 500)
   - color (String, @NotBlank, hex color format)
   - wishCount (int, for display)
   - Add conversion methods:
     - static fromEntity(Category entity): CategoryDTO
     - toEntity(User user): Category

2. WishDTO with fields:
   - id (Long)
   - title (String, @NotBlank, @Size max 200)
   - description (String, @Size max 1000)
   - status (WishStatus, @NotNull)
   - remarks (String, @Size max 500)
   - categoryId (Long, @NotNull)
   - categoryName (String, for display)
   - categoryColor (String, for display)
   - createdDate (LocalDateTime)
   - updatedDate (LocalDateTime)
   - achievedDate (LocalDateTime)
   - Add conversion methods:
     - static fromEntity(Wish entity): WishDTO
     - toEntity(User user, Category category): Wish

Include proper validation annotations.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 7: Controllers with Authentication

### Prompt 7.1: Create AuthController
```
Create AuthController for login handling:

Endpoints:
- GET /login - show login page with Google sign-in button
- GET / - redirect to /wishes if authenticated, else to /login

Include:
- @Controller annotation
- Check authentication status
- Redirect appropriately

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 7.2: Create CategoryController with User Context
```
Create CategoryController with USER AUTHENTICATION:

Endpoints:
- GET /categories - list current user's categories
- GET /categories/new - show create form
- POST /categories - create for current user
- GET /categories/{id}/edit - edit form (verify ownership)
- POST /categories/{id} - update (verify ownership)
- POST /categories/{id}/delete - delete (verify ownership)

Include:
- @Controller annotation
- Inject CategoryService and UserService
- Get current user with userService.getCurrentUser() in EVERY method
- Pass user to service layer
- Use Model to pass data to views
- Pass current user info to all views (for navbar)
- Use RedirectAttributes for flash messages
- Handle validation errors
- Handle exceptions (not found, unauthorized, has wishes)
- Return 403 if user tries to access another user's category

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 7.3: Create WishController with User Context
```
Create WishController with USER AUTHENTICATION:

Endpoints:
- GET /wishes - list current user's wishes (Kanban board)
- GET /wishes/filter?categoryId={id} - filter by user's category
- GET /wishes/new - show create form (populate with user's categories)
- POST /wishes - create for current user
- GET /wishes/{id} - view details (verify ownership)
- GET /wishes/{id}/edit - edit form (verify ownership, populate user's categories)
- POST /wishes/{id} - update (verify ownership)
- POST /wishes/{id}/delete - delete (verify ownership)
- POST /wishes/{id}/status - change status (verify ownership)

Include:
- @Controller annotation
- Inject WishService, CategoryService, and UserService
- Get current user with userService.getCurrentUser() in EVERY method
- Pass user to service layer
- Use Model to pass data to views
- Pass current user info and categories to all views
- Use RedirectAttributes for flash messages
- Handle validation errors
- Handle exceptions (not found, unauthorized, invalid transition)
- Return 403 if user tries to access another user's wish
- For Kanban board: group wishes by status and pass to view

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 8: Thymeleaf Templates with Figma Design

### Prompt 8.1: Analyze Figma Design
```
Access the Figma design at:
https://www.figma.com/proto/ZULiad73cd7g7FvpvBuTwE/Sensa-UI-UX?page-id=9130%3A3020&node-id=9136-3826

Extract and document:
1. Color palette (exact hex codes):
   - Primary color
   - Secondary color
   - Background colors
   - Text colors
   - Status colors (WISH, IN_PROGRESS, ACHIEVED)
   - Border colors
   - Button colors

2. Typography:
   - Font family
   - Heading sizes and weights
   - Body text size
   - Button text size

3. Spacing:
   - Container padding
   - Card padding and margins
   - Grid gaps
   - Element spacing

4. Component styles:
   - Button styles (primary, secondary)
   - Card styles
   - Form input styles
   - Badge/tag styles
   - Navigation bar style

5. Layout:
   - Container widths
   - Kanban column widths
   - Responsive breakpoints

Create a design-tokens.css or variables section with all extracted values.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.2: Create Base Layout
```
Create Thymeleaf base layout (templates/layout.html) matching Figma design EXACTLY:

Requirements:
- Use Bootstrap 5 CDN
- Include custom CSS with Figma design tokens
- Navigation bar (match Figma exactly):
  - Left: Brand logo/name
  - Center/Left: Links (Home, Wishes, Categories)
  - Right: 
    - User profile picture (circular, 40px)
    - User name
    - Logout button/link
  - Use exact colors, spacing, typography from Figma
- Flash message area (success/error alerts styled per Figma)
- Main content block (th:block="content")
- Footer (match Figma if present)
- Include meta tags for responsive design
- Link to custom CSS: /css/style.css
- Optional JS: /js/app.js

Thymeleaf variables to expect:
- ${currentUser} - User object with name and pictureUrl
- ${successMessage} - success flash message
- ${errorMessage} - error flash message

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.3: Create Login Page
```
Create Thymeleaf login page (templates/login.html) matching Figma design:

Requirements:
- Clean, centered layout
- App logo/name at top
- Tagline or description
- "Sign in with Google" button:
  - Style exactly as per Figma (color, size, spacing, icon)
  - Link: /oauth2/authorization/google
  - Include Google icon
- Simple footer
- No username/password fields (OAuth only)
- Show logout message if present
- Responsive design

Match Figma design exactly for colors, typography, spacing, button style.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.4: Create Kanban Board Wish List
```
Create Thymeleaf Kanban board for wishes (templates/wishes/list.html) matching Figma design PIXEL-PERFECT:

Requirements:
- Extend layout.html
- Page title: "My Wishes" (or as per Figma)
- Top section:
  - Filter by category (dropdown, styled per Figma)
  - "Add New Wish" button (prominent, styled per Figma)
- Three-column Kanban board layout:
  - Column headers with count badges:
    - "Wish (X)"
    - "In Progress (X)"
    - "Achieved (X)"
  - Equal width columns with gap
  - Scrollable columns if content overflows

Each Wish Card (match Figma exactly):
- Card container with:
  - Background color based on status (from Figma)
  - Border, shadow (from Figma)
  - Padding (from Figma)
  - Hover effect (from Figma)
- Card content:
  - Title (bold, primary text color, font size from Figma)
  - Category badge (rounded, with category color background, category name)
  - Description preview (truncate to 100 chars, secondary text color)
  - Remarks (if present, italic or distinct style from Figma)
  - Created date (small, secondary text, format: "MMM dd, yyyy")
  - Action buttons (styled per Figma):
    - View (icon)
    - Edit (icon)
    - Delete (icon, with confirmation modal)
    - Status transition button:
      - WISH: "Start" or arrow right icon
      - IN_PROGRESS: "Complete" or checkmark icon
      - ACHIEVED: no button

Empty state:
- Show message if column is empty
- Style per Figma

Thymeleaf logic:
- Use th:each to iterate wishes grouped by status
- Use th:if for conditional rendering
- Use th:style for dynamic category colors
- Form with hidden input for status change (POST /wishes/{id}/status)
- Delete confirmation modal

Use exact colors, spacing, typography, card design from Figma.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.5: Create Wish Form
```
Create Thymeleaf wish form (templates/wishes/form.html) matching Figma design:

Requirements:
- Extend layout.html
- Page title: "New Wish" or "Edit Wish"
- Centered form container (max-width from Figma)
- Form fields (styled per Figma):
  - Title* (text input, required)
  - Description (textarea, auto-resize, optional)
  - Category* (select dropdown, required, show category name with color indicator)
  - Status* (radio buttons or segmented control per Figma)
  - Remarks (textarea, optional)
- Show validation errors inline (red text, per Figma error style)
- Buttons (styled per Figma):
  - Cancel (secondary, link to /wishes)
  - Save (primary)
- Use th:object for form binding (${wish})
- Use th:field for inputs
- Use th:errors for validation messages
- Pre-populate for edit mode

Match Figma form styling exactly: input styles, spacing, button styles, error styling.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.6: Create Wish Details
```
Create Thymeleaf wish details page (templates/wishes/view.html) matching Figma design:

Requirements:
- Extend layout.html
- Large card container (styled per Figma)
- Display:
  - Title (large, bold)
  - Status badge (prominent, colored per Figma)
  - Category badge (with category color)
  - Description (full text, proper line breaks)
  - Remarks (if present, distinct section)
  - Created date
  - Updated date (if different from created)
  - Achieved date (if status is ACHIEVED)
- Action buttons (styled per Figma):
  - Edit
  - Delete (with confirmation)
  - Back to List
- Responsive layout

Match Figma detail view design.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.7: Create Category List
```
Create Thymeleaf category list (templates/categories/list.html) matching Figma design:

Requirements:
- Extend layout.html
- Page title: "My Categories"
- "Add Category" button (styled per Figma)
- Grid or list view (per Figma):
  - Each category card shows:
    - Color preview (circle or square, large, with category color)
    - Name (bold)
    - Description
    - Wish count (e.g., "5 wishes")
    - Actions: Edit, Delete (icon buttons per Figma)
- Delete with confirmation (show error if category has wishes)
- Empty state message if no categories
- Responsive grid

Use exact Figma design for category cards.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 8.8: Create Category Form
```
Create Thymeleaf category form (templates/categories/form.html) matching Figma design:

Requirements:
- Extend layout.html
- Page title: "New Category" or "Edit Category"
- Centered form (max-width from Figma)
- Form fields (styled per Figma):
  - Name* (text input, required, max 100)
  - Description (textarea, optional, max 500)
  - Color* (color picker input, required, default from Figma palette)
    - Show color preview swatch
    - Suggest Figma palette colors
- Show validation errors inline
- Buttons: Cancel, Save (styled per Figma)
- Use th:object, th:field, th:errors
- Pre-populate for edit mode

Match Figma form styling.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 9: Custom CSS

### Prompt 9.1: Create Custom CSS
```
Create comprehensive custom CSS (static/css/style.css) matching Figma design EXACTLY:

Include:
1. CSS Variables (extract from Figma):
   - --primary-color
   - --secondary-color
   - --bg-color
   - --card-bg
   - --text-primary
   - --text-secondary
   - --status-wish-bg
   - --status-inprogress-bg
   - --status-achieved-bg
   - --border-color
   - --success-color
   - --error-color
   - Font variables
   - Spacing variables
   - Border radius variables

2. Global Styles:
   - Body font, colors, background
   - Headings
   - Links
   - Containers

3. Navigation Bar:
   - Exact styling from Figma
   - User profile section
   - Logo/brand styling

4. Kanban Board:
   - Column layout (flexbox or grid)
   - Column headers with count badges
   - Card styling for each status
   - Hover effects
   - Drag-drop visual feedback (if applicable)

5. Wish Cards:
   - Base card style
   - Status-specific backgrounds/borders
   - Category badge styling
   - Action button styling
   - Responsive card width

6. Forms:
   - Input field styling
   - Textarea styling
   - Select dropdown styling
   - Color picker styling
   - Button styling (primary, secondary)
   - Validation error styling
   - Focus states

7. Buttons:
   - Primary button (from Figma)
   - Secondary button
   - Icon buttons
   - Hover, active, disabled states

8. Badges/Tags:
   - Status badges
   - Category badges (dynamic colors)
   - Count badges

9. Modals/Alerts:
   - Confirmation modal
   - Success/error alerts
   - Flash messages

10. Responsive Design:
    - Breakpoints from Figma
    - Mobile navigation (hamburger if needed)
    - Stacked Kanban columns on mobile
    - Touch-friendly button sizes

11. Animations:
    - Smooth transitions
    - Hover effects
    - Loading states

12. Utility Classes:
    - Spacing utilities
    - Text utilities
    - Display utilities

Match every detail from Figma: colors, fonts, spacing, shadows, borders, hover effects.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 10: Sample Data

### Prompt 10.1: Create Sample Data
```
Create data.sql with sample data for MULTIPLE USERS:

Include:
1. Create 2-3 users:
   - User 1: email "user1@example.com", name "John Doe"
   - User 2: email "user2@example.com", name "Jane Smith"

2. For EACH user, create:
   - 5 categories with different colors:
     - Travel (#3B82F6)
     - Books (#8B5CF6)
     - Fitness (#10B981)
     - Tech (#F59E0B)
     - Personal (#EC4899)

3. For EACH user, create 10-15 wishes distributed across:
   - 5 in WISH status
   - 5 in IN_PROGRESS status
   - 5 in ACHIEVED status (with achievedDate)
   - Distribute across different categories
   - Include realistic titles, descriptions, and remarks

4. Use proper H2 SQL syntax:
   - INSERT statements
   - Proper date formats
   - Foreign key references

IMPORTANT: Ensure data isolation - each user has separate categories and wishes.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 11: Testing

### Prompt 11.1: Create UserService Tests
```
Create unit tests for UserService:

Tests:
- testFindByEmail_Found
- testFindByEmail_NotFound
- testFindOrCreateUser_ExistingUser_UpdatesLastLogin
- testFindOrCreateUser_NewUser_CreatesUser
- testGetCurrentUser_Authenticated_ReturnsUser
- testGetCurrentUser_NotAuthenticated_ThrowsException
- testUpdateLastLogin_UpdatesTimestamp

Use JUnit 5, Mockito
Mock UserRepository and SecurityContextHolder
Assert user properties and timestamps

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 11.2: Create CategoryService Tests
```
Create unit tests for CategoryService with USER CONTEXT:

Tests:
- testFindAllByUser_ReturnsUserCategories
- testFindByIdAndUser_ValidOwnership_ReturnsCategory
- testFindByIdAndUser_InvalidOwnership_ThrowsException
- testCreate_ValidData_CreatesCategory
- testCreate_DuplicateName_ThrowsException
- testUpdate_ValidOwnership_UpdatesCategory
- testUpdate_InvalidOwnership_ThrowsException
- testDelete_NoWishes_DeletesCategory
- testDelete_HasWishes_ThrowsException
- testDelete_InvalidOwnership_ThrowsException

Use JUnit 5, Mockito
Mock CategoryRepository, WishRepository
Create test users and verify user isolation
Coverage: 80%+

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 11.3: Create WishService Tests
```
Create unit tests for WishService with USER CONTEXT:

Tests:
- testFindAllByUser_ReturnsUserWishes
- testFindByIdAndUser_ValidOwnership_ReturnsWish
- testFindByIdAndUser_InvalidOwnership_ThrowsException
- testCreate_ValidCategory_CreatesWish
- testCreate_CategoryNotOwnedByUser_ThrowsException
- testUpdate_ValidOwnership_UpdatesWish
- testUpdate_InvalidOwnership_ThrowsException
- testUpdate_CategoryNotOwnedByUser_ThrowsException
- testDelete_ValidOwnership_DeletesWish
- testDelete_InvalidOwnership_ThrowsException
- testChangeStatus_WishToInProgress_Success
- testChangeStatus_InProgressToAchieved_SetsAchievedDate
- testChangeStatus_InvalidTransition_ThrowsException
- testChangeStatus_InvalidOwnership_ThrowsException

Use JUnit 5, Mockito
Mock WishRepository, CategoryRepository
Create test users and verify user isolation
Test status transitions thoroughly
Coverage: 80%+

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 11.4: Create Repository Tests
```
Create integration tests for repositories with @DataJpaTest:

For UserRepository:
- testSaveAndFindByEmail
- testFindByEmail_NotFound
- testExistsByEmail

For CategoryRepository:
- testSaveAndFindByUser
- testFindByIdAndUser_ValidUser
- testFindByIdAndUser_InvalidUser_ReturnsEmpty
- testFindByUserOrderByNameAsc
- testExistsByNameAndUser

For WishRepository:
- testSaveAndFindByUser
- testFindByIdAndUser_ValidUser
- testFindByIdAndUser_InvalidUser_ReturnsEmpty
- testFindByUserOrderByCreatedDateDesc
- testFindByUserAndStatus
- testFindByUserAndCategory
- testCountByCategory

Use JUnit 5, @DataJpaTest, H2 database
Create multiple test users
Verify data isolation between users

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 11.5: Create Controller Tests
```
Create controller tests with @WebMvcTest:

For CategoryController:
- testListCategories_Authenticated_ReturnsView
- testListCategories_Unauthenticated_RedirectsToLogin
- testCreateCategory_ValidData_RedirectsToList
- testCreateCategory_InvalidData_ReturnsFormWithErrors
- testDeleteCategory_ValidOwnership_Success
- testDeleteCategory_InvalidOwnership_Returns403
- testDeleteCategory_HasWishes_ReturnsError

For WishController:
- testListWishes_Authenticated_ReturnsKanbanView
- testListWishes_Unauthenticated_RedirectsToLogin
- testCreateWish_ValidData_RedirectsToList
- testCreateWish_InvalidData_ReturnsFormWithErrors
- testViewWish_ValidOwnership_ReturnsView
- testViewWish_InvalidOwnership_Returns403
- testChangeStatus_ValidTransition_Success
- testChangeStatus_InvalidTransition_ReturnsError
- testChangeStatus_InvalidOwnership_Returns403
- testDeleteWish_ValidOwnership_Success
- testDeleteWish_InvalidOwnership_Returns403

Use MockMvc, mock services and authentication
Mock SecurityContextHolder to return test user
Verify view names, model attributes, redirect URLs
Test authorization (403 for invalid ownership)

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 11.6: Create Security Tests
```
Create security integration tests with @SpringBootTest:

Tests:
- testUnauthenticatedAccess_RedirectsToLogin
- testAuthenticatedAccess_AllowsWishList
- testCrossUserAccess_Category_Returns403
- testCrossUserAccess_Wish_Returns403
- testOAuth2UserCreation_CreatesNewUser
- testOAuth2UserLogin_UpdatesLastLogin
- testLogout_ClearsAuthentication

Use @SpringBootTest, @AutoConfigureMockMvc
Mock OAuth2 authentication
Create multiple test users
Verify data isolation and authorization

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Phase 12: Final Touches

### Prompt 12.1: Create Comprehensive README
```
Create detailed README.md for Wish Tracker:

Include:
1. Project Overview:
   - Description
   - Features list
   - Agentic development approach used

2. Technology Stack:
   - Java 21
   - Spring Boot 3.3.x
   - Thymeleaf
   - H2 Database
   - Spring Security OAuth2
   - Bootstrap 5
   - JUnit 5

3. Prerequisites:
   - Java 21 installed
   - Maven installed
   - Google OAuth2 credentials

4. Google OAuth Setup (DETAILED):
   - Go to Google Cloud Console
   - Create new project
   - Enable Google+ API
   - Create OAuth2 credentials
   - Add authorized redirect URIs: http://localhost:8080/login/oauth2/code/google
   - Copy Client ID and Client Secret

5. Setup Instructions:
   ```
   # Clone repository
   git clone <repo-url>
   cd wish-tracker

   # Set environment variables
   export GOOGLE_CLIENT_ID=your-client-id
   export GOOGLE_CLIENT_SECRET=your-client-secret

   # Run application
   mvn spring-boot:run

   # Access application
   Open browser: http://localhost:8080

   # H2 Console (for debugging)
   http://localhost:8080/h2-console
   JDBC URL: jdbc:h2:mem:wishtracker
   Username: sa
   Password: (leave blank)
   ```

6. Testing:
   ```
   # Run all tests
   mvn test

   # Run with coverage
   mvn test jacoco:report
   ```

7. Features:
   - Google SSO authentication
   - User-scoped data (complete isolation)
   - Category management with colors
   - Wish tracking with status (Wish → In Progress → Achieved)
   - Kanban board visualization
   - Responsive design matching Figma

8. Project Structure:
   - Brief overview of packages

9. Design:
   - Link to Figma design
   - Note about design adherence

10. Agentic Development:
    - Brief description of Plan → Execute → Review cycles used
    - Tools used (Claude Code)
    - Approach to prompting and iteration

11. Future Enhancements:
    - Drag-and-drop Kanban
    - Due dates and reminders
    - Wish sharing
    - Data export

12. License and Credits

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Prompt 12.2: Final Code Review and Checklist
```
Perform comprehensive review of Wish Tracker application:

1. Security Review:
   - [ ] All endpoints require authentication except /login
   - [ ] User data is isolated (no cross-user access)
   - [ ] All repositories filter by user
   - [ ] All services verify user ownership
   - [ ] OAuth2 configuration is correct
   - [ ] User creation on first login works

2. Functionality Review:
   - [ ] User can sign in with Google
   - [ ] User profile shows in navbar
   - [ ] Categories: Create, Read, Update, Delete (CRUD)
   - [ ] Category uniqueness per user
   - [ ] Cannot delete category with wishes
   - [ ] Wishes: Create, Read, Update, Delete (CRUD)
   - [ ] Status transitions work correctly
   - [ ] AchievedDate set on completion
   - [ ] Category filtering works
   - [ ] Flash messages display

3. UI/UX Review:
   - [ ] Matches Figma design (colors, fonts, spacing)
   - [ ] Kanban board displays correctly
   - [ ] Cards show all required information
   - [ ] Forms have proper validation
   - [ ] Responsive on mobile
   - [ ] Buttons styled correctly
   - [ ] User profile in navbar
   - [ ] Empty states display properly

4. Code Quality:
   - [ ] No code duplication
   - [ ] Proper exception handling
   - [ ] Consistent naming conventions
   - [ ] Proper use of Spring annotations
   - [ ] DTOs used for data transfer
   - [ ] Entities have proper relationships
   - [ ] Validation annotations present

5. Testing:
   - [ ] All tests pass
   - [ ] Coverage > 80%
   - [ ] User isolation tested
   - [ ] Security tests pass
   - [ ] Repository tests pass
   - [ ] Service tests pass
   - [ ] Controller tests pass

6. Documentation:
   - [ ] README is complete
   - [ ] Setup instructions work
   - [ ] Google OAuth setup documented
   - [ ] Code comments where needed

7. Configuration:
   - [ ] application.properties correct
   - [ ] H2 console accessible
   - [ ] OAuth2 properties configured
   - [ ] Logging configured

Provide detailed report of any issues found with specific fixes needed.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

---

## Emergency Prompts

### Debug Authentication Issues
```
I'm having authentication issues in Wish Tracker:

Error: [paste error]

Context:
- OAuth2 flow status: [describe]
- User creation: [working/not working]
- Current user retrieval: [working/not working]

Help debug and fix.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Debug User Isolation Issues
```
User data isolation not working properly:

Issue: [describe - e.g., "User A can see User B's wishes"]

Current behavior: [describe]
Expected behavior: [describe]

Review security implementation and fix data isolation.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### UI Design Mismatch
```
UI doesn't match Figma design:

Component: [e.g., Kanban board, wish card]
Current state: [describe or show code]
Figma design: [describe what it should look like]

Review Figma design again and adjust CSS/HTML to match exactly.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.
```

### Add Missing Feature
```
Add this feature to Wish Tracker:

Feature: [describe]
Requirements: [list]
User context: [how it relates to users]

Update all necessary layers: entity, repository, service, controller, view, tests.

Follow all rules in wish-tracker-rules.md strictly, especially user-scoped data access and Figma design matching.