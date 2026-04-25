USE iems_db;
UPDATE users SET password = '$2a$12$R9h/lIPz0bouic7ZeJlGLu8znSZSxwS436BWASU.u3z18vjWvKOn2', role = 'ADMIN' WHERE email = 'shaurya@gmail.com';
UPDATE users SET password = '$2a$12$R9h/lIPz0bouic7ZeJlGLu8znSZSxwS436BWASU.u3z18vjWvKOn2', role = 'STUDENT' WHERE email = 'test@gmail.com';
