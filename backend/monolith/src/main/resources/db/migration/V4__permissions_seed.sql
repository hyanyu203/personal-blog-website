-- Seed permissions and bind to ADMIN role (idempotent)

INSERT INTO permissions (code, name, module, description)
SELECT 'article:create', '创建文章', 'article', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'article:create');
INSERT INTO permissions (code, name, module, description)
SELECT 'article:update', '更新文章', 'article', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'article:update');
INSERT INTO permissions (code, name, module, description)
SELECT 'article:publish', '发布文章', 'article', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'article:publish');
INSERT INTO permissions (code, name, module, description)
SELECT 'comment:review', '审核评论', 'comment', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'comment:review');
INSERT INTO permissions (code, name, module, description)
SELECT 'project:sync', '同步项目', 'project', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'project:sync');
INSERT INTO permissions (code, name, module, description)
SELECT 'setting:update', '更新设置', 'system', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'setting:update');
INSERT INTO permissions (code, name, module, description)
SELECT 'user:manage', '用户管理', 'user', NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE code = 'user:manage');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN' AND r.deleted_at IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
