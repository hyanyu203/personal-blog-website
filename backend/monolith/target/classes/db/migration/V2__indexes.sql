-- V2 indexes (from sql/indexes.sql)

CREATE INDEX idx_articles_status_time ON articles (status, published_at DESC);
CREATE INDEX idx_articles_category ON articles (category_id);
CREATE INDEX idx_articles_author ON articles (author_id);
CREATE INDEX idx_articles_pinned ON articles (pinned, published_at, deleted_at);
CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_comments_target ON comments (target_type, target_id, status, deleted_at, created_at DESC);
CREATE INDEX idx_comments_parent ON comments (parent_id);
CREATE INDEX idx_snippets_language ON snippets (language, deleted_at);
CREATE INDEX idx_notes_status_time ON notes (status, published_at DESC, deleted_at);
CREATE INDEX idx_friend_links_status ON friend_links (status, sort_order, deleted_at);
CREATE INDEX idx_audit_logs_actor ON audit_logs (actor_id, created_at DESC);
CREATE INDEX idx_audit_logs_target ON audit_logs (target_type, target_id, created_at DESC);
CREATE INDEX idx_github_sync_project ON github_sync_logs (project_id, created_at DESC);
