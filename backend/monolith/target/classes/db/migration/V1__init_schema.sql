CREATE TABLE IF NOT EXISTS users (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username        VARCHAR(64)     NOT NULL,
  display_name    VARCHAR(128)    NOT NULL,
  email           VARCHAR(255)    NULL,
  password_hash   VARCHAR(255)    NULL,
  avatar_url      TEXT            NULL,
  provider        VARCHAR(32)     NULL,
  provider_id     VARCHAR(128)    NULL,
  status          VARCHAR(32)     NOT NULL DEFAULT 'active',
  bio             TEXT            NULL,
  last_login_at   DATETIME        NULL,
  metadata        JSON            NOT NULL,
  created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at      DATETIME        NULL,
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_provider (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS roles (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  code        VARCHAR(64)     NOT NULL,
  name        VARCHAR(128)    NOT NULL,
  description TEXT            NULL,
  metadata    JSON            NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at  DATETIME        NULL,
  UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS permissions (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  code        VARCHAR(128)    NOT NULL,
  name        VARCHAR(128)    NOT NULL,
  module      VARCHAR(64)     NOT NULL,
  description TEXT            NULL,
  UNIQUE KEY uk_permissions_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id     BIGINT UNSIGNED NOT NULL,
  role_id     BIGINT UNSIGNED NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS role_permissions (
  role_id       BIGINT UNSIGNED NOT NULL,
  permission_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categories (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(64)     NOT NULL,
  slug        VARCHAR(64)     NOT NULL,
  description TEXT            NULL,
  parent_id   BIGINT UNSIGNED NULL,
  sort_order  INT             NOT NULL DEFAULT 0,
  post_count  INT             NOT NULL DEFAULT 0,
  metadata    JSON            NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at  DATETIME        NULL,
  UNIQUE KEY uk_categories_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tags (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(64)     NOT NULL,
  slug         VARCHAR(64)     NOT NULL,
  description  TEXT            NULL,
  color        VARCHAR(16)     NULL,
  usage_count  INT             NOT NULL DEFAULT 0,
  metadata     JSON            NOT NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at   DATETIME        NULL,
  UNIQUE KEY uk_tags_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS articles (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  author_id           BIGINT UNSIGNED NOT NULL,
  category_id         BIGINT UNSIGNED NULL,
  title               VARCHAR(255)    NOT NULL,
  slug                VARCHAR(255)    NOT NULL,
  summary             TEXT            NULL,
  cover_attachment_id BIGINT UNSIGNED NULL,
  content_md          MEDIUMTEXT      NOT NULL,
  content_html        MEDIUMTEXT      NULL,
  content_text        MEDIUMTEXT      NULL,
  status              VARCHAR(32)     NOT NULL DEFAULT 'draft',
  visibility          VARCHAR(32)     NOT NULL DEFAULT 'public',
  pinned              TINYINT(1)      NOT NULL DEFAULT 0,
  published_at        DATETIME        NULL,
  reading_minutes     INT             NOT NULL DEFAULT 0,
  word_count          INT             NOT NULL DEFAULT 0,
  view_count          BIGINT          NOT NULL DEFAULT 0,
  like_count          BIGINT          NOT NULL DEFAULT 0,
  comment_count       BIGINT          NOT NULL DEFAULT 0,
  github_repo         VARCHAR(255)    NULL,
  github_commit_sha   VARCHAR(64)     NULL,
  version             INT             NOT NULL DEFAULT 1,
  metadata            JSON            NOT NULL,
  created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at          DATETIME        NULL,
  UNIQUE KEY uk_articles_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS article_versions (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  article_id  BIGINT UNSIGNED NOT NULL,
  version     INT             NOT NULL,
  title       VARCHAR(255)    NOT NULL,
  content_md  MEDIUMTEXT      NOT NULL,
  content_html MEDIUMTEXT     NULL,
  change_note TEXT            NULL,
  created_by  BIGINT UNSIGNED NOT NULL,
  metadata    JSON            NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS article_tags (
  article_id  BIGINT UNSIGNED NOT NULL,
  tag_id      BIGINT UNSIGNED NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (article_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS snippets (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  author_id        BIGINT UNSIGNED NOT NULL,
  title            VARCHAR(255)    NOT NULL,
  slug             VARCHAR(255)    NOT NULL,
  language         VARCHAR(64)     NOT NULL,
  code             MEDIUMTEXT      NOT NULL,
  highlighted_html MEDIUMTEXT      NULL,
  description_md   TEXT            NULL,
  description_html TEXT            NULL,
  visibility       VARCHAR(32)     NOT NULL DEFAULT 'public',
  raw_token        VARCHAR(128)    NULL,
  view_count       BIGINT          NOT NULL DEFAULT 0,
  copy_count       BIGINT          NOT NULL DEFAULT 0,
  metadata         JSON            NOT NULL,
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at       DATETIME        NULL,
  UNIQUE KEY uk_snippets_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notes (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  author_id    BIGINT UNSIGNED NOT NULL,
  content_md   TEXT            NOT NULL,
  content_html TEXT            NULL,
  content_text TEXT            NULL,
  status       VARCHAR(32)     NOT NULL DEFAULT 'draft',
  visibility   VARCHAR(32)     NOT NULL DEFAULT 'public',
  published_at DATETIME        NULL,
  like_count   BIGINT          NOT NULL DEFAULT 0,
  metadata     JSON            NOT NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at   DATETIME        NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS projects (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  owner         VARCHAR(128)    NOT NULL,
  repo          VARCHAR(128)    NOT NULL,
  name          VARCHAR(255)    NOT NULL,
  description   TEXT            NULL,
  homepage_url  TEXT            NULL,
  github_url    TEXT            NULL,
  language      VARCHAR(64)     NULL,
  stars         INT             NOT NULL DEFAULT 0,
  forks         INT             NOT NULL DEFAULT 0,
  open_issues   INT             NOT NULL DEFAULT 0,
  license       VARCHAR(128)    NULL,
  pushed_at     DATETIME        NULL,
  synced_at     DATETIME        NULL,
  sync_status   VARCHAR(32)     NOT NULL DEFAULT 'ok',
  pinned        TINYINT(1)      NOT NULL DEFAULT 0,
  sort_order    INT             NOT NULL DEFAULT 0,
  metadata      JSON            NOT NULL,
  created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at    DATETIME        NULL,
  UNIQUE KEY uk_projects_owner_repo (owner, repo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comments (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  target_type      VARCHAR(32)     NOT NULL,
  target_id        BIGINT UNSIGNED NOT NULL,
  parent_id        BIGINT UNSIGNED NULL,
  root_id          BIGINT UNSIGNED NULL,
  path             TEXT            NULL,
  depth            INT             NOT NULL DEFAULT 0,
  user_id          BIGINT UNSIGNED NULL,
  nickname         VARCHAR(64)     NULL,
  email_hash       VARCHAR(128)    NULL,
  website          TEXT            NULL,
  content_md       TEXT            NOT NULL,
  content_html     TEXT            NULL,
  status           VARCHAR(32)     NOT NULL DEFAULT 'pending',
  ip_hash          VARCHAR(128)    NULL,
  user_agent_hash  VARCHAR(128)    NULL,
  like_count       BIGINT          NOT NULL DEFAULT 0,
  reply_count      INT             NOT NULL DEFAULT 0,
  metadata         JSON            NOT NULL,
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at       DATETIME        NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS likes (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  target_type      VARCHAR(32)     NOT NULL,
  target_id        BIGINT UNSIGNED NOT NULL,
  user_id          BIGINT UNSIGNED NULL,
  fingerprint_hash VARCHAR(128)    NULL,
  ip_hash          VARCHAR(128)    NULL,
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       DATETIME        NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS attachments (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  uploader_id  BIGINT UNSIGNED NOT NULL,
  filename     VARCHAR(255)    NOT NULL,
  object_key   TEXT            NOT NULL,
  url          TEXT            NOT NULL,
  mime_type    VARCHAR(128)    NOT NULL,
  size_bytes   BIGINT          NOT NULL,
  width        INT             NULL,
  height       INT             NULL,
  sha256       VARCHAR(128)    NULL,
  status       VARCHAR(32)     NOT NULL DEFAULT 'ready',
  metadata     JSON            NOT NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at   DATETIME        NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS friend_links (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(128)    NOT NULL,
  url          TEXT            NOT NULL,
  avatar_url   TEXT            NULL,
  description  TEXT            NULL,
  owner_email  VARCHAR(255)    NULL,
  status       VARCHAR(32)     NOT NULL DEFAULT 'pending',
  sort_order   INT             NOT NULL DEFAULT 0,
  metadata     JSON            NOT NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at   DATETIME        NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS subscriptions (
  id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email             VARCHAR(255)    NOT NULL,
  status            VARCHAR(32)     NOT NULL DEFAULT 'pending',
  confirm_token     VARCHAR(128)    NULL,
  unsubscribe_token VARCHAR(128)    NULL,
  metadata          JSON            NOT NULL,
  created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS system_settings (
  `key`        VARCHAR(128)    NOT NULL PRIMARY KEY,
  value        JSON            NOT NULL,
  description  TEXT            NULL,
  is_public    TINYINT(1)      NOT NULL DEFAULT 0,
  updated_by   BIGINT UNSIGNED NULL,
  updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_logs (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  actor_id    BIGINT UNSIGNED NULL,
  action      VARCHAR(128)    NOT NULL,
  target_type VARCHAR(64)     NULL,
  target_id   BIGINT UNSIGNED NULL,
  ip_hash     VARCHAR(128)    NULL,
  metadata    JSON            NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS github_sync_logs (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  project_id    BIGINT UNSIGNED NOT NULL,
  status        VARCHAR(32)     NOT NULL,
  request_count INT             NOT NULL DEFAULT 0,
  error_message TEXT            NULL,
  metadata      JSON            NOT NULL,
  created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS search_documents (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  target_type VARCHAR(32)     NOT NULL,
  target_id   BIGINT UNSIGNED NOT NULL,
  title       VARCHAR(255)    NOT NULL,
  content     MEDIUMTEXT      NOT NULL,
  tags        JSON            NULL,
  status      VARCHAR(32)     NOT NULL DEFAULT 'active',
  boost       FLOAT           NOT NULL DEFAULT 1.0,
  metadata    JSON            NOT NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
