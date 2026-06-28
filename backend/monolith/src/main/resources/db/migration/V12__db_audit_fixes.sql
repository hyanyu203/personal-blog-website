-- V12: DB audit fixes (2026-06-28)
-- Addresses: H-DB-1, H-DB-2, H-DB-3, M-DB-1, M-DB-2, M-DB-4, L-DB-1, L-DB-2, L-DB-3

-- ============================================================
-- H-DB-1: likes — add dedup constraints and query indexes
-- ============================================================
-- The likes table stores persistent like history; dedup is also enforced
-- via Redis (LikeCounterService.incrementByUser), but the DB layer had no
-- constraints, so a Redis eviction or race could produce duplicate rows.

-- User-based dedup: one active like per (target, user).
ALTER TABLE likes
    ADD COLUMN user_like_key VARCHAR(200)
        AS (IF(deleted_at IS NULL AND user_id IS NOT NULL,
               CONCAT(target_type, '|', target_id, '|', user_id),
               NULL)) STORED,
    ADD UNIQUE INDEX uk_likes_user_active (user_like_key);

-- Fingerprint-based dedup: one active like per (target, fingerprint).
ALTER TABLE likes
    ADD COLUMN fp_like_key VARCHAR(200)
        AS (IF(deleted_at IS NULL AND fingerprint_hash IS NOT NULL,
               CONCAT(target_type, '|', target_id, '|', fingerprint_hash),
               NULL)) STORED,
    ADD UNIQUE INDEX uk_likes_fp_active (fp_like_key);

-- Query indexes: "has user X liked target Y?" / "count likes for target Y"
CREATE INDEX idx_likes_target_user ON likes (target_type, target_id, user_id,          deleted_at);
CREATE INDEX idx_likes_target_fp   ON likes (target_type, target_id, fingerprint_hash, deleted_at);

-- ============================================================
-- H-DB-2: webmentions — prevent duplicate source→target rows
-- ============================================================
-- source_url and target_url are TEXT; use a generated VARCHAR(512) key.
ALTER TABLE webmentions
    ADD COLUMN source_target_key VARCHAR(512)
        AS (CONCAT(LEFT(source_url, 255), '|', LEFT(target_url, 255))) STORED,
    ADD UNIQUE INDEX uk_webmentions_source_target (source_target_key);

-- ============================================================
-- H-DB-3: projects — soft-delete-aware unique constraint
-- ============================================================
-- V10 fixed users and articles but missed projects.
ALTER TABLE projects DROP INDEX uk_projects_owner_repo;
ALTER TABLE projects
    ADD COLUMN owner_repo_active VARCHAR(260)
        AS (IF(deleted_at IS NULL, CONCAT(owner, '/', repo), NULL)) STORED,
    ADD UNIQUE INDEX uk_projects_owner_repo_active (owner_repo_active);

-- ============================================================
-- M-DB-1: comments — missing query indexes
-- ============================================================
-- "All comments by user X" and "all replies in thread T" were full scans.
CREATE INDEX idx_comments_user ON comments (user_id, created_at DESC, deleted_at);
CREATE INDEX idx_comments_root ON comments (root_id,  depth,          created_at);

-- ============================================================
-- M-DB-2: attachments — sha256 dedup + unique constraint
-- ============================================================
-- Remove pre-existing duplicates (keep earliest row per sha256).
DELETE a1
FROM attachments a1
INNER JOIN attachments a2
    ON a1.sha256 = a2.sha256
   AND a1.id > a2.id
WHERE a1.sha256 IS NOT NULL;

ALTER TABLE attachments
    ADD UNIQUE INDEX uk_attachments_sha256 (sha256);

-- ============================================================
-- M-DB-4: notes — add comment_count column and author index
-- ============================================================
-- articles tracks comment_count but notes did not, causing CommentService
-- to silently discard the count update for note comments.
ALTER TABLE notes
    ADD COLUMN comment_count BIGINT NOT NULL DEFAULT 0 AFTER like_count;

-- Backfill: count currently approved, non-deleted top-level comments per note.
UPDATE notes n
SET comment_count = (
    SELECT COUNT(*)
    FROM comments c
    WHERE c.target_type = 'note'
      AND c.target_id   = n.id
      AND c.parent_id   IS NULL
      AND c.status      = 'approved'
      AND c.deleted_at  IS NULL
)
WHERE n.deleted_at IS NULL;

CREATE INDEX idx_notes_author ON notes (author_id, created_at DESC, deleted_at);

-- ============================================================
-- L-DB-1: categories — index on parent_id
-- ============================================================
CREATE INDEX idx_categories_parent ON categories (parent_id);

-- ============================================================
-- L-DB-2: audit_logs — time-range index
-- ============================================================
CREATE INDEX idx_audit_logs_time ON audit_logs (created_at DESC);

-- ============================================================
-- L-DB-3: subscriptions — hash confirm_token in-place
-- ============================================================
-- confirm_token is one-time use (cleared after confirmation) but was stored
-- as a plain UUID. We now store SHA2-256 so a DB dump can't be used to
-- replay confirmation links.
--
-- unsubscribe_token is intentionally kept plaintext because it is embedded
-- in every newsletter email and must be readable to generate unsubscribe URLs.
-- Its threat model differs: it's already transmitted in plaintext in emails.
--
-- Migration strategy:
--   • Pending subscriptions: invalidate confirm_token (user must re-subscribe).
--     Their unsubscribe_token doesn't matter yet (they haven't confirmed).
--   • Confirmed / unsubscribed rows: confirm_token is already NULL — no action.
UPDATE subscriptions
SET confirm_token = NULL
WHERE status = 'pending';
-- After this migration the application will store SHA2(uuid, 256) for new
-- confirm_token values (see SubscriptionService.subscribe).
-- The column keeps the name confirm_token; semantics now mean "hex SHA-256 hash".
