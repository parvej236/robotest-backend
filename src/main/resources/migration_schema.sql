-- 1. Create the Table
CREATE TABLE IF NOT EXISTS rulebook (
                                        id BIGINT PRIMARY KEY,
                                        sections JSONB NOT NULL,
                                        metadata JSONB NOT NULL,
                                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Comprehensive Initialization from Official Rulebook
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