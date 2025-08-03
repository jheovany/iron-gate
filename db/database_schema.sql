-- Crear base de datos
CREATE DATABASE irongate;

-- Usar la base de datos
\c irongate;

-- Crear tabla de usuarios (Spring Boot JPA la creará automáticamente, pero aquí está el esquema de referencia)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(is_enabled);

-- Insertar usuarios de prueba (las contraseñas están encriptadas con BCrypt)
-- Contraseña: "password123"
INSERT INTO users (email, password, first_name, last_name, role, is_enabled) VALUES
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', 'ADMIN', true),
('moderator@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Moderator', 'User', 'MODERATOR', true),
('user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Regular', 'User', 'USER', true)
ON CONFLICT (email) DO NOTHING;

-- Verificar que los usuarios se insertaron correctamente
SELECT id, email, first_name, last_name, role, is_enabled, created_at FROM users;