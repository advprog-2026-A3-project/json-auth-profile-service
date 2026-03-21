-- V1__Init_schema.sql

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    phone_number VARCHAR(20),
    bio TEXT,
    social_media_link VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'TITIPERS',
    kyc_status VARCHAR(50) NOT NULL DEFAULT 'NONE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS profiles (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL UNIQUE,
                                        username VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    successful_transactions INT DEFAULT 0,
    failed_transactions INT DEFAULT 0,
    rating DOUBLE PRECISION DEFAULT 0.0,
    CONSTRAINT fk_profiles_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS kyc_requests (
                                            id BIGSERIAL PRIMARY KEY,
                                            user_id BIGINT NOT NULL,
                                            full_name VARCHAR(255) NOT NULL,
    id_card_data TEXT,
    social_media_link VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMP,
    review_notes TEXT,
    CONSTRAINT fk_kyc_requests_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_username ON profiles(username);
CREATE INDEX idx_kyc_requests_user_id ON kyc_requests(user_id);
CREATE INDEX idx_kyc_requests_status ON kyc_requests(status);

INSERT INTO users (email, username, password, display_name, role, kyc_status)
VALUES (
           'admin@json.app',
           'admin',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'System Admin',
           'ADMIN',
           'APPROVED'
       ) ON CONFLICT DO NOTHING;