-- Performance indexes (Phase 1 / audit remediation)

CREATE INDEX idx_article_tags_tag ON article_tags (tag_id, article_id);

CREATE UNIQUE INDEX uk_search_documents_target ON search_documents (target_type, target_id);

CREATE UNIQUE INDEX uk_subscriptions_email ON subscriptions (email);

CREATE INDEX idx_subscriptions_confirm_token ON subscriptions (confirm_token);

CREATE INDEX idx_subscriptions_unsubscribe_token ON subscriptions (unsubscribe_token);

CREATE INDEX idx_article_versions_article ON article_versions (article_id, version DESC);

CREATE INDEX idx_attachments_uploader ON attachments (uploader_id, created_at DESC);
