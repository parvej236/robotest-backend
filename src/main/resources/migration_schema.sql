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
            INSERT INTO questions (
                contest_id,
                description,
                image_url,
                video_url,
                type,
                correct_answer,
                error_percentage,
                custom_answer_key,
                order_index,
                points
            )
            SELECT
                c.id AS contest_id,
                ('Question for contest ' || c.id || ' #' || q.idx) AS description,
                NULL AS image_url,
                NULL AS video_url,

                CASE WHEN (q.idx % 2) = 0 THEN 'NUMERIC_MCQ' ELSE 'CUSTOM' END AS type,

                CASE WHEN (q.idx % 2) = 0 THEN (50.0 + q.idx) ELSE NULL END AS correct_answer,
                CASE WHEN (q.idx % 2) = 0 THEN 5.0 ELSE NULL END AS error_percentage,
                CASE WHEN (q.idx % 2) = 1 THEN ('ANSWER_' || c.id || '_' || q.idx) ELSE NULL END AS custom_answer_key,

                q.idx AS order_index,
                10 AS points
            FROM ins_contests c
                     CROSS JOIN generate_series(1, 5) AS q(idx);
        END IF;
    END $$;
