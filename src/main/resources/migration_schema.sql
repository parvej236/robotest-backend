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