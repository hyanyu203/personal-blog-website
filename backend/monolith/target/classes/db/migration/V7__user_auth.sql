-- USER role for frontend registered users
INSERT INTO roles (code, name, description, metadata, created_at, updated_at)
SELECT 'USER', '注册用户', '前台注册用户', '{}', NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'USER' AND deleted_at IS NULL);

ALTER TABLE users
  ADD COLUMN email_verified TINYINT(1) NOT NULL DEFAULT 0 AFTER email,
  ADD COLUMN token_version INT NOT NULL DEFAULT 0 AFTER status;

UPDATE users SET email_verified = 1 WHERE email IS NOT NULL AND deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS verification_codes (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email      VARCHAR(255)    NOT NULL,
  code_hash  VARCHAR(64)     NOT NULL,
  purpose    VARCHAR(32)     NOT NULL,
  expires_at DATETIME        NOT NULL,
  used_at    DATETIME        NULL,
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_verification_email_purpose (email, purpose)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
