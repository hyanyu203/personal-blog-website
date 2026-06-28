package com.jiangou.support;

import com.jiangou.security.AuthCookieService;
import com.jiangou.security.ClientIpResolver;
import com.jiangou.security.CsrfCookieService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Shared mocks required by servlet filters in @WebMvcTest slices.
 */
@TestConfiguration
public class WebMvcTestMocks {

    @MockBean
    private CsrfCookieService csrfCookieService;

    @MockBean
    private AuthCookieService authCookieService;

    @MockBean
    private ClientIpResolver clientIpResolver;
}
