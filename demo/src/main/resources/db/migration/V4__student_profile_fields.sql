CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    cgpa DOUBLE,
    course VARCHAR(255),
    phone VARCHAR(20),
    skills VARCHAR(1000),
    resume_filename VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Note: In a real environment, you'd use a more complex check for columns,
-- but for this project context, this is sufficient.
ALTER TABLE applications ADD COLUMN cgpa_at_apply DOUBLE;
