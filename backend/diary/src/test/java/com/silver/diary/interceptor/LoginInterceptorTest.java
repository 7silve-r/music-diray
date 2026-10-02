package com.silver.diary.interceptor;

import com.silver.diary.controller.UserController;
import com.silver.diary.handler.GlobalExceptionHandler;
import com.silver.diary.service.UserService;
import com.silver.diary.support.TestData;
import com.silver.diary.utils.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginInterceptorTest {
    JwtUtil jwt; LoginInterceptor interceptor;
    @BeforeEach void setup() {
        jwt = mock(JwtUtil.class); interceptor = new LoginInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtUtil", jwt);
    }
    @Test void noToken() {
        assertEquals(401, assertThrows(ResponseStatusException.class,
                () -> interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object())).getStatusCode().value());
        verifyNoInteractions(jwt);
    }
    @Test void badToken() {
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "bad");
        doThrow(new io.jsonwebtoken.MalformedJwtException("bad")).when(jwt).validateToken("bad");
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object())).getStatusCode().value());
        verify(jwt).validateToken("bad");
    }
    @Test @DisplayName("Authorization 请求头应同时通过拦截器和真实控制器")
    void authHeader() throws Exception {
        var users = mock(UserService.class); when(TestData.users(users).one()).thenReturn(TestData.user());
        var controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", users);
        ReflectionTestUtils.setField(controller, "jwtUtil", jwt);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()).addInterceptors(interceptor).build();
        mvc.perform(get("/my/userinfo").header("Authorization", "valid-token"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        verify(jwt).validateToken("valid-token");
    }
}
