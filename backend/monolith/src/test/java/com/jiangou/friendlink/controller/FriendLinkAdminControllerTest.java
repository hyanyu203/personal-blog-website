package com.jiangou.friendlink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.common.exception.GlobalExceptionHandler;
import com.jiangou.common.service.RateLimitService;
import com.jiangou.common.util.AuditSupport;
import com.jiangou.support.WebMvcTestMocks;
import com.jiangou.friendlink.dto.FriendLinkDTO;
import com.jiangou.friendlink.service.FriendLinkService;
import com.jiangou.friendlink.vo.FriendLinkVO;
import com.jiangou.security.AuthTokenResolver;
import com.jiangou.security.AuthUserCacheService;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FriendLinkAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, WebMvcTestMocks.class})
class FriendLinkAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FriendLinkService friendLinkService;

    @MockBean
    private AuditSupport auditSupport;

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
    @WithMockUser(roles = "ADMIN")
    void createFriendLink() throws Exception {
        FriendLinkDTO dto = new FriendLinkDTO();
        dto.setName("示例");
        dto.setUrl("https://example.com");

        when(friendLinkService.create(any(FriendLinkDTO.class)))
                .thenReturn(FriendLinkVO.builder().id(1L).name("示例").url("https://example.com").status("approved").build());

        mockMvc.perform(post("/api/v1/admin/friend-links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("示例"));

        verify(auditSupport).log(eq("friendlink:create"), eq("friendlink"), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFriendLink() throws Exception {
        FriendLinkDTO dto = new FriendLinkDTO();
        dto.setName("更新");

        when(friendLinkService.update(eq(2L), any(FriendLinkDTO.class)))
                .thenReturn(FriendLinkVO.builder().id(2L).name("更新").url("https://example.com").status("approved").build());

        mockMvc.perform(patch("/api/v1/admin/friend-links/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("更新"));
    }
}
