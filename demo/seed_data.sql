USE iems_db;

-- Disable FK checks for clean truncate
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE options;
TRUNCATE TABLE questions;
TRUNCATE TABLE exams;
TRUNCATE TABLE internships;
TRUNCATE TABLE companies;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Companies
INSERT INTO companies (company_name, location) VALUES 
('Google', 'Bangalore'),
('Microsoft', 'Hyderabad'),
('Meta', 'Remote');

-- 2. Internships
INSERT INTO internships (role, domain, description, min_cgpa, is_active, company_id, stipend, deadline) VALUES 
('Frontend Developer Intern', 'Web Development', 'Work on React.js and high-performance UI components.', 7.5, 1, 1, 50000.0, '2026-12-31'),
('Backend Engineer Intern', 'Software Engineering', 'Build scalable microservices with Spring Boot and MySQL.', 8.0, 1, 2, 60000.0, '2026-12-31'),
('Data Science Intern', 'AI/ML', 'Apply machine learning models to large scale datasets.', 8.5, 1, 3, 70000.0, '2026-12-31');

-- 3. Exams
INSERT INTO exams (exam_name, duration) VALUES 
('Full Stack Basics', 30),
('Java Core Quiz', 20);

-- 4. Questions & Options for 'Full Stack Basics' (Exam ID 1)
INSERT INTO questions (exam_id, question_text, type, marks) VALUES 
(1, 'What is the purpose of useEffect in React?', 'MCQ', 10),
(1, 'Which annotation is used to map a class as a Spring Boot entry point?', 'MCQ', 10);

INSERT INTO options (question_id, option_text, is_correct) VALUES 
(1, 'To perform side effects in functional components', 1),
(1, 'To create new state variables', 0),
(1, 'To handle routing between pages', 0),
(2, '@SpringBootApplication', 1),
(2, '@EnableAutoConfiguration', 0),
(2, '@ComponentScan', 0);

-- 5. Questions & Options for 'Java Core Quiz' (Exam ID 2)
INSERT INTO questions (exam_id, question_text, type, marks) VALUES 
(2, 'What is a Record in Java?', 'MCQ', 10),
(2, 'Which interface is used to make a class thread-safe via synchronization?', 'MCQ', 10);

INSERT INTO options (question_id, option_text, is_correct) VALUES 
(3, 'A special kind of class for immutable data', 1),
(3, 'A database entry object', 0),
(3, 'A way to log system events', 0),
(4, 'Runnable', 0),
(4, 'Callable', 0),
(4, 'Lock', 1);
