-- Create postgres user if it doesn't exist and set password
DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'postgres') THEN
      CREATE USER postgres WITH PASSWORD 'postgres';
   ELSE
      ALTER USER postgres WITH PASSWORD 'postgres';
   END IF;
END
$$;

-- Grant necessary permissions to postgres user
GRANT ALL PRIVILEGES ON DATABASE postgres TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;
ALTER USER postgres WITH SUPERUSER;

-- Create the student table
CREATE TABLE IF NOT EXISTS student (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER,
    grade VARCHAR(255)
);

-- Create an index on the email field for faster lookups
CREATE INDEX IF NOT EXISTS idx_student_email ON student(email);

-- Add comments to the table and columns for documentation
COMMENT ON TABLE student IS 'Student information table monitored by Debezium for CDC';
COMMENT ON COLUMN student.id IS 'Unique identifier for the student';
COMMENT ON COLUMN student.first_name IS 'Student''s first name';
COMMENT ON COLUMN student.last_name IS 'Student''s last name';
COMMENT ON COLUMN student.email IS 'Student''s email address (unique)';
COMMENT ON COLUMN student.age IS 'Student''s age';
COMMENT ON COLUMN student.grade IS 'Student''s current grade level';

-- Insert sample data into the student table
INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('John', 'Doe', 'john.doe@example.com', 18, '12th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('Jane', 'Smith', 'jane.smith@example.com', 17, '11th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('Michael', 'Johnson', 'michael.johnson@example.com', 16, '10th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('Emily', 'Williams', 'emily.williams@example.com', 15, '9th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('David', 'Brown', 'david.brown@example.com', 14, '8th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('Sarah', 'Miller', 'sarah.miller@example.com', 13, '7th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('Robert', 'Wilson', 'robert.wilson@example.com', 12, '6th');

INSERT INTO student (first_name, last_name, email, age, grade) 
VALUES ('Jennifer', 'Taylor', 'jennifer.taylor@example.com', 11, '5th');

INSERT INTO student (first_name, last_name, email, age, grade)
VALUES ('Vaisahli', 'Doe', 'vaisah.doe@example.com', 18, '12th');
