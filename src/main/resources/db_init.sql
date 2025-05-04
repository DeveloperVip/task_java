-- HR Management System Database Schema and Sample Data
-- Create database if not exists
CREATE DATABASE IF NOT EXISTS hr_system;
USE hr_system;

-- Drop tables if they exist (for clean reset)
DROP TABLE IF EXISTS leaves;
DROP TABLE IF EXISTS contracts;
DROP TABLE IF EXISTS candidates;
DROP TABLE IF EXISTS employees;

-- Create employees table
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    join_date DATE NOT NULL
);

-- Create contracts table
CREATE TABLE contracts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL
);

-- Create leaves table
CREATE TABLE leaves (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL
);

-- Create candidates table
CREATE TABLE candidates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position_applied VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Insert sample employees
INSERT INTO employees (name, position, join_date) VALUES 
('John Smith', 'Software Developer', '2023-02-15'),
('Emma Johnson', 'HR Manager', '2022-08-10'),
('Michael Brown', 'Marketing Specialist', '2024-01-20'),
('Sarah Davis', 'Financial Analyst', '2023-11-05'),
('Robert Wilson', 'Project Manager', '2022-05-30');

-- Insert sample contracts
INSERT INTO contracts (employee_id, start_date, end_date, status) VALUES 
(1, '2023-02-15', '2025-02-15', 'Active'), -- John Smith (fixed-term)
(2, '2022-08-10', NULL, 'Active'),         -- Emma Johnson (indefinite)
(3, '2024-01-20', '2025-01-20', 'Active'), -- Michael Brown (fixed-term)
(4, '2023-11-05', '2024-04-30', 'Terminated'), -- Sarah Davis (terminated)
(5, '2022-05-30', '2024-05-30', 'Active'); -- Robert Wilson (fixed-term)

-- Insert sample leave requests
INSERT INTO leaves (employee_id, start_date, end_date, reason, status) VALUES 
(1, '2025-05-10', '2025-05-15', 'Family vacation', 'Pending'),
(2, '2025-06-01', '2025-06-05', 'Personal reasons', 'Approved'),
(3, '2025-05-20', '2025-05-21', 'Medical appointment', 'Pending'),
(5, '2025-05-05', '2025-05-12', 'Conference attendance', 'Rejected');

-- Insert sample candidates
INSERT INTO candidates (name, position_applied, status) VALUES 
('Alice Thompson', 'Software Developer', 'New'),
('David Clark', 'Project Manager', 'Interviewed'),
('Jennifer Walker', 'Marketing Specialist', 'Rejected'),
('Thomas Lee', 'Financial Analyst', 'Hired'),
('Olivia Garcia', 'HR Assistant', 'New');