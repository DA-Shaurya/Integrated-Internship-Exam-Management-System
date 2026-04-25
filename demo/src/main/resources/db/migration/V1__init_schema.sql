CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE companies (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255)
);

CREATE TABLE internships (
    internship_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT,
    role VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL,
    description TEXT,
    stipend DOUBLE,
    min_cgpa DOUBLE,
    deadline DATE,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (company_id) REFERENCES companies(company_id)
);

CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    internship_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    resume_url VARCHAR(1024),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(user_id),
    FOREIGN KEY (internship_id) REFERENCES internships(internship_id)
);

CREATE TABLE exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL
);

CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT,
    question_text TEXT NOT NULL,
    marks INT DEFAULT 1,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id)
);

CREATE TABLE options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

CREATE TABLE exam_attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    exam_id INT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    status VARCHAR(50) DEFAULT 'IN_PROGRESS',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id)
);

CREATE TABLE results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    obtained_marks INT NOT NULL,
    total_marks INT NOT NULL,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id)
);

CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
