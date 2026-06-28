ALTER TABLE snippets
    ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0 AFTER copy_count;
