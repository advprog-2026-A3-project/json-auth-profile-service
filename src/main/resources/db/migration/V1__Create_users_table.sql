CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    bio TEXT,
    social_media_link VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'TITIPERS',
    kyc_status VARCHAR(50) NOT NULL DEFAULT 'NONE',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- Seed admin account (password: admin123)
INSERT INTO users (email, username, password, full_name, role, kyc_status)
VALUES (
           'admin@json.app',
           'admin',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'System Admin',
           'ADMIN',
           'APPROVED'
       ) ON CONFLICT DO NOTHING;