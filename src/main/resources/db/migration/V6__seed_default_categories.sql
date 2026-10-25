INSERT INTO categories (name, description, color, created_at, updated_at)
VALUES 
('Fitness', 'Physical health, workouts, and exercise', '#dc3545', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Study', 'Learning, courses, and educational goals', '#0d6efd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Work', 'Career tasks and professional projects', '#ffc107', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Health', 'Wellness, nutrition, and mental health', '#198754', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Personal', 'Habits, personal growth, and daily routines', '#6f42c1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Reading', 'Books, articles, and documentation', '#fd7e14', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Coding', 'Software development and practice problems', '#20c997', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Finance', 'Budgeting, saving, and financial planning', '#0dcaf0', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
