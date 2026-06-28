package com.jiangou.security;

/**
 * SpEL expressions for admin API method security. Align with permissions seeded in V4__permissions_seed.sql.
 */
public final class AdminPermissions {

    private static final String ADMIN = "hasRole('ADMIN')";

    public static final String USER_MANAGE = ADMIN + " or hasAuthority('user:manage')";
    public static final String COMMENT_REVIEW = ADMIN + " or hasAuthority('comment:review')";
    public static final String PROJECT_SYNC = ADMIN + " or hasAuthority('project:sync')";
    public static final String SETTING_UPDATE = ADMIN + " or hasAuthority('setting:update')";

    public static final String ARTICLE_READ = ADMIN
            + " or hasAnyAuthority('article:create','article:update','article:publish')";
    public static final String ARTICLE_CREATE = ADMIN + " or hasAuthority('article:create')";
    public static final String ARTICLE_UPDATE = ADMIN + " or hasAuthority('article:update')";
    public static final String ARTICLE_PUBLISH = ADMIN + " or hasAuthority('article:publish')";

    /** Routes without a dedicated permission code (categories, media, etc.). */
    public static final String ADMIN_ONLY = ADMIN;

    private AdminPermissions() {
    }
}
