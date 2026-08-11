-- ==========================================
-- DUMMY DATA SEED SCRIPT FOR ROBOTEST
-- Inserts realistic data after existing rows
-- ==========================================

-- Clean up existing dummy data to allow clean, idempotent reruns
DELETE FROM results WHERE id >= 100;
DELETE FROM submissions WHERE id >= 1000;
DELETE FROM registrations WHERE user_id >= 100 OR contest_id >= 100;
DELETE FROM questions WHERE id >= 1000;
DELETE FROM contests WHERE id >= 100;
DELETE FROM user_roles WHERE user_id >= 100;
DELETE FROM users WHERE id >= 100;

-- ── 1. INSERT USERS (Starting IDs at 100) ──
INSERT INTO users (
    id, bio, created_at, email, email_verified, enabled,
    password_reset_token_used, full_name, gender, hobby, password, profile_image_url,
    registration_number, roll_number, university, updated_at, username
) VALUES
    (100, 'Passionate about mechanical design and robotics.', NOW() - INTERVAL '15 days', 'john.doe@gmail.com', TRUE, TRUE, FALSE, 'John Doe', 'Male', '3D Printing', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'CUET-2023-ME-01', '2303001', 'CUET', NOW(), 'john_cad'),
    (101, 'CAD speed modeler and SolidWorks enthusiast.', NOW() - INTERVAL '14 days', 'jane.smith@gmail.com', TRUE, TRUE, FALSE, 'Jane Smith', 'Female', 'Arduino', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'DU-2022-RME-04', '2204004', 'Dhaka University', NOW(), 'jane_designer'),
    (102, 'Designing autonomous drones and light mechanism structures.', NOW() - INTERVAL '13 days', 'bot.builder@gmail.com', TRUE, TRUE, FALSE, 'Tahmid Hossain', 'Male', 'RC Planes', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'BUET-2023-ME-18', '2305018', 'BUET', NOW(), 'bot_builder'),
    (103, 'Fusion 360 certified user. Love complex assemblies.', NOW() - INTERVAL '12 days', 'cad.master@gmail.com', TRUE, TRUE, FALSE, 'Sajid Alam', 'Male', 'Gaming', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'RUET-2022-IPE-12', '2208012', 'RUET', NOW(), 'cad_master'),
    (104, 'Mechanical engineering student from MIST.', NOW() - INTERVAL '11 days', 'mist.designer@gmail.com', TRUE, TRUE, FALSE, 'Nusrat Jahan', 'Female', 'Sketching', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'MIST-2023-AE-09', '2309009', 'MIST', NOW(), 'mist_designer'),
    (105, 'SolidWorks CSWA certified. Speed is key!', NOW() - INTERVAL '10 days', 'sw.guru@gmail.com', TRUE, TRUE, FALSE, 'Arif Rahman', 'Male', 'Blogging', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'KUET-2022-ME-31', '2203031', 'KUET', NOW(), 'sw_guru'),
    (106, 'Onshape power user and mechanism design expert.', NOW() - INTERVAL '9 days', 'fusion.expert@gmail.com', TRUE, TRUE, FALSE, 'Zayan Ahmed', 'Male', 'Robotics', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'DU-2023-RME-15', '2304015', 'Dhaka University', NOW(), 'fusion_expert'),
    (107, 'CAD/CAM fan. Making CNC parts is fun.', NOW() - INTERVAL '8 days', 'cuet.cad@gmail.com', TRUE, TRUE, FALSE, 'Faria Islam', 'Female', 'DIY Crafts', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'CUET-2022-ME-14', '2203014', 'CUET', NOW(), 'cuet_cad'),
    (108, 'Mechatronics designer specializing in automated systems.', NOW() - INTERVAL '7 days', 'ruet.maker@gmail.com', TRUE, TRUE, FALSE, 'Sadman Sakib', 'Male', '3D Printing', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'RUET-2023-MTE-27', '2307027', 'RUET', NOW(), 'ruet_maker'),
    (109, 'BUET ME student, looking to solve advanced mechanical problems.', NOW() - INTERVAL '6 days', 'buet.mech@gmail.com', TRUE, TRUE, FALSE, 'Rafid Anwar', 'Male', 'Robotics', '$2a$12$N9qo8uLOVGCoo2bW.7AZe.O22cTzD8n5jL/dY91K.a5e.E1kFp/uG', NULL, 'BUET-2022-ME-42', '2205042', 'BUET', NOW(), 'buet_mech')
ON CONFLICT (id) DO NOTHING;

-- Assign ROLE_USER to all dummy users
INSERT INTO user_roles (user_id, role_id) VALUES
    (100, 1), (101, 1), (102, 1), (103, 1), (104, 1),
    (105, 1), (106, 1), (107, 1), (108, 1), (109, 1)
ON CONFLICT DO NOTHING;


-- ── 2. INSERT CONTESTS (Starting IDs at 100) ──
INSERT INTO contests (
    id, name, description, contest_date, registration_start,
    registration_end, contest_start, contest_end, status, created_at, updated_at
) VALUES
    -- Finished Contest 1
    (100, 'Gearbox Mechanism Challenge', 
     'Design and optimize a 2-stage reduction gearbox housing. Focus on reducing weight while maintaining structural strength. Submit the overall assembly mass in grams.',
     NOW() - INTERVAL '15 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '16 days', 
     NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days' + INTERVAL '2 hours', 
     'FINISHED', NOW() - INTERVAL '20 days', NOW()),

    -- Finished Contest 2
    (101, 'Quadcopter Chassis Optimization', 
     'Create a lightweight quadcopter arm structure. The arm must handle a motor thrust of 1.2 kg at its end point. Validate the optimized mass using aluminum density.',
     NOW() - INTERVAL '8 days', NOW() - INTERVAL '12 days', NOW() - INTERVAL '9 days', 
     NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days' + INTERVAL '3 hours', 
     'FINISHED', NOW() - INTERVAL '12 days', NOW()),

    -- Running Contest (Active now!)
    (102, 'Robotic Gripper Speed Modeling', 
     'A fast-paced speed modeling challenge. Model a mechanical gripper finger linkage based on the provided technical drawings. Submit the exact model weight and answer key questions.',
     NOW(), NOW() - INTERVAL '3 days', NOW() + INTERVAL '2 hours', 
     NOW() - INTERVAL '30 minutes', NOW() + INTERVAL '2 hours', 
     'RUNNING', NOW() - INTERVAL '4 days', NOW()),

    -- Registration Open Contest
    (103, 'Drone Frame Light-weighting', 
     'Optimize a carbon-fiber drone central plate. Cutout shapes must respect the mounting holes for flight controller, battery straps, and ESC board. Registration is open!',
     NOW() + INTERVAL '3 days', NOW() - INTERVAL '1 day', NOW() + INTERVAL '2 days', 
     NOW() + INTERVAL '3 days', NOW() + INTERVAL '3 days' + INTERVAL '4 hours', 
     'REGISTRATION_OPEN', NOW() - INTERVAL '2 days', NOW()),

    -- Upcoming Contest
    (104, 'Linkage Mechanism Design', 
     'Upcoming challenge focusing on 4-bar linkage optimization. Participants will generate coupler curves matching target trajectories. Check back soon!',
     NOW() + INTERVAL '10 days', NOW() + INTERVAL '7 days', NOW() + INTERVAL '9 days', 
     NOW() + INTERVAL '10 days', NOW() + INTERVAL '10 days' + INTERVAL '2 hours', 
     'UPCOMING', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;


-- ── 3. INSERT QUESTIONS (Starting IDs at 1000) ──
INSERT INTO questions (
    id, contest_id, description, image_url, video_url,
    type, time_limit, correct_answer, error_percentage,
    custom_answer_key, order_index, points
) VALUES
    -- Questions for Contest 100 (Gearbox Mechanism Challenge)
    (1000, 100, 'Model the main input shaft spur gear. Calculate its total weight in grams using structural steel density.', '/uploads/questions/images/gear_input.png', NULL, 'NUMERIC_MCQ', 1800, 420.5, 1.0, NULL, 1, 10),
    (1001, 100, 'Enter the exact number of teeth of the reduction gear stage 1.', NULL, NULL, 'CUSTOM', 900, NULL, NULL, '36', 2, 10),
    (1002, 100, 'Complete the outer casing and calculate the overall assembly volume. Match the density of Aluminum 6061-T6 to find the mass in grams.', '/uploads/questions/images/gear_casing.png', NULL, 'NUMERIC_MCQ', 2700, 1250.3, 0.5, NULL, 3, 20),

    -- Questions for Contest 101 (Quadcopter Chassis Optimization)
    (1010, 101, 'Model the carbon fiber quadcopter arm link. Find the final mass in grams.', '/uploads/questions/images/drone_arm.png', NULL, 'NUMERIC_MCQ', 1800, 54.8, 1.0, NULL, 1, 15),
    (1011, 101, 'What is the bolt diameter spec used for the motor mount holes on the quadcopter arm? (e.g. M3, M4, M5)', NULL, NULL, 'CUSTOM', 600, NULL, NULL, 'M3', 2, 10),

    -- Questions for Contest 102 (Robotic Gripper Speed Modeling - RUNNING)
    (1020, 102, 'Model the inner active finger linkage. Find the weight in grams using Brass density.', '/uploads/questions/images/gripper_finger.png', NULL, 'NUMERIC_MCQ', 1200, 112.4, 1.0, NULL, 1, 15),
    (1021, 102, 'What is the center-to-center link length in millimeters for the parallel support link?', NULL, NULL, 'CUSTOM', 900, NULL, NULL, '45mm', 2, 15),

    -- Questions for Contest 103 (Drone Frame - REGISTRATION_OPEN)
    (1030, 103, 'Model the top plate of the drone frame. Calculate the mass in grams using Carbon Fiber density.', '/uploads/questions/images/top_plate.png', NULL, 'NUMERIC_MCQ', 1800, 32.1, 1.5, NULL, 1, 20)
ON CONFLICT (id) DO NOTHING;


-- ── 4. INSERT REGISTRATIONS ──
INSERT INTO registrations (
    contest_id, user_id, registered_at, is_submission_complete
) VALUES
    -- Registrations for Contest 100 (Finished)
    (100, 100, NOW() - INTERVAL '18 days', TRUE),
    (100, 101, NOW() - INTERVAL '18 days', TRUE),
    (100, 102, NOW() - INTERVAL '17 days', TRUE),
    (100, 103, NOW() - INTERVAL '17 days', TRUE),
    (100, 104, NOW() - INTERVAL '16 days', FALSE),

    -- Registrations for Contest 101 (Finished)
    (101, 101, NOW() - INTERVAL '11 days', TRUE),
    (101, 102, NOW() - INTERVAL '11 days', TRUE),
    (101, 105, NOW() - INTERVAL '10 days', TRUE),
    (101, 106, NOW() - INTERVAL '10 days', FALSE),

    -- Registrations for Contest 102 (Running)
    (102, 100, NOW() - INTERVAL '2 days', FALSE),
    (102, 101, NOW() - INTERVAL '2 days', TRUE),
    (102, 102, NOW() - INTERVAL '1 day', TRUE),
    (102, 103, NOW() - INTERVAL '1 day', FALSE),
    (102, 105, NOW() - INTERVAL '12 hours', TRUE),

    -- Registrations for Contest 103 (Registration Open)
    (103, 100, NOW() - INTERVAL '2 hours', FALSE),
    (103, 101, NOW() - INTERVAL '1 hour', FALSE),
    (103, 102, NOW() - INTERVAL '30 mins', FALSE)
ON CONFLICT (user_id, contest_id) DO NOTHING;


-- ── 5. INSERT SUBMISSIONS (Starting IDs at 1000) ──
INSERT INTO submissions (
    id, user_id, contest_id, question_id, submitted_answer,
    wrong_count, correct, submitted_at, question_started_at
) VALUES
    -- Submissions for Contest 100 (Gearbox Mechanism Challenge)
    -- User 100 (Excellent performance)
    (1000, 100, 100, 1000, '420.5', 0, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '10 mins', NOW() - INTERVAL '15 days'),
    (1001, 100, 100, 1001, '36', 0, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '18 mins', NOW() - INTERVAL '15 days' + INTERVAL '12 mins'),
    (1002, 100, 100, 1002, '1251.2', 1, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '45 mins', NOW() - INTERVAL '15 days' + INTERVAL '20 mins'),

    -- User 101 (Medium performance)
    (1003, 101, 100, 1000, '422.1', 1, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '15 mins', NOW() - INTERVAL '15 days'),
    (1004, 101, 100, 1001, '36', 0, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '25 mins', NOW() - INTERVAL '15 days' + INTERVAL '18 mins'),
    (1005, 101, 100, 1002, '1300.0', 3, FALSE, NOW() - INTERVAL '15 days' + INTERVAL '60 mins', NOW() - INTERVAL '15 days' + INTERVAL '30 mins'),

    -- User 102 (Slow but correct)
    (1006, 102, 100, 1000, '420.2', 0, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '25 mins', NOW() - INTERVAL '15 days'),
    (1007, 102, 100, 1001, '36', 0, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '40 mins', NOW() - INTERVAL '15 days' + INTERVAL '30 mins'),
    (1008, 102, 100, 1002, '1250.3', 0, TRUE, NOW() - INTERVAL '15 days' + INTERVAL '80 mins', NOW() - INTERVAL '15 days' + INTERVAL '45 mins'),

    -- User 103 (Failed)
    (1009, 103, 100, 1000, '450.0', 4, FALSE, NOW() - INTERVAL '15 days' + INTERVAL '30 mins', NOW() - INTERVAL '15 days'),

    -- Submissions for Contest 101 (Quadcopter arm)
    -- User 101
    (1010, 101, 101, 1010, '54.8', 0, TRUE, NOW() - INTERVAL '8 days' + INTERVAL '12 mins', NOW() - INTERVAL '8 days'),
    (1011, 101, 101, 1011, 'm3', 0, TRUE, NOW() - INTERVAL '8 days' + INTERVAL '20 mins', NOW() - INTERVAL '8 days' + INTERVAL '15 mins'),

    -- User 102
    (1012, 102, 101, 1010, '55.1', 1, TRUE, NOW() - INTERVAL '8 days' + INTERVAL '18 mins', NOW() - INTERVAL '8 days'),
    (1013, 102, 101, 1011, 'M3', 0, TRUE, NOW() - INTERVAL '8 days' + INTERVAL '25 mins', NOW() - INTERVAL '8 days' + INTERVAL '20 mins'),

    -- User 105
    (1014, 105, 101, 1010, '60.0', 2, FALSE, NOW() - INTERVAL '8 days' + INTERVAL '30 mins', NOW() - INTERVAL '8 days'),
    (1015, 105, 101, 1011, 'M4', 1, FALSE, NOW() - INTERVAL '8 days' + INTERVAL '40 mins', NOW() - INTERVAL '8 days' + INTERVAL '32 mins'),

    -- Submissions for Contest 102 (Robotic Gripper - RUNNING)
    -- User 101
    (1020, 101, 102, 1020, '112.4', 0, TRUE, NOW() - INTERVAL '10 mins', NOW() - INTERVAL '20 mins'),
    (1021, 101, 102, 1021, '45mm', 0, TRUE, NOW() - INTERVAL '2 mins', NOW() - INTERVAL '9 mins'),

    -- User 102
    (1022, 102, 102, 1020, '113.0', 1, TRUE, NOW() - INTERVAL '5 mins', NOW() - INTERVAL '18 mins'),

    -- User 105
    (1023, 105, 102, 1020, '112.4', 0, TRUE, NOW() - INTERVAL '15 mins', NOW() - INTERVAL '25 mins')
ON CONFLICT (id) DO NOTHING;


-- ── 6. INSERT LEADERBOARD RESULTS (Starting IDs at 100) ──
INSERT INTO results (
    id, user_id, contest_id, total_score, solved_count, rank, calculated_at
) VALUES
    -- Results for Contest 100
    (100, 100, 100, 29.8, 3, 1, NOW() - INTERVAL '14 days'),
    (101, 102, 100, 28.5, 3, 2, NOW() - INTERVAL '14 days'),
    (102, 101, 100, 19.4, 2, 3, NOW() - INTERVAL '14 days'),
    (103, 103, 100, -0.8, 0, 4, NOW() - INTERVAL '14 days'),

    -- Results for Contest 101
    (104, 101, 101, 25.0, 2, 1, NOW() - INTERVAL '7 days'),
    (105, 102, 101, 24.6, 2, 2, NOW() - INTERVAL '7 days'),
    (106, 105, 101, -0.6, 0, 3, NOW() - INTERVAL '7 days')
ON CONFLICT (id) DO NOTHING;


-- ── 7. SYNCHRONIZE SEQUENCE COUNTERS ──
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1));
SELECT setval('contests_id_seq', COALESCE((SELECT MAX(id) FROM contests), 1));
SELECT setval('questions_id_seq', COALESCE((SELECT MAX(id) FROM questions), 1));
SELECT setval('registrations_id_seq', COALESCE((SELECT MAX(id) FROM registrations), 1));
SELECT setval('submissions_id_seq', COALESCE((SELECT MAX(id) FROM submissions), 1));
SELECT setval('results_id_seq', COALESCE((SELECT MAX(id) FROM results), 1));

-- Print status
SELECT 'Dummy Data Seeding Completed Successfully!' AS Status;
