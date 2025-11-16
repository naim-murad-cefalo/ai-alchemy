/*-- Sample Data for Wish Tracker Application
-- Creates multiple users with categories and wishes for testing

-- ============================================
-- USER 1: John Doe
-- ============================================
INSERT INTO users (id, email, name, picture_url, created_date, last_login_date) VALUES
(1, 'john.doe@example.com', 'John Doe', 'https://via.placeholder.com/150', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Categories for User 1
INSERT INTO categories (id, name, description, color, user_id, created_date) VALUES
(1, 'Travel', 'Places I want to visit and travel experiences', '#3B82F6', 1, CURRENT_TIMESTAMP),
(2, 'Books', 'Reading list and literary goals', '#8B5CF6', 1, CURRENT_TIMESTAMP),
(3, 'Fitness', 'Health and fitness objectives', '#10B981', 1, CURRENT_TIMESTAMP),
(4, 'Tech', 'Technology and programming skills', '#F59E0B', 1, CURRENT_TIMESTAMP),
(5, 'Personal', 'Personal development and life goals', '#EC4899', 1, CURRENT_TIMESTAMP);

-- Wishes for User 1 - WISH Status
INSERT INTO wishes (id, title, description, status, remarks, category_id, user_id, created_date, updated_date, achieved_date) VALUES
(1, 'Visit Japan', 'Experience cherry blossom season in Tokyo and Kyoto', 'WISH', 'Need to save $3000 for the trip', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(2, 'Read 50 Books This Year', 'Complete reading challenge with diverse genres', 'WISH', 'Currently at 12 books', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(3, 'Learn Spanish', 'Become conversational in Spanish for my trip to Spain', 'WISH', 'Planning to use Duolingo and take classes', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(4, 'Build a Home Gym', 'Set up a complete workout space at home', 'WISH', 'Need to research equipment', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(5, 'Learn Kubernetes', 'Master container orchestration for work', 'WISH', 'Enrolled in online course', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Wishes for User 1 - IN_PROGRESS Status
INSERT INTO wishes (id, title, description, status, remarks, category_id, user_id, created_date, updated_date, achieved_date) VALUES
(6, 'Run a Half Marathon', 'Complete my first 21k race', 'IN_PROGRESS', 'Training 4 days per week, race in 2 months', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(7, 'Master Spring Boot', 'Build production-ready REST APIs', 'IN_PROGRESS', 'Working through official documentation', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(8, 'Meditate Daily', 'Establish a consistent meditation practice', 'IN_PROGRESS', '15 days streak so far', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(9, 'Visit All National Parks in My State', 'Complete the state parks challenge', 'IN_PROGRESS', '3 out of 8 visited', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(10, 'Read War and Peace', 'Finally tackle this classic novel', 'IN_PROGRESS', 'Page 234 of 1296', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Wishes for User 1 - ACHIEVED Status
INSERT INTO wishes (id, title, description, status, remarks, category_id, user_id, created_date, updated_date, achieved_date) VALUES
(11, 'Complete Java Certification', 'Pass Oracle Java SE Certification', 'ACHIEVED', 'Passed with 85%!', 4, 1, DATEADD('DAY', -30, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -5, CURRENT_TIMESTAMP)),
(12, 'Run 5K Without Stopping', 'Build up running endurance', 'ACHIEVED', 'Completed in 28 minutes', 3, 1, DATEADD('DAY', -60, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -15, CURRENT_TIMESTAMP)),
(13, 'Read The Lord of the Rings', 'Complete the entire trilogy', 'ACHIEVED', 'Took 2 months but loved it!', 2, 1, DATEADD('DAY', -90, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -30, CURRENT_TIMESTAMP)),
(14, 'Visit Grand Canyon', 'Road trip to Arizona', 'ACHIEVED', 'Amazing experience!', 1, 1, DATEADD('DAY', -45, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -20, CURRENT_TIMESTAMP)),
(15, 'Wake Up at 6 AM for 30 Days', 'Develop early morning routine', 'ACHIEVED', 'Game changer for productivity', 5, 1, DATEADD('DAY', -40, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -10, CURRENT_TIMESTAMP));

-- ============================================
-- USER 2: Jane Smith
-- ============================================
INSERT INTO users (id, email, name, picture_url, created_date, last_login_date) VALUES
(2, 'jane.smith@example.com', 'Jane Smith', 'https://via.placeholder.com/150', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Categories for User 2
INSERT INTO categories (id, name, description, color, user_id, created_date) VALUES
(6, 'Career', 'Professional development and career goals', '#6366F1', 2, CURRENT_TIMESTAMP),
(7, 'Art', 'Creative projects and artistic pursuits', '#EC4899', 2, CURRENT_TIMESTAMP),
(8, 'Languages', 'Learning new languages', '#F59E0B', 2, CURRENT_TIMESTAMP),
(9, 'Music', 'Musical skills and performances', '#8B5CF6', 2, CURRENT_TIMESTAMP),
(10, 'Adventure', 'Outdoor adventures and experiences', '#10B981', 2, CURRENT_TIMESTAMP);

-- Wishes for User 2 - WISH Status
INSERT INTO wishes (id, title, description, status, remarks, category_id, user_id, created_date, updated_date, achieved_date) VALUES
(16, 'Get AWS Solutions Architect Certification', 'Advance cloud computing skills', 'WISH', 'Studying for the exam', 6, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(17, 'Paint a Self-Portrait', 'Create a detailed self-portrait in oils', 'WISH', 'Bought supplies, need to start', 7, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(18, 'Learn Italian Cooking', 'Master authentic Italian cuisine', 'WISH', 'Planning to take a class', 10, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(19, 'Compose an Original Song', 'Write and record my own music', 'WISH', 'Have some melody ideas', 9, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(20, 'Skydive', 'Experience the thrill of skydiving', 'WISH', 'Looking for a good location', 10, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Wishes for User 2 - IN_PROGRESS Status
INSERT INTO wishes (id, title, description, status, remarks, category_id, user_id, created_date, updated_date, achieved_date) VALUES
(21, 'Learn French', 'Become conversational in French', 'IN_PROGRESS', 'Practicing with Duolingo daily', 8, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(22, 'Complete Portfolio Website', 'Build professional portfolio site', 'IN_PROGRESS', '70% complete, adding case studies', 6, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(23, 'Learn to Play Guitar', 'Master basic guitar chords and songs', 'IN_PROGRESS', 'Can play 5 songs so far', 9, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(24, 'Hike the Appalachian Trail Section', 'Complete a 50-mile section hike', 'IN_PROGRESS', 'Training with weekend hikes', 10, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
(25, 'Create Art Series', 'Complete a series of 10 watercolor paintings', 'IN_PROGRESS', '4 paintings done', 7, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL);

-- Wishes for User 2 - ACHIEVED Status
INSERT INTO wishes (id, title, description, status, remarks, category_id, user_id, created_date, updated_date, achieved_date) VALUES
(26, 'Get Promoted to Senior Developer', 'Advance in my career', 'ACHIEVED', 'Promoted last month!', 6, 2, DATEADD('DAY', -90, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -30, CURRENT_TIMESTAMP)),
(27, 'Complete Oil Painting Course', 'Finish beginner oil painting class', 'ACHIEVED', 'Learned so much!', 7, 2, DATEADD('DAY', -60, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -20, CURRENT_TIMESTAMP)),
(28, 'Visit Paris', 'See the Eiffel Tower and Louvre', 'ACHIEVED', 'Dream trip fulfilled!', 10, 2, DATEADD('DAY', -120, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -45, CURRENT_TIMESTAMP)),
(29, 'Learn Basic Spanish', 'Hold simple conversations in Spanish', 'ACHIEVED', 'Can order food and ask directions', 8, 2, DATEADD('DAY', -150, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -60, CURRENT_TIMESTAMP)),
(30, 'Perform at Open Mic Night', 'Overcome stage fright and perform', 'ACHIEVED', 'Sang two songs, got great feedback!', 9, 2, DATEADD('DAY', -75, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -25, CURRENT_TIMESTAMP));

-- ============================================
-- Sequence Reset (H2 specific)
-- ============================================
ALTER SEQUENCE users_seq RESTART WITH 3;
ALTER SEQUENCE categories_seq RESTART WITH 11;
ALTER SEQUENCE wishes_seq RESTART WITH 31;
*/