CREATE TABLE IF NOT EXISTS webmentions (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  source_url   TEXT            NOT NULL,
  target_url   TEXT            NOT NULL,
  type         VARCHAR(32)     NOT NULL DEFAULT 'mention',
  status       VARCHAR(32)     NOT NULL DEFAULT 'pending',
  verified_at  DATETIME        NULL,
  metadata     JSON            NOT NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_webmentions_target (target_url(255)),
  KEY idx_webmentions_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO system_settings (`key`, value, description, is_public, updated_at)
SELECT 'webmentionEnabled', 'false', '是否启用 Webmention 接收', 0, NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE `key` = 'webmentionEnabled');
