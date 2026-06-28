package com.jiangou.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiangou.common.exception.GlobalExceptionHandler;
import com.jiangou.common.service.RateLimitService;
import com.jiangou.common.util.AuditSupport;
import com.jiangou.security.AuthTokenResolver;
import com.jiangou.security.AuthUserCacheService;
import com.jiangou.security.JwtTokenBlacklistService;
import com.jiangou.security.JwtTokenProvider;
import com.jiangou.security.SecurityUserDetailsService;
import com.jiangou.support.WebMvcTestMocks;
import com.jiangou.user.dto.UserUpdateDTO;
import com.jiangou.user.service.UserService;
import com.jiangou.user.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, WebMvcTestMocks.class})
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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
    void getUser() throws Exception {
        when(userService.getById(1L)).thenReturn(UserVO.builder()
                .id(1L).username("admin").displayName("管理员")
                .status("active").roles(Collections.singletonList("ADMIN"))
                .permissions(Collections.emptyList()).build());

        mockMvc.perform(get("/api/v1/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setDisplayName("新名称");

        when(userService.update(eq(1L), any(UserUpdateDTO.class))).thenReturn(UserVO.builder()
                .id(1L).username("admin").displayName("新名称")
                .status("active").roles(Collections.singletonList("ADMIN"))
                .permissions(Collections.emptyList()).build());

        mockMvc.perform(patch("/api/v1/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.displayName").value("新名称"));
    }
}
