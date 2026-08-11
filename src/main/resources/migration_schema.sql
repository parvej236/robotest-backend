INSERT INTO rulebook (id, sections, metadata)
VALUES (
           1,
           '{
             "overview": "RMEDU CAD CONTEST is an online competition designed to improve CAD proficiency, engineering design ability, and creative problem-solving skills among students of the Robotics and Mechatronics Engineering Department. All contests are conducted through the official website for registration, downloads, and submissions.",
             "eligibility": "Exclusively for students of the Robotics and Mechatronics Engineering Department.\n Participants must use their own account; \n multiple accounts are strictly prohibited.",
             "quick_cad_speed_modeling": {
               "description": "A bi-weekly contest focused on speed and modeling accuracy from a given drawing.",
               "duration": "15-30 minutes",
               "submission_requirements": "CAD file (STEP, STL, or native), model weight (mass), and automatic time recording.",
               "evaluation_criteria": "1. Geometry Validation (must match drawing exactly). 2. Weight Accuracy (Ranked by Weight Error = |Submitted Weight - Actual Weight|). 3. Submission Time (Tie-breaker for similar accuracy)."
             },
             "mechathon": {
               "description": "A monthly team-based competition (exactly 2 members) to design mechanisms like lifting, walking, or robotic gripper systems.",
               "duration": "3-4 hours",
               "submission_requirements": "Complete CAD assembly, rendered images (PNG/JPG), and a PDF report covering working principle, components, and function. Optional animation (MP4/GIF).",
               "creativity_innovation": "30",
               "functionality": "30",
               "cad_modeling": "20",
               "engineering_feasibility": "20"
              },
             "software_protocol": "Participants may use professional CAD software: SolidWorks, Fusion 360, Autodesk Inventor, CATIA, Siemens NX, or Onshape.",
             "fair_play_and_disqualification": {
               "policy": "All participants must submit original work. Sharing CAD files or unfair collaboration is prohibited.",
               "disqualification_triggers": "Copied/plagiarized designs, downloaded internet models, pre-made submissions, multiple accounts, or false information."
             },
             "organizer_rights": "The committee reserves the right to update rules, modify schedules, and disqualify unfair submissions. All decisions are final."
           }',
           '{
             "version": "1.0",
             "publish_date": "April 12, 2026",
             "organization": "Robotics and Mechatronics Engineering Department",
             "monthly_weightage": {
               "creative_mechanism_design": "60%",
               "quick_cad_speed_modeling": "40%"
             }
           }'
       )
ON CONFLICT (id) DO UPDATE
    SET sections = EXCLUDED.sections,
        metadata = EXCLUDED.metadata,
        updated_at = CURRENT_TIMESTAMP;

-- Seed 50 contests with 5 questions each (only when contests table is empty)
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM contests) THEN
            WITH ins_contests AS (
                INSERT INTO contests (
                                      name,
                                      description,
                                      contest_date,
                                      registration_start,
                                      registration_end,
                                      contest_start,
                                      contest_end,
                                      status
                    )
                    SELECT
                        ('Contest ' || i) AS name,
                        ('Description for contest ' || i) AS description,

                        -- contest_date == contest_start (used by UI)
                        CASE
                            WHEN i <= 15 THEN NOW() - INTERVAL '1 day'
                            WHEN i <= 25 THEN NOW() - INTERVAL '10 days'
                            WHEN i <= 35 THEN NOW() + INTERVAL '1 day'
                            ELSE NOW() + INTERVAL '10 days'
                            END AS contest_date,

                        CASE
                            WHEN i <= 15 THEN NOW() - INTERVAL '2 days'
                            WHEN i <= 25 THEN NOW() - INTERVAL '20 days'
                            WHEN i <= 35 THEN NOW() - INTERVAL '1 day'
                            ELSE NOW() + INTERVAL '5 days'
                            END AS registration_start,

                        CASE
                            WHEN i <= 15 THEN NOW() - INTERVAL '1 day'
                            WHEN i <= 25 THEN NOW() - INTERVAL '15 days'
                            WHEN i <= 35 THEN NOW() + INTERVAL '2 hours'
                            ELSE NOW() + INTERVAL '6 days'
                            END AS registration_end,

                        CASE
                            WHEN i <= 15 THEN NOW() - INTERVAL '1 day'
                            WHEN i <= 25 THEN NOW() - INTERVAL '10 days'
                            WHEN i <= 35 THEN NOW() + INTERVAL '1 day'
                            ELSE NOW() + INTERVAL '10 days'
                            END AS contest_start,

                        CASE
                            WHEN i <= 15 THEN NOW() + INTERVAL '1 day'
                            WHEN i <= 25 THEN NOW() - INTERVAL '5 days'
                            WHEN i <= 35 THEN NOW() + INTERVAL '2 days'
                            ELSE NOW() + INTERVAL '11 days'
                            END AS contest_end,

                        'UPCOMING' AS status
                    FROM generate_series(1, 20) AS g(i)
                    RETURNING id
            )
        END IF;
    END $$;


insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (14, null, '2026-04-15 14:17:43.711863', 'sshupoma885@gmail.com', null, null, true, true, 'Upoma', null, null, '$2a$12$euKNmlEoHxg7n54ujLzK4.hb.h2O8swabo4D0ih3WU2MmL/FAfYD.', null, null, false, null, '$2a$12$8WsVWVn6jqt.HRdoQh92pO8ed7LJcGx3UMDaQJn8S0YtUV0yxn0t.', '2026-05-15 14:18:45.669432', null, null, null, '2026-04-15 14:18:45.669962', 'noobcad');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (9, '', '2026-04-15 14:10:13.614587', 'atmanan2006@gmail.com', '318fb88b-8245-43df-820e-b21ffe2581e3', '2026-04-16 14:10:13.614083', true, true, 'Ahnaf Tahmid Manan', '', '', '$2a$12$yTadItr3FseHN88QEvBLy.m0JgiVg8cYLpj9bDc9UKwiFo8g6mvTK', null, null, false, '/uploads/profiles/3e0ed353-bac4-416f-a696-b53ec20371ba.png', '$2a$12$jKpvqHAkHuOxAz44Ij1dLO9rmz0lBgXBc3.Api3uHG69pQMIYDIQC', '2026-05-15 14:39:32.791483', '', '', '', '2026-04-15 14:39:32.793289', 'ChomChom');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (16, null, '2026-04-15 14:18:32.298709', 'himi19122005@gmail.com', null, null, true, true, 'Sabah hussain', null, null, '$2a$12$4U.Y2ESyMTW0Q0kcR1wI8.KWJcNyeDvsEdJVfOc6gNOfEYR/1OYzW', null, null, false, null, '$2a$12$QHmVI1PlsWglWNYzmjJylO.iEQ0WmcDlp7RsPpQD8NrvlypUOE/Eq', '2026-05-15 14:19:13.255114', null, null, null, '2026-04-15 14:19:13.255520', 'Himi');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (13, null, '2026-04-15 14:13:09.349350', 'hello.adiba.82@gmail.com', '7eb08e8c-4dac-4850-881b-0a160267173e', '2026-04-16 14:13:09.349101', true, true, 'Adiba Nur Raisa', null, null, '$2a$12$ybUtnaQ7GYGQpizbkiuV4useALLw8ZpRnxBjpkxv1tHN7HsMEJurG', null, null, false, null, '$2a$12$97x7BPB51O0H57pLvi3QX.DwNux2bLSPQYoGTdHo/dBP46ymNDT0C', '2026-05-15 14:13:42.974259', null, null, null, '2026-04-15 14:13:42.974576', 'Adiba');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (5, null, '2026-04-15 00:11:44.974496', 'shahnewaj-2023616389@rme.du.ac.bd', '588f3c16-cc61-49c4-bf52-64c217b5e999', '2026-04-16 00:11:44.973984', true, true, 'Shahnewaj', null, null, '$2a$12$cu3fq1D.Zb1VuLwfH6iU3OJLajCaggrTjjYmeyhfTC8nftd3oNC/i', null, null, false, null, '$2a$12$DBjM0HkHToWU7nYoMFPslei1FJD0.t1ewSlm4zbCzODFtPLnOgdxG', '2026-05-15 00:18:34.081898', null, null, null, '2026-04-15 00:18:34.082759', 'Shahnewaj ');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (3, '', '2026-04-14 23:47:42.124035', 'hridoyq264@gmail.com', null, null, true, true, 'System Administrator', 'Other', 'Catching Pokémons', '$2a$12$SfvgiWDa00ygSwH2B76yUe29YUx/3TvTVYpqVFl0HEXd75ieoicgK', null, null, false, null, null, null, '', '', '', '2026-04-19 22:51:35.656776', 'admin');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (10, null, '2026-04-15 14:11:52.390412', 'samiarana5024@gmail.com', '486ceb9c-4bed-472d-a3af-0317bf633b7c', '2026-04-16 14:11:52.390160', true, true, 'samia binte rana', null, null, '$2a$12$0/oksjEOwnKOqDNp494E/OaI913tbXWjPkB8lHXCWftkhI11xWKSG', null, null, false, null, '$2a$12$x.EbZHrsxuOgT9z1Eqy5uu2Z/yz5NKPCiX9iI4rfhH62a8cl7miru', '2026-05-15 14:14:39.278990', null, null, null, '2026-04-15 14:14:39.279525', 'mybsmth');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (4, '', '2026-04-14 23:49:39.254393', 'softbot264@gmail.com', null, null, true, true, 'Shahnewaj Hridoy', '', '', '$2a$12$uTZobrkxsLd.zx43b9z/3OGAdu7.q1S66YY9WgT82rSN9CI/5vhrq', null, null, false, '/uploads/profiles/9d5c2894-275a-49e3-8dc5-aa16ded3369a.png', '$2a$12$X5G661d.x.1DtP5ki65Y9.TgdigHSyuGSc5BbcRJ4EJpBIBIO6BMK', '2026-05-19 22:51:46.260978', '', '', '', '2026-04-19 22:51:46.264709', 'Hridoy');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (12, null, '2026-04-15 14:12:24.851410', 'aadritomaitra@gmail.com', 'bb99cb02-20e6-4401-a33c-73d9a702cb18', '2026-04-16 14:12:24.851144', true, true, 'Aadrito Maitra', null, null, '$2a$12$dVQzdGg3VFEtuVwBWmucnuC3JVbobhzi5yeoN2EBCs5Qr0V8prHkK', null, null, false, null, '$2a$12$9rV1tADxnkmGX.Ccr9HgxOX5g.kdha.3Ysi5lbncSRp1ipupZQD62', '2026-05-15 14:15:19.620405', null, null, null, '2026-04-15 14:15:19.620782', 'ghum');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (15, null, '2026-04-15 14:18:05.699751', 'tigersakib2004@gmail.com', 'b523328b-9d75-472d-be3f-410999cc1674', '2026-04-16 14:18:05.699219', true, true, 'sakib', null, null, '$2a$12$O7cxOdL.0/ZH8xz3rbIqmOx17p09sm9hFiIIsBnW3h.m4PZwSfyu2', null, null, false, null, '$2a$12$hEnWKuYxbX02lRGLKTwre.4FIKHXi7jP96BW1JnSeh.RQelBzXw/m', '2026-05-15 14:20:07.266812', null, null, null, '2026-04-15 14:20:07.267364', 'sakib69');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (8, null, '2026-04-15 14:09:53.207169', 'nahamed343@gmail.com', '2633dce2-05bd-40dc-a04b-0de8b921807a', '2026-04-16 14:09:53.205282', true, true, 'fida nahal ahamed', null, null, '$2a$12$XLx48pxGRl35Qtk6vwihVe1DXI9prbLgaRkIAQLwUKxbmzZf9lEIG', null, null, false, null, '$2a$12$zdiUnyM/C6dL2mnR8lJ3V.bsPZwp93U7Xfsw0dYThMf2zuc1KxVae', '2026-05-15 14:16:19.430299', null, null, null, '2026-04-15 14:16:19.430946', 'nahal');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (7, null, '2026-04-15 12:38:31.360884', 'rishadrahman694@gmail.com', null, null, true, true, 'Rishad', null, null, '$2a$12$EQbUGUFnW2mabh34axZoQ.8zLXAMJWAfHxc70z1U/qg5Z8qDAEElO', null, null, false, null, '$2a$12$YslEnyFLlPxnHQnHLRv7eO//Kdq73nqpCdg4zjKx.CcUdwaWfW/fe', '2026-05-15 12:39:18.596701', null, null, null, '2026-04-15 12:39:18.597106', 'Rishad31');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (6, '', '2026-04-15 12:12:05.571786', 'samianasa2030@gmail.com', null, null, true, true, 'Samia Islam', 'Other', 'Sleeping', '$2a$12$3.JRkn0wllUhlTCoPtGRs.m9mwR.B//b5dOW6K65kEhIrmdbsN2sO', null, null, false, '/uploads/profiles/d56e6f2c-5dbc-4675-8c33-2684efa23eba.jpeg', '$2a$12$HzPbCSQgDdFpFHSGrYbTV.ujKn0l8wtQowubCWdZcSsR2it2kjXnC', '2026-05-15 14:16:31.588508', '2023116375', '04', 'Dhaka University', '2026-04-15 14:16:31.589235', 'Idk_shit');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (18, null, '2026-04-15 14:25:42.342744', 'emailtomdismailhossain@gmail.com', null, null, true, true, 'Ismail', null, null, '$2a$12$U9lRuf8d7WFXI1ViAaF4AOJqky08fbm7UF27HuHSSX0Oakohf1KAe', null, null, false, null, '$2a$12$Fbu7eyNbPO2g5FjFyGqaJO3RWX/i/eyFey3EgszGiirIdmXasNjdS', '2026-05-15 14:41:27.367926', null, null, null, '2026-04-15 14:41:27.368485', 'Hossain');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (11, null, '2026-04-15 14:11:56.264058', 'hasanurrahmansofwat@gmail.com', 'fe5f98ef-33ca-43ec-be99-27374c0eac01', '2026-04-16 14:11:56.263745', true, true, 'Kazi Hasanur Rahman', null, null, '$2a$12$fyrbAgnocgJcDs66SmU/DOkjYSKf9jidi/j8p3ThrP0o6Rs7rFhSS', null, null, false, null, '$2a$12$t6yjixN/60/VsuSomvFTVuouuRCz4AzS6INbEsnWcppc6XiHGapum', '2026-05-15 14:58:44.524458', null, null, null, '2026-04-15 14:58:44.526700', 'Sofwat');
insert into public.users (id, bio, created_at, email, email_verification_token, email_verification_token_expiry, email_verified, enabled, full_name, gender, hobby, password, password_reset_token, password_reset_token_expiry, password_reset_token_used, profile_image_url, refresh_token, refresh_token_expiry, registration_number, roll_number, university, updated_at, username) values (17, null, '2026-04-15 14:19:54.577250', 'dian.bit3d@gmail.com', null, null, true, true, 'Asef Zaman Dian', null, null, '$2a$12$RxLGfv7SCSAnjxh0WGliWOENiNhPVkWbD/PMyjt0yRpveIxZXDNbi', null, null, false, null, '$2a$12$ijARd0fghQS66MDbm7ElEukWYqM/a2OAj7k1ckunj1xmmY.BdrKqy', '2026-05-15 14:26:40.325596', null, null, null, '2026-04-15 14:26:40.326201', 'bitslay');

insert into public.user_roles (user_id, role_id) values (3, 2);
insert into public.user_roles (user_id, role_id) values (3, 1);
insert into public.user_roles (user_id, role_id) values (4, 1);
insert into public.user_roles (user_id, role_id) values (5, 1);
insert into public.user_roles (user_id, role_id) values (6, 1);
insert into public.user_roles (user_id, role_id) values (7, 1);
insert into public.user_roles (user_id, role_id) values (11, 1);
insert into public.user_roles (user_id, role_id) values (12, 1);
insert into public.user_roles (user_id, role_id) values (13, 1);
insert into public.user_roles (user_id, role_id) values (8, 1);
insert into public.user_roles (user_id, role_id) values (9, 1);
insert into public.user_roles (user_id, role_id) values (10, 1);
insert into public.user_roles (user_id, role_id) values (14, 1);
insert into public.user_roles (user_id, role_id) values (16, 1);
insert into public.user_roles (user_id, role_id) values (17, 1);
insert into public.user_roles (user_id, role_id) values (15, 1);
insert into public.user_roles (user_id, role_id) values (18, 1);

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM contests) THEN
            INSERT INTO contests (
                name,
                description,
                contest_date,
                registration_start,
                registration_end,
                contest_start,
                contest_end,
                status
            )
            SELECT
                ('Contest ' || i) AS name,
                ('Description for contest ' || i) AS description,

                -- contest_date == contest_start (used by UI)
                CASE
                    WHEN i <= 15 THEN NOW() - INTERVAL '1 day'
                    WHEN i <= 25 THEN NOW() - INTERVAL '10 days'
                    WHEN i <= 35 THEN NOW() + INTERVAL '1 day'
                    ELSE NOW() + INTERVAL '10 days'
                    END AS contest_date,

                CASE
                    WHEN i <= 15 THEN NOW() - INTERVAL '2 days'
                    WHEN i <= 25 THEN NOW() - INTERVAL '20 days'
                    WHEN i <= 35 THEN NOW() - INTERVAL '1 day'
                    ELSE NOW() + INTERVAL '5 days'
                    END AS registration_start,

                CASE
                    WHEN i <= 15 THEN NOW() - INTERVAL '1 day'
                    WHEN i <= 25 THEN NOW() - INTERVAL '15 days'
                    WHEN i <= 35 THEN NOW() + INTERVAL '2 hours'
                    ELSE NOW() + INTERVAL '6 days'
                    END AS registration_end,

                CASE
                    WHEN i <= 15 THEN NOW() - INTERVAL '1 day'
                    WHEN i <= 25 THEN NOW() - INTERVAL '10 days'
                    WHEN i <= 35 THEN NOW() + INTERVAL '1 day'
                    ELSE NOW() + INTERVAL '10 days'
                    END AS contest_start,

                CASE
                    WHEN i <= 15 THEN NOW() + INTERVAL '1 day'
                    WHEN i <= 25 THEN NOW() - INTERVAL '5 days'
                    WHEN i <= 35 THEN NOW() + INTERVAL '2 days'
                    ELSE NOW() + INTERVAL '11 days'
                    END AS contest_end,

                'UPCOMING' AS status
            FROM generate_series(1, 200) AS g(i);
        END IF;
    END $$;

TRUNCATE TABLE public.contests cascade ;


INSERT INTO users (
    id, bio, created_at, email, email_verification_token,
    email_verification_token_expiry, email_verified, enabled,
    full_name, gender, hobby, password, password_reset_token,
    password_reset_token_expiry, password_reset_token_used,
    profile_image_url, refresh_token, refresh_token_expiry,
    registration_number, roll_number, university, updated_at, username
) VALUES
      (2, 'Cloning Life', '2026-04-20 21:01:38', 'alamparvej2024@gmail.com', '94b361cf-c835-4a1d-a63d-0497fe1ab988', '2026-04-21 21:01:38', TRUE, TRUE, 'M Parvej Alam', 'Male', 'Nothing', '$2a$12$RAumBOcOkBmi.Ps51fQ4n.KgXL0YbdT1c8RUZKe3iV0QzSvkba6l.', NULL, NULL, FALSE, NULL, '20d54b67-6ba4-4e2b-86db-accd905ccccf', '2026-04-23 00:17:16', NULL, NULL, 'CUET', '2026-04-25 17:28:40', 'parvej'),
      (3, NULL, '2026-04-21 10:03:11', 'sarker.arnab09@gmail.com', '22207992-b69a-48e1-88c4-90df393be950', '2026-04-22 10:03:11', TRUE, TRUE, 'Arnab Sarker', NULL, NULL, '$2a$12$M/JxJPQpmPstHShVlgBIguL0kJoLFFgheOgAdzvPJCMnQGr.DofIC', NULL, NULL, FALSE, NULL, '34ccab88-47bc-4cb7-a3cc-dcece58bc8ee', '2026-04-21 11:04:35', NULL, NULL, NULL, '2026-04-21 10:35:44', 'Franky3'),
      (4, NULL, '2026-04-14 23:49:39', 'softbot264@gmail.com', NULL, NULL, TRUE, TRUE, 'Shahnewaj Hridoy', NULL, NULL, '$2a$12$uTZobrkxsLd.zx43b9z/3OGAdu7.q1S66YY9WgT82rSN9CI/5vhrq', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-24 12:11:25', 'Hridoy8'),
      (5, NULL, '2026-04-21 07:50:46', 'atif.7484.drmc@gmail.com', '71c91c08-f383-4b7f-a070-f0c57f4fa16a', '2026-04-22 07:50:46', TRUE, TRUE, 'Atif Hossain', 'Male', NULL, '$2a$12$BnZNVmt0MInQJXJfO3dIg.BKUcUIktTTRg4SD91fYW1YAzrG8Ee1.', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-21 07:54:10', 'pLexii5'),
      (7, NULL, '2026-04-22 07:29:01', 'ahnafmanan2006@gmail.com', '3cea7c2a-1d26-4f21-a621-9cbdb30cb047', '2026-04-23 13:29:01', TRUE, TRUE, 'Ahnaf Tahmid Manan', 'Male', NULL, '$2a$12$6ivLEAF6wrkbvrkhI1kaJO01bQE6598rfPm/QvZwnoYNDzgpQ2Fh2', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 07:35:30', 'Manan22'),
      (8, NULL, '2026-04-15 14:09:53', 'nahamed343@gmail.com', '2633dce2-05bd-40dc-a04b-0de8b921807a', '2026-04-16 14:09:53', TRUE, TRUE, 'fida nahal ahamed', NULL, NULL, '$2a$12$XLx48pxGRl35Qtk6vwihVe1DXI9prbLgaRkIAQLwUKxbmzZf9lEIG', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-15 14:16:19', 'nahal11'),
      (11, NULL, '2026-04-15 14:11:56', 'hasanurrahmansofwat@gmail.com', 'fe5f98ef-33ca-43ec-be99-27374c0eac01', '2026-04-16 14:11:56', TRUE, TRUE, 'Kazi Hasanur Rahman', NULL, NULL, '$2a$12$fyrbAgnocgJcDs66SmU/DOkjYSKf9jidi/j8p3ThrP0o6Rs7rFhSS', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-15 14:58:44', 'Sofwat1'),
      (12, NULL, '2026-04-21 11:37:16', 'mdemon-2023716388@rme.du.ac.bd', '66e3ac92-4c3c-4578-988f-d4db0959fd3b', '2026-04-22 17:37:16', TRUE, TRUE, 'Md Emon Sardar', NULL, NULL, '$2a$12$3zSBNEThW9ymL5W7ytboz.OLnCcvqwv2LuoAPZokqNZNKba1dxgHm', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 07:29:39', 'Emon12'),
      (13, NULL, '2026-04-15 14:13:09', 'hello.adiba.82@gmail.com', '7eb08e8c-4dac-4850-881b-0a160267173e', '2026-04-16 14:13:09', TRUE, TRUE, 'Adiba Nur Raisa', NULL, NULL, '$2a$12$ybUtnaQ7GYGQpizbkiuV4useALLw8ZpRnxBjpkxv1tHN7HsMEJurG', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-15 14:13:42', 'Adiba19'),
      (14, NULL, '2026-04-15 14:17:43', 'sshupoma885@gmail.com', NULL, NULL, TRUE, TRUE, 'Upoma', NULL, NULL, '$2a$12$euKNmlEoHxg7n54ujLzK4.hb.h2O8swabo4D0ih3WU2MmL/FAfYD.', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 09:00:07', 'noobcad31'),
      (15, NULL, '2026-04-15 14:18:05', 'tigersakib2004@gmail.com', 'b523328b-9d75-472d-be3f-410999cc1674', '2026-04-16 14:18:05', TRUE, TRUE, 'sakib', NULL, NULL, '$2a$12$O7cxOdL.0/ZH8xz3rbIqmOx17p09sm9hFiIIsBnW3h.m4PZwSfyu2', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 05:15:52', 'sakib6932'),
      (16, NULL, '2026-04-15 14:18:32', 'himi19122005@gmail.com', NULL, NULL, TRUE, TRUE, 'Sabah hussain', NULL, NULL, '$2a$12$4U.Y2ESyMTW0Q0kcR1wI8.KWJcNyeDvsEdJVfOc6gNOfEYR/1OYzW', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-15 14:19:13', 'Himi25'),
      (17, NULL, '2026-04-15 14:19:54', 'dian.bit3d@gmail.com', NULL, NULL, TRUE, TRUE, 'Asef Zaman Dian', 'Male', NULL, '$2a$12$RxLGfv7SCSAnjxh0WGliWOENiNhPVkWbD/PMyjt0yRpveIxZXDNbi', NULL, NULL, FALSE, NULL, NULL, NULL, '28RMEDU', NULL, NULL, '2026-04-22 08:24:21', 'bitslay23'),
      (18, NULL, '2026-04-21 12:43:07', 'ragson700@gmail.com', '66d05eed-3f60-4ca2-9780-669357a2102f', '2026-04-22 18:43:07', TRUE, TRUE, 'Ragib', NULL, NULL, '$2a$12$SCRkOFBkreoUvoklTjWAiuaSDcpmb2z6x.46Y.ncJMyuR44PLcl3yb', NULL, NULL, FALSE, NULL, '25bab9a-fdc2-440e-99b8-c83f5cd0480e', '2026-04-21 19:43:32', NULL, NULL, NULL, '2026-04-21 15:50:33', 'Hasan7'),
      (19, NULL, '2026-04-21 08:49:36', 'nusratislampriya2006@gmail.com', '32c63eb6-c6fc-445e-8f40-f7200237bdac', '2026-04-22 08:49:36', TRUE, TRUE, 'Nusrat Islam', 'Female', NULL, '$2a$12$3HcDjyjWuXcp55B5SAhKdugOYebNNlTj/4a4FFIPtSI7m7QMazT4e', NULL, NULL, FALSE, NULL, NULL, NULL, 'SK-172-002', NULL, 'University of Dhaka', '2026-04-21 08:53:34', 'nusratislam19'),
      (20, NULL, '2026-04-21 10:31:48', 'arnabsarker001@gmail.com', 'l3be91a8b-0767-412f-b865-c510e0d30573', '2026-04-22 10:31:48', TRUE, TRUE, 'Arnab Sarker', NULL, NULL, '$2a$12$21TEoLKnaDBtJwvn70qtZeMgcI3BRW6X1EjD/I8UFdaAO8itdzYL2', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-21 10:33:38', 'Luffy20'),
      (24, NULL, '2026-04-21 08:16:46', 'user1@gmail.com', NULL, NULL, TRUE, TRUE, 'User 1', NULL, NULL, '$2a$12$Wdw8DEeuktaefmBT5ZW3.eH2JpaJDIbm1Im8J7t/t3DZfEsgN.gDW', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-21 09:32:53', 'user124'),
      (26, NULL, '2026-04-15 12:12:05', 'samianasa2030@gmail.com', NULL, NULL, TRUE, TRUE, 'Samia Islam', 'Other', 'Sleeping', '$2a$12$3.JRkn0wllUhlTCoPtGRs.m9mwR.B//b5dOW6K65kEhIrmdbsN2sO', NULL, NULL, FALSE, NULL, NULL, NULL, '20231163754', NULL, 'Dhaka University', '2026-04-15 14:16:31', 'Idk_shit'),
      (27, NULL, '2026-04-21 13:15:30', 'sesnabid@gmail.com', '189a074f-ad07-4ce9-8d9b-dddb033e3482', '2026-04-22 19:15:30', TRUE, TRUE, 'Shams-E_Sheefat Nabid', NULL, NULL, '$2a$12$0KtSzArBjSof3apPAZDcFuUBoQPfLkwZ7xD0.waN0Cti2ImF79vaae', NULL, NULL, FALSE, NULL, '05ed094-44a7-4843-9c57-2c4e3dbcc706', '2026-04-21 20:16:11', NULL, NULL, NULL, '2026-04-21 15:50:42', 'nub-id27'),
      (28, NULL, '2026-04-15 00:11:44', 'shahnewaj-2023616389@rme.du.ac.bd', '588f3c16-cc61-49c4-bf52-64c217b5e999', '2026-04-16 00:11:44', TRUE, TRUE, 'Shahnewaj', NULL, NULL, '$2a$12$cu3fq1D.Zb1VuLwfH6iU3OJLajCaggrTjjYmeyhfTC8nftd3oNC/i', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-21 12:14:36', 'Shahnewaj28'),
      (29, NULL, '2026-04-22 07:27:15', 'mirzapriyontee12@gmail.com', '6a649d24-930f-4d99-9c05-efb7c0d1f0e1', '2026-04-23 13:27:15', TRUE, TRUE, 'Mirza Lamiya Hasan Priyontee', NULL, NULL, '$2a$12$MPGrqDtFmeiCIZEOqQgHpuXh5F9J7q17cpaeJkH7hMyO9v1ba8CI2', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 07:29:08', 'Priyontee29'),
      (30, NULL, '2026-04-15 14:12:24', 'aadritomaitra@gmail.com', 'bb99cb02-20e6-4401-a33c-73d9a702cb18', '2026-04-16 14:12:24', TRUE, TRUE, 'Aadrito Maitra', NULL, NULL, '$2a$12$dVQzdGg3VFEtuVwBWmucnuC3JVbobhzi5yeoN2EBCs5Qr0V8prHkK', NULL, NULL, FALSE, NULL, '77c596c8-1762-4830-8bdc-21a94f32bf0a', '2026-04-22 14:33:34', NULL, NULL, NULL, '2026-04-22 07:33:34', 'ghum30'),
      (31, NULL, '2026-04-15 12:38:31', 'rishadrahman694@gmail.com', NULL, NULL, TRUE, TRUE, 'Rishad', NULL, NULL, '$2a$12$EQbUGUFnW2mabh34axZoQ.8zLXAMJWAfHxc70z1U/qg5Z8qDAEElO', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 08:11:05', 'Rishad316'),
      (32, NULL, '2026-04-21 21:33:02', 'shinghoamit548@gmail.com', '4f0a3016-b449-423d-8ce2-330da12cb02f', '2026-04-23 03:33:02', TRUE, TRUE, 'Amit', NULL, NULL, '$2a$12$Y/V7OhAcMNW4ZCCsu12mE.8yA4LMKDNXGZO7VMUgkaPwJ4Oxd5PrO', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 02:03:57', 'Kumar shingha'),
      (33, NULL, '2026-04-21 18:24:59', 'tasinmuhtadimahi@gmail.com', 'bd7dc5e4-9dc5-4044-8370-ebf7411e40b2', '2026-04-23 00:24:59', TRUE, TRUE, 'tasin muhtadi mahi', NULL, NULL, '$2a$12$GTy1ib/iC5DErqx3CD6aZeBDdqlnHNhEBUlKodaST790pGOwZwZS.', NULL, NULL, FALSE, NULL, '10dba0e0-df79-48d6-8af4-980792fb4c4a', '2026-04-22 01:25:26', NULL, NULL, NULL, '2026-04-22 07:29:55', 'meow33'),
      (34, NULL, '2026-04-22 07:36:43', 'pareidoliaxghum@gmail.com', '7c19b247-970b-41c7-b7ba-fa61fff91894', '2026-04-23 13:36:43', TRUE, TRUE, 'Aadrito Maitra', NULL, NULL, '$2a$12$YljAam1SoWqeRpgGzq.0Fu335SGb3jUTvRlDT0Gz8bLw9iLERR7ny', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 07:37:18', 'Ghum34'),
      (35, NULL, '2026-04-22 07:29:38', 'ragson300@gmail.com', 'ada28e6c-ec25-47fe-8795-def7da7df5a7', '2026-04-23 13:29:38', TRUE, TRUE, 'Ragib', NULL, NULL, '$2a$12$60RF.6s5xkjNF94v6egb4.1eCGO8wT2a72sIHa9dWTpYrJ3ppDzgu', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 07:30:07', 'Hasan35'),
      (36, NULL, '2026-04-22 07:36:42', 'samiarana5024@gmail.com', '8be0ff16-e6c7-41f9-81c7-62b9863f7e1f', '2026-04-23 13:36:42', TRUE, TRUE, 'samia binte rana', NULL, NULL, '$2a$12$Y1q5cFhiEEi2vAFpHAM4DO0tgX9q84CnGxsLoswY0o6tscOgT80GC', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-22 07:38:15', 'sajuti36'),
      (37, NULL, '2026-04-23 11:34:50', 'naimrupok88@gmail.com', 'f544664e-4291-424a-8d80-34d0243fb7ee', '2026-04-24 17:34:50', TRUE, TRUE, 'Naim', NULL, NULL, '$2a$12$o3HmDCeEB0MfP2QYqP92N.5fUsZxYXcJ.Djg20PZeGdnXkytxdq2K', NULL, NULL, FALSE, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-23 11:35:12', 'Rupok')

ON CONFLICT (id) DO NOTHING;

-- Synchronize the ID sequence after manual ID insertion
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));