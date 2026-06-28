package com.jiangou.system.controller;

import com.jiangou.common.exception.GlobalExceptionHandler;
import com.jiangou.support.WebMvcTestMocks;
import com.jiangou.common.service.RateLimitService;
import com.jiangou.security.AuthTokenResolver;
import com.jiangou.security.AuthUserCacheService;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.system.service.HomeService;
import com.jiangou.system.service.StatsService;
import com.jiangou.system.service.SystemSettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SystemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, WebMvcTestMocks.class})
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    @MockBean
    private HomeService homeService;

    @MockBean
    private SystemSettingService systemSettingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockBean
    private JwtTokenBlacklistService jwtTokenBlacklistService;

    @MockBean
    private AuthTokenResolver authTokenResolver;

    @MockBean
    private AuthUserCacheService authUserCacheService;

    @MockBean
    private RateLimitService rateLimitService;

    @Test
    void publicStats() throws Exception {
        Map<String, Object> stats = new HashMap<String, Object>();
        stats.put("articleCount", 10);
        stats.put("runningDays", 100);
        when(statsService.publicStats()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.articleCount").value(10))
                .andExpect(jsonPath("$.data.runningDays").value(100));
    }
}
