-- Soft-delete aware unique constraints (MySQL generated columns; multiple NULLs allowed in UNIQUE)

ALTER TABLE users DROP INDEX uk_users_username;
ALTER TABLE users DROP INDEX uk_users_email;

ALTER TABLE users
    ADD COLUMN username_active VARCHAR(64) AS (IF(deleted_at IS NULL, username, NULL)) STORED,
    ADD COLUMN email_active VARCHAR(255) AS (IF(deleted_at IS NULL, email, NULL)) STORED,
    ADD UNIQUE INDEX uk_users_username_active (username_active),
    ADD UNIQUE INDEX uk_users_email_active (email_active);

ALTER TABLE articles DROP INDEX uk_articles_slug;

ALTER TABLE articles
    ADD COLUMN slug_active VARCHAR(255) AS (IF(deleted_at IS NULL, slug, NULL)) STORED,
    ADD UNIQUE INDEX uk_articles_slug_active (slug_active);

-- verification_codes was superseded by Redis-backed EmailCodeService (V7)
DROP TABLE IF EXISTS verification_codes;
