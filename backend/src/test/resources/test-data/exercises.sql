INSERT INTO exercise (name, reps, weight, execution, activity_id, death_stop)
VALUES
    -- Exercises for Morning Workout (activity_id = 1)
    ('Push-ups', 20, 0.0, 'Standard push-up form', 1, false),
    ('Squats', 25, 0.0, 'Deep squat with proper form', 1, false),
    ('Bench Press', 10, 80.0, 'Controlled movement, full range', 1, true),

    -- Exercises for Evening Run (activity_id = 2)
    ('Running', 1, 0.0, 'Steady pace jogging', 2, false),
    ('Cool-down Walk', 1, 0.0, 'Slow walking for recovery', 2, false),

    -- Exercises for Strength Training (activity_id = 3)
    ('Deadlifts', 8, 120.0, 'Proper deadlift technique', 3, true),
    ('Pull-ups', 12, 0.0, 'Full range of motion', 3, false),
    ('Overhead Press', 10, 60.0, 'Strict military press', 3, true);