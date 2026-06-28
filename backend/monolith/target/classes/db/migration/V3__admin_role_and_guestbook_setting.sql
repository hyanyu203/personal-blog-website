-- Ensure default admin user has ADMIN role (idempotent)
INSERT INTO roles (code, name, description, metadata, created_at, updated_at)
SELECT 'ADMIN', '管理员', '', '{}', NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'ADMIN' AND deleted_at IS NULL);

INSERT INTO user_roles (user_id, role_id, created_at)
SELECT u.id, r.id, NOW()
FROM users u
INNER JOIN roles r ON r.code = 'ADMIN' AND r.deleted_at IS NULL
WHERE u.username = 'admin' AND u.deleted_at IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

INSERT INTO system_settings (`key`, value, description, is_public, updated_at)
SELECT 'guestbookTargetId', '1', '留言板评论 targetId', 1, NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE `key` = 'guestbookTargetId');
