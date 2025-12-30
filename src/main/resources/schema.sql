CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL,
                       full_name VARCHAR(100),
                       avatar_url TEXT,
                       phone VARCHAR(20),
                       is_verified BOOLEAN DEFAULT FALSE,
                       status VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, BANNED
                       provider VARCHAR(30) DEFAULT 'LOCAL', -- LOCAL, GOOGLE, FACEBOOK, GITHUB
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW(),
                       last_login TIMESTAMP,
                       last_login_ip VARCHAR(50)
);

CREATE TABLE user_roles (
                            user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL,
                       description TEXT,
                       created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE permissions (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(50) UNIQUE NOT NULL,
                             description TEXT,
                             created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE role_permissions (
                                  role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                  permission_id INT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
                                  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE login_logs (
                            id SERIAL PRIMARY KEY,
                            user_id INT REFERENCES users(id) ON DELETE CASCADE,
                            login_time TIMESTAMP DEFAULT NOW(),
                            ip_address VARCHAR(50),
                            user_agent TEXT,
                            success BOOLEAN
);
