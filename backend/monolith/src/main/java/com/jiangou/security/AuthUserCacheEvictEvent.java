package com.jiangou.security;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class AuthUserCacheEvictEvent {

    private final Set<Long> userIds;

    public AuthUserCacheEvictEvent(Collection<Long> userIds) {
        this.userIds = Collections.unmodifiableSet(new LinkedHashSet<Long>(userIds));
    }

    public Set<Long> getUserIds() {
        return userIds;
    }
}
