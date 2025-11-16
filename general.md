

```markdown
# Wish Tracker - Prompt Templates for Claude Code

## IMPORTANT: Reference Rules
In every prompt, add: "Follow all rules in wish-tracker-rules.md strictly"

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
  - Spring Boot DevTools
  - Validation
  - Lombok
  - JUnit 5
  - Mockito
- Package structure: com.wishtracker
- Create application.properties with H2 configuration
- Create README.md with project description and setup instructions

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 2: Domain Model

### Prompt 2.1: Create Entities
```
Create the domain model for Wish Tracker application:

1. WishStatus enum with values: WISH, IN_PROGRESS, ACHIEVED

2. Category entity with fields:
   - id (Long, auto-generated)
   - name (String, required, unique, max 100)
   - description (String, optional, max 500)
   - color (String, hex color)
   - wishes (OneToMany with Wish, cascade all)
   - Add validation annotations
   - Add Lombok annotations

3. Wish entity with fields:
   - id (Long, auto-generated)
   - title (String, required, max 200)
   - description (String, optional, max 1000)
   - status (WishStatus enum, default WISH)
   - remarks (String, optional, max 500)
   - category (ManyToOne with Category, required)
   - createdDate (LocalDateTime, auto)
   - updatedDate (LocalDateTime, auto)
   - achievedDate (LocalDateTime, nullable)
   - Add @PrePersist and @PreUpdate for timestamps
   - Add validation annotations
   - Add Lombok annotations

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 3: Repositories

### Prompt 3.1: Create Repositories
```
Create Spring Data JPA repositories for Wish Tracker:

1. CategoryRepository extending JpaRepository<Category, Long>
   - Custom query: findByNameIgnoreCase
   - Custom query: existsByNameIgnoreCase

2. WishRepository extending JpaRepository<Wish, Long>
   - Custom query: findByStatus
   - Custom query: findByCategory
   - Custom query: findByCategoryAndStatus
   - Custom query: findAllByOrderByCreatedDateDesc

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 4: DTOs

### Prompt 4.1: Create DTOs
```
Create Data Transfer Objects for Wish Tracker:

1. CategoryDTO with fields matching Category entity
   - Add validation annotations
   - Add conversion methods: toEntity() and fromEntity()

2. WishDTO with fields matching Wish entity
   - Include categoryId (Long) instead of full Category object
   - Include categoryName for display
   - Add validation annotations
   - Add conversion methods: toEntity() and fromEntity()

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 5: Services

### Prompt 5.1: Create CategoryService
```
Create CategoryService with business logic:

Methods:
- findAll() - return all categories
- findById(Long id) - return category or throw exception
- create(CategoryDTO dto) - validate uniqueness, create category
- update(Long id, CategoryDTO dto) - validate exists, update
- delete(Long id) - check if has wishes, delete or throw exception
- existsByName(String name) - check name existence

Include:
- @Service annotation
- @Transactional where needed
- Proper exception handling
- Validation logic
- Convert between Entity and DTO

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 5.2: Create WishService
```
Create WishService with business logic:

Methods:
- findAll() - return all wishes ordered by created date
- findById(Long id) - return wish or throw exception
- findByStatus(WishStatus status) - filter by status
- findByCategory(Long categoryId) - filter by category
- create(WishDTO dto) - validate category exists, create wish
- update(Long id, WishDTO dto) - validate exists, update
- delete(Long id) - delete wish
- changeStatus(Long id, WishStatus newStatus) - validate transition, update status
  - WISH can move to IN_PROGRESS only
  - IN_PROGRESS can move to ACHIEVED only
  - Set achievedDate when status becomes ACHIEVED
- validateStatusTransition(WishStatus current, WishStatus next) - business rule

Include:
- @Service annotation
- @Transactional where needed
- Status transition validation
- Auto-set achievedDate
- Convert between Entity and DTO

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 6: Controllers

### Prompt 6.1: Create CategoryController
```
Create CategoryController for category management:

Endpoints:
- GET /categories - list all categories
- GET /categories/new - show create form
- POST /categories - create new category
- GET /categories/{id}/edit - show edit form
- POST /categories/{id} - update category
- POST /categories/{id}/delete - delete category

Include:
- @Controller annotation
- Inject CategoryService
- Use Model to pass data to views
- Use RedirectAttributes for flash messages
- Handle validation errors
- Handle exceptions (category not found, has wishes, etc.)
- Return appropriate view names

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 6.2: Create WishController
```
Create WishController for wish management:

Endpoints:
- GET / or /wishes - list all wishes (Kanban board view)
- GET /wishes/filter - filter by category
- GET /wishes/new - show create form
- POST /wishes - create new wish
- GET /wishes/{id} - view wish details
- GET /wishes/{id}/edit - show edit form
- POST /wishes/{id} - update wish
- POST /wishes/{id}/delete - delete wish
- POST /wishes/{id}/status - change status (with transition validation)

Include:
- @Controller annotation
- Inject WishService and CategoryService
- Use Model to pass data to views
- Pass all categories to forms
- Use RedirectAttributes for flash messages
- Handle validation errors
- Handle exceptions (wish not found, invalid status transition)
- Return appropriate view names

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 7: Thymeleaf Templates

### Prompt 7.1: Create Base Layout
```
Create Thymeleaf base layout template (templates/layout.html):

Requirements:
- Use Bootstrap 5 CDN
- Include navigation bar with:
  - Brand: "Wish Tracker"
  - Links: Home, Wishes, Categories
- Include flash message area (success/error alerts)
- Include main content block (th:block="content")
- Include footer
- Add custom CSS link
- Responsive design

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 7.2: Create Wish List Template
```
Create Thymeleaf template for wish list (templates/wishes/list.html):

Requirements:
- Extend layout.html
- Kanban board style with 3 columns: WISH | IN_PROGRESS | ACHIEVED
- Use Bootstrap cards for each wish showing:
  - Title (bold)
  - Category badge with color
  - Description (truncated to 100 chars)
  - Remarks if present
  - Created date
  - Action buttons: View, Edit, Delete, Change Status
- Filter dropdown by category (with "All" option)
- "Add New Wish" button (prominent, top right)
- Empty state message if no wishes
- Use Thymeleaf th:each for iteration
- Use th:if for conditional rendering
- Status transition buttons based on current status:
  - WISH: show "Start Progress" button
  - IN_PROGRESS: show "Mark Achieved" button
  - ACHIEVED: no status button
- Confirmation dialog for delete actions

Follow all rules in wish-tracker-rules.md and match Figma design.
```

### Prompt 7.3: Create Wish Form Template
```
Create Thymeleaf template for wish form (templates/wishes/form.html):

Requirements:
- Extend layout.html
- Form fields:
  - Title (text input, required, maxlength 200)
  - Description (textarea, optional, maxlength 1000)
  - Category (select dropdown, required, populated from model)
  - Status (radio buttons or select, default WISH)
  - Remarks (textarea, optional, maxlength 500)
- Use th:object for form binding
- Display validation errors with th:errors
- Cancel button (link back to list)
- Save button
- Pre-populate fields for edit mode (th:value or th:field)
- Use Bootstrap form styling

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 7.4: Create Wish Details Template
```
Create Thymeleaf template for wish details (templates/wishes/view.html):

Requirements:
- Extend layout.html
- Display all wish information:
  - Title (large heading)
  - Status badge (colored)
  - Category badge (with color)
  - Description (full text)
  - Remarks (if present)
  - Created date
  - Achieved date (if status is ACHIEVED)
- Action buttons: Edit, Delete, Back to List
- Use Bootstrap card layout
- Responsive design

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 7.5: Create Category Templates
```
Create Thymeleaf templates for category management:

1. templates/categories/list.html:
   - Table or card view
   - Columns: Name, Description, Color Preview, Wish Count, Actions
   - Actions: Edit, Delete
   - "Add Category" button
   - Delete confirmation
   - Show error if category has wishes

2. templates/categories/form.html:
   - Form fields: Name, Description, Color (color picker)
   - Validation errors display
   - Cancel and Save buttons
   - Pre-populate for edit mode

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 8: Styling

### Prompt 8.1: Create Custom CSS
```
Create custom CSS file (static/css/style.css) for Wish Tracker:

Requirements:
- Status color coding:
  - WISH: light gray background
  - IN_PROGRESS: light blue background
  - ACHIEVED: light green background
- Category color badges
- Kanban board column styling
- Card hover effects
- Smooth transitions
- Responsive breakpoints
- Button styling consistency
- Form styling enhancements
- Match Figma design colors and spacing

Follow all rules in wish-tracker-rules.md and match Figma design.
```

---

## Phase 9: Sample Data

### Prompt 9.1: Create Sample Data
```
Create data.sql file with sample data for Wish Tracker:

Include:
- 5 categories with different colors (Travel, Books, Fitness, Tech, Personal)
- 10-15 wishes across different statuses:
  - 5 in WISH status
  - 5 in IN_PROGRESS status
  - 5 in ACHIEVED status (with achievedDate)
- Distribute wishes across categories
- Include realistic titles, descriptions, and remarks
- Use proper SQL INSERT statements for H2 database

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 10: Testing

### Prompt 10.1: Create Service Tests
```
Create comprehensive unit tests for CategoryService and WishService:

For CategoryService:
- Test findAll returns all categories
- Test findById with valid and invalid ID
- Test create with valid data
- Test create with duplicate name (should fail)
- Test update with valid data
- Test delete with no wishes
- Test delete with wishes (should fail)

For WishService:
- Test findAll returns wishes ordered by date
- Test findById with valid and invalid ID
- Test findByStatus filters correctly
- Test create with valid category
- Test create with invalid category (should fail)
- Test update with valid data
- Test delete
- Test changeStatus with valid transitions
- Test changeStatus with invalid transitions (should fail)
- Test achievedDate is set when status becomes ACHIEVED

Use JUnit 5, Mockito, and AssertJ
Mock repositories
Coverage target: 80%+

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 10.2: Create Repository Tests
```
Create integration tests for repositories using @DataJpaTest:

For CategoryRepository:
- Test save and findById
- Test findByNameIgnoreCase
- Test existsByNameIgnoreCase
- Test delete

For WishRepository:
- Test save and findById
- Test findByStatus
- Test findByCategory
- Test findByCategoryAndStatus
- Test findAllByOrderByCreatedDateDesc
- Test delete

Use JUnit 5 and H2 in-memory database

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 10.3: Create Controller Tests
```
Create controller tests using @WebMvcTest:

For CategoryController:
- Test GET /categories returns list view
- Test GET /categories/new returns form
- Test POST /categories with valid data redirects
- Test POST /categories with invalid data returns form with errors
- Test POST /categories/{id}/delete succeeds
- Test POST /categories/{id}/delete fails if has wishes

For WishController:
- Test GET /wishes returns list view with all statuses
- Test GET /wishes/new returns form with categories
- Test POST /wishes with valid data redirects
- Test POST /wishes with invalid data returns form with errors
- Test POST /wishes/{id}/status with valid transition succeeds
- Test POST /wishes/{id}/status with invalid transition fails
- Test POST /wishes/{id}/delete succeeds

Mock services
Use MockMvc
Verify view names and model attributes

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 11: Google SSO (Brownie Points)

### Prompt 11.1: Implement Google SSO
```
Add Google OAuth2 authentication to Wish Tracker:

Requirements:
1. Add spring-boot-starter-oauth2-client dependency to pom.xml

2. Configure SecurityConfig:
   - Enable oauth2Login
   - Require authentication for all pages except /login
   - Configure logout

3. Add OAuth2 properties to application.properties:
   - spring.security.oauth2.client.registration.google.client-id
   - spring.security.oauth2.client.registration.google.client-secret
   - spring.security.oauth2.client.registration.google.scope

4. Create login.html template with "Sign in with Google" button

5. Update layout.html navbar to show:
   - Authenticated user name
   - Logout button

6. Add instructions in README.md for setting up Google OAuth credentials

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Phase 12: Final Polish

### Prompt 12.1: Update README
```
Create comprehensive README.md for Wish Tracker:

Include:
- Project description
- Features list
- Technology stack
- Prerequisites (Java 21, Maven)
- Setup instructions:
  1. Clone repository
  2. Configure Google OAuth (if implemented)
  3. Run: mvn spring-boot:run
  4. Access: http://localhost:8080
  5. H2 Console: http://localhost:8080/h2-console
- Testing instructions: mvn test
- Project structure overview
- Screenshots (optional)
- Agentic development approach used
- Credits

Follow all rules in wish-tracker-rules.md strictly.
```

### Prompt 12.2: Code Review
```
Review the entire Wish Tracker application for:

1. Code quality:
   - Proper use of Spring Boot conventions
   - Consistent naming conventions
   - Proper exception handling
   - No code duplication

2. Functionality:
   - All CRUD operations work
   - Status transitions validated
   - Category deletion prevention works
   - Form validation works

3. UI/UX:
   - Matches Figma design
   - Responsive on mobile
   - User-friendly error messages
   - Smooth interactions

4. Testing:
   - All tests pass
   - Coverage > 80%
   - Tests are meaningful

5. Documentation:
   - README is complete
   - Code is well-commented
   - Setup instructions work

Provide a checklist of items to fix or improve.

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Emergency Prompts (If Stuck)

### Debug Prompt
```
I'm getting this error in Wish Tracker: [paste error]

Current context:
- Working on: [feature/component]
- What I'm trying to do: [description]

Help me debug and fix this issue.

Follow all rules in wish-tracker-rules.md strictly.
```

### Refactor Prompt
```
Review and refactor this code from Wish Tracker:

[paste code]

Issues:
- [describe problems]

Improve it following best practices.

Follow all rules in wish-tracker-rules.md strictly.
```

### Feature Addition
```
Add this feature to Wish Tracker: [describe feature]

Requirements:
- [list requirements]

Update all necessary layers: entity, repository, service, controller, view, tests.

Follow all rules in wish-tracker-rules.md strictly.
```

---

## Tips for Using These Prompts

1. **Start with Phase 1** and work sequentially
2. **Always include** "Follow all rules in wish-tracker-rules.md strictly" in each prompt
3. **Review output** before moving to next prompt
4. **Test immediately** after each phase
5. **Use Plan → Execute → Review** cycle
6. **Reference previous work** when needed: "Building on the entities created earlier..."
7. **Be specific** about what needs fixing if something is wrong
8. **Verify UI** matches Figma design after template generation
9. **Run tests** after Phase 10 to ensure quality
10. **Time management**: Phases 1-6 are critical, do those first

## Estimated Time Per Phase
- Phase 1-2: 15 minutes (setup + entities)
- Phase 3-5: 20 minutes (repos + services)
- Phase 6: 15 minutes (controllers)
- Phase 7-8: 30 minutes (UI templates + CSS)
- Phase 9: 5 minutes (sample data)
- Phase 10: 25 minutes (tests)
- Phase 11: 15 minutes (Google SSO - optional)
- Phase 12: 10 minutes (README + review)

**Total: ~2-2.5 hours**
```

---

## How to Use Tomorrow

1. **Save both files**:
```bash
cd ~
# Save the two files I provided above
```

2. **Start Claude Code**:
```bash
mkdir wish-tracker
cd wish-tracker
claude
```

3. **First prompt in Claude**:
```
I'm building Wish Tracker application. I have detailed rules in ~/wish-tracker-rules.md and prompts in ~/wish-tracker-prompts.md.

Please read both files first, then let's start with Phase 1.1 from the prompts file.
```

4. **Continue with each phase**, copy-pasting prompts from the prompts file

5. **Test after each phase** before moving forward

Good luck tomorrow! 🚀