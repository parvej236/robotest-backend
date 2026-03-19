-- =====================================================
--  ROBOTEST AUTH SYSTEM — PostgreSQL Schema
-- =====================================================

-- Run once:
-- createdb robotest_db
-- psql -d robotest_db -f schema.sql

CREATE TABLE IF NOT EXISTS roles (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id                               BIGSERIAL    PRIMARY KEY,
    full_name                        VARCHAR(255) NOT NULL,
    username                         VARCHAR(50)  UNIQUE NOT NULL,
    email                            VARCHAR(255) UNIQUE NOT NULL,
    password                         VARCHAR(255) NOT NULL,
    enabled                          BOOLEAN      DEFAULT FALSE,
    email_verified                   BOOLEAN      DEFAULT FALSE,

    email_verification_token         VARCHAR(255),
    email_verification_token_expiry  TIMESTAMP,

    password_reset_token             VARCHAR(255),
    password_reset_token_expiry      TIMESTAMP,
    password_reset_token_used        BOOLEAN      DEFAULT FALSE,

    refresh_token                    TEXT,
    refresh_token_expiry             TIMESTAMP,

    created_at                       TIMESTAMP    DEFAULT NOW(),
    updated_at                       TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_email                   ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username                ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email_verify_token      ON users(email_verification_token);
CREATE INDEX IF NOT EXISTS idx_users_password_reset_token    ON users(password_reset_token);

-- Seed roles (admin user is auto-created by DataInitializer.java on startup)
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN')
ON CONFLICT DO NOTHING;
