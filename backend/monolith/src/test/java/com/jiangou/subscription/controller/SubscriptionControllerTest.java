package com.jiangou.subscription.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.common.exception.GlobalExceptionHandler;
import com.jiangou.common.service.RateLimitService;
import com.jiangou.security.AuthTokenResolver;
import com.jiangou.security.AuthUserCacheService;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.subscription.dto.SubscriptionTokenDTO;
import com.jiangou.subscription.service.SubscriptionService;
import com.jiangou.support.WebMvcTestMocks;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, WebMvcTestMocks.class})
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionService subscriptionService;

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
    void confirm_usesPostBodyToken() throws Exception {
        SubscriptionTokenDTO dto = new SubscriptionTokenDTO();
        dto.setToken("confirm-token");

        mockMvc.perform(post("/api/v1/subscriptions/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.message").value("订阅成功"));

        verify(subscriptionService).confirm("confirm-token");
    }

    @Test
    void unsubscribe_usesPostBodyToken() throws Exception {
        SubscriptionTokenDTO dto = new SubscriptionTokenDTO();
        dto.setToken("unsubscribe-token");

        mockMvc.perform(post("/api/v1/subscriptions/unsubscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.message").value("已退订"));

        verify(subscriptionService).unsubscribe("unsubscribe-token");
    }
}
