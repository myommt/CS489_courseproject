-- Initial data for dental surgery app
-- Insert roles if they don't exist
INSERT IGNORE INTO roles (role_id, name)
VALUES (1, 'SYSADMIN'),
  (2, 'DENTIST'),
  (3, 'PATIENT');
-- Insert default admin user
-- Password is 'admin123' encoded with BCrypt (using camelCase column names to match PhysicalNamingStrategyStandardImpl)
INSERT IGNORE INTO users (
    firstName,
    lastName,
    username,
    password,
    email,
    enabled,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired
  )
VALUES (
    'System',
    'Administrator',
    'admin',
    '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq',
    'admin@dentalsurgery.com',
    1,
    1,
    1,
    1
  );
-- Insert second admin user
INSERT IGNORE INTO users (
    firstName,
    lastName,
    username,
    password,
    email,
    enabled,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired
  )
VALUES (
    'System',
    'Administrator',
    'admin2',
    '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq',
    'admin2@dentalsurgery.com',
    1,
    1,
    1,
    1
  );
-- Insert user with password 'admin123'
INSERT IGNORE INTO users (
    firstName,
    lastName,
    username,
    password,
    email,
    enabled,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired
  )
VALUES (
    'Test',
    'User',
    'testuser',
    '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq',
    'test@dentalsurgery.com',
    1,
    1,
    1,
    1
  );
-- Insert user Jim B with password 'admin123'
INSERT IGNORE INTO users (
    firstName,
    lastName,
    username,
    password,
    email,
    enabled,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired
  )
VALUES (
    'Jim',
    'Brown',
    'jim.b@gmail.com',
    '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq',
    'jim.b@gmail.com',
    1,
    1,
    1,
    1
  );
-- Insert user Tony Smith with password 'admin123'
INSERT IGNORE INTO users (
    firstName,
    lastName,
    username,
    password,
    email,
    enabled,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired
  )
VALUES (
    'Tony',
    'Smith',
    'tony.smith@southwest.dentists.org',
    '$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq',
    'tony.smith@southwest.dentists.org',
    1,
    1,
    1,
    1
  );
-- Assign SYSADMIN role to admin users
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (1, 1);
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (4, 1);
-- Insert Dentist records
INSERT IGNORE INTO dentists (
    dentist_id,
    firstName,
    lastName,
    email,
    contactNumber,
    specialization
  )
VALUES (
    1,
    'Test',
    'User',
    'test@dentalsurgery.com',
    '555-0100',
    'General Dentistry'
  );
INSERT IGNORE INTO dentists (
    dentist_id,
    firstName,
    lastName,
    email,
    contactNumber,
    specialization
  )
VALUES (
    2,
    'Tony',
    'Smith',
    'tony.smith@southwest.dentists.org',
    '555-0200',
    'Orthodontics'
  );
-- Insert Patient records  
INSERT IGNORE INTO patients (
    patient_id,
    firstName,
    lastName,
    email,
    contactNumber,
    dob
  )
VALUES (
    1,
    'Test',
    'User',
    'test@dentalsurgery.com',
    '555-0100',
    '1990-01-15'
  );
INSERT IGNORE INTO patients (
    patient_id,
    firstName,
    lastName,
    email,
    contactNumber,
    dob
  )
VALUES (
    2,
    'Jim',
    'Brown',
    'jim.b@gmail.com',
    '555-0300',
    '1985-05-20'
  );
-- Update users to link them with dentist/patient records
UPDATE users
SET dentist_id = 1
WHERE user_id = 3;
-- Test User is a dentist
UPDATE users
SET dentist_id = 2
WHERE user_id = 5;
-- Tony Smith is a dentist
--UPDATE users SET patient_id = 1 WHERE user_id = 2; -- Link one of the PATIENT role users to Test User patient record
UPDATE users
SET patient_id = 1
WHERE user_id = 5;
-- Jim B is a patient
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (1, 1);
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (5, 2);
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (5, 3);
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (2, 1);
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (3, 2);
INSERT IGNORE INTO users_roles (user_id, role_id)
VALUES (4, 3);
-- Insert sample addresses for surgery locations
INSERT IGNORE INTO addresses (address_id, street, city, state, zipcode)
VALUES (1, '123 Main Street', 'Fairfield', 'IA', '52557'),
  (2, '456 Oak Avenue', 'Des Moines', 'IA', '50309'),
  (3, '789 Cedar Lane', 'Iowa City', 'IA', '52240');
-- Insert sample surgery locations
INSERT IGNORE INTO surgerylocations (
    surgerylocation_id,
    name,
    contactNumber,
    address_id
  )
VALUES (1, 'Main Dental Clinic', '(641) 555-1234', 1),
  (2, 'Downtown Dental Care', '(515) 555-5678', 2),
  (
    3,
    'University Dental Center',
    '(319) 555-9012',
    3
  );
-- Insert sample appointments
INSERT IGNORE INTO appointments (
    appointment_id,
    appointmentDateTime,
    appointmentType,
    status,
    patient_id,
    dentist_id,
    surgerylocation_id
  )
VALUES (
    1,
    '2025-10-22 09:00:00',
    'Checkup',
    'SCHEDULED',
    1,
    1,
    1
  ),
  (
    2,
    '2025-10-22 14:00:00',
    'Cleaning',
    'CONFIRMED',
    2,
    2,
    2
  ),
  (
    3,
    '2025-10-21 10:00:00',
    'Filling',
    'COMPLETED',
    1,
    2,
    1
  ),
  (
    4,
    '2025-10-20 15:00:00',
    'Root Canal',
    'COMPLETED',
    2,
    1,
    3
  );
-- Insert sample bills (some pending, some paid)
INSERT IGNORE INTO bills (
    bill_id,
    total_cost,
    payment_status,
    patient_id,
    appointment_id
  )
VALUES (1, 150.00, 'PENDING', 1, 3),
  (2, 450.00, 'PENDING', 2, 4),
  (3, 200.00, 'PAID', 1, 1);