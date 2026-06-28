package com.jiangou.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.common.cache.RedisCacheHelper;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.system.entity.SystemSettingEntity;
import com.jiangou.system.mapper.SystemSettingMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTest {

    @Mock
    private SystemSettingMapper settingMapper;

    @Mock
    private RedisCacheHelper cacheHelper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SystemSettingService systemSettingService;

    @Test
    void update_rejectsUnknownKey() {
        assertThrows(ValidationException.class, () -> systemSettingService.update("arbitraryKey", "value", 1L));

        verifyNoInteractions(settingMapper, cacheHelper);
    }

    @Test
    void update_allowsKnownKeyAndClearsPublicCache() {
        SystemSettingEntity entity = new SystemSettingEntity();
        entity.setKey("siteTitle");
        when(settingMapper.selectById("siteTitle")).thenReturn(entity);

        SystemSettingEntity updated = systemSettingService.update("siteTitle", "新的标题", 7L);

        verify(settingMapper).updateById(entity);
        verify(cacheHelper).delete("cache:settings:public");
        assertEquals("新的标题", updated.getValue());
        assertEquals(Long.valueOf(7L), updated.getUpdatedBy());
    }

    @Test
    void update_createsPublicSettingWithPublicFlag() {
        SystemSettingEntity created = systemSettingService.update("siteTitle", "新的标题", 3L);

        verify(settingMapper).insert(created);
        assertEquals(Boolean.TRUE, created.getIsPublic());
        assertEquals("新的标题", created.getValue());
    }
}
