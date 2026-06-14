-- Ensure the `role` column exists on `users` table and default to 'USER'
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(30) NOT NULL DEFAULT 'USER';

-- Safe update: set role to 'USER' where NULL
UPDATE users
SET role = 'USER'
WHERE role IS NULL;

-- Optional: remove legacy user_roles table if present (keeps schema-role-migration logic)
DROP TABLE IF EXISTS user_roles;
