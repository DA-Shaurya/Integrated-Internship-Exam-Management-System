-- Users
INSERT IGNORE INTO users (email, password, name, role) VALUES 
('shaurya@gmail.com', '1234', 'Admin Shaurya', 'ADMIN'),
('test@gmail.com', '1234', 'Student Test', 'STUDENT');

-- Companies
INSERT IGNORE INTO companies (company_name, location) VALUES 
('Google', 'Mountain View'),
('Microsoft', 'Redmond'),
('Meta', 'Menlo Park');

-- Internships
INSERT IGNORE INTO internships (company_id, role, domain, description, stipend, min_cgpa, deadline) VALUES 
(1, 'Software Engineer Intern', 'Backend', 'Work on Google Cloud', 50000, 8.5, '2026-06-30'),
(2, 'Frontend Developer Intern', 'Frontend', 'Work on VS Code', 45000, 8.0, '2026-05-15'),
(3, 'Data Engineering Intern', 'Data', 'Work on Meta AI', 55000, 7.5, '2026-07-20');

-- Applications (student_id 2 applies to internship 1 and 2)
INSERT IGNORE INTO applications (student_id, internship_id, status) VALUES 
(2, 1, 'APPLIED'),
(2, 2, 'SHORTLISTED');

-- Exams
INSERT IGNORE INTO exams (exam_name, duration_minutes) VALUES 
('Software Engineering MCQ', 60),
('Data Structures & Algorithms', 45);

-- Questions
INSERT IGNORE INTO questions (exam_id, question_text, marks) VALUES 
(1, 'What is the parent class of all Java classes?', 2),
(1, 'Which of these is NOT a primitive type?', 2),
(1, 'What does SQL stand for?', 2),
(1, 'Which design pattern ensures only one instance of a class?', 2),
(1, 'What does HTTP stand for?', 2),
(2, 'Which data structure uses LIFO?', 2),
(2, 'What is the time complexity of binary search?', 2),
(2, 'Which data structure uses FIFO?', 2),
(2, 'What is a graph with no cycles called?', 2),
(2, 'Which sorting algorithm has O(n log n) average time complexity?', 2);

-- Options
INSERT IGNORE INTO options (question_id, option_text, is_correct) VALUES 
(1, 'Object', TRUE), (1, 'Class', FALSE), (1, 'Main', FALSE), (1, 'System', FALSE),
(2, 'int', FALSE), (2, 'String', TRUE), (2, 'boolean', FALSE), (2, 'double', FALSE),
(3, 'Structured Query Language', TRUE), (3, 'Simple Query Language', FALSE), (3, 'Standard Query Logic', FALSE), (3, 'Sequential Query Language', FALSE),
(4, 'Singleton', TRUE), (4, 'Factory', FALSE), (4, 'Observer', FALSE), (4, 'Decorator', FALSE),
(5, 'HyperText Transfer Protocol', TRUE), (5, 'HyperText Transmission Protocol', FALSE), (5, 'HighText Transfer Protocol', FALSE), (5, 'Hyper Transfer Text Protocol', FALSE),
(6, 'Stack', TRUE), (6, 'Queue', FALSE), (6, 'Tree', FALSE), (6, 'Graph', FALSE),
(7, 'O(log n)', TRUE), (7, 'O(n)', FALSE), (7, 'O(n^2)', FALSE), (7, 'O(1)', FALSE),
(8, 'Queue', TRUE), (8, 'Stack', FALSE), (8, 'Array', FALSE), (8, 'Linked List', FALSE),
(9, 'Tree', TRUE), (9, 'Graph', FALSE), (9, 'Cycle', FALSE), (9, 'Mesh', FALSE),
(10, 'Merge Sort', TRUE), (10, 'Bubble Sort', FALSE), (10, 'Insertion Sort', FALSE), (10, 'Selection Sort', FALSE);
