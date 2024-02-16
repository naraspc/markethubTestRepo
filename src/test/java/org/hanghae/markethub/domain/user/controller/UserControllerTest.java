package org.hanghae.markethub.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.service.UserService;
import org.hanghae.markethub.global.constant.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class UserControllerTest {

    private MockMvc mockMvc;

    AutoCloseable openMocks;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;


    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;


    @BeforeEach
    void setUp() {

        openMocks = MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userRequestDto = UserRequestDto.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .phone("123-456-7890")
                .address("123 Test St")
                .role(Role.USER)
                .build();

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .phone("123-456-7890")
                .address("123 Test St")
                .role(Role.USER)
                .build();

        UserRequestDto updatedUserRequestDto = UserRequestDto.builder()
                .email("updated@example.com")
                .password("newpassword")
                .name("Updated User")
                .phone("987-654-3210")
                .address("456 Updated St")
                .role(Role.ADMIN)
                .build();

    }

    @Test
    @DisplayName("회원 생성 성공")
    void createUser_Success() throws Exception {
        // given

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequestDto)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void signup_WithValidUser_RedirectToLoginForm() throws Exception {
        // Given
        UserRequestDto userRequestDto = new UserRequestDto(); // valid user object

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/signup")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\", \"name\":\"Test User\", \"phone\":\"123-456-7890\", \"address\":\"123 Test St\"}"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/api/user/loginFormPage"));
    }

    @Test
    void checkEmail_ExistingEmail_ReturnsTrue() throws Exception {
        // Given
        String existingEmail = "existing@example.com";
        when(userService.checkEmailExists(existingEmail)).thenReturn(true);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/checkEmail")
                        .param("email", existingEmail))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("true"));
    }

    @Test
    void checkEmail_ExistingEmail_ReturnsFalse() throws Exception {
        // Given
        String existingEmail = "existing@example.com";
        when(userService.checkEmailExists(existingEmail)).thenReturn(false);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/checkEmail")
                        .param("email", existingEmail))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("false"));
    }


    @Test
    void checkEmail_ExceptionThrown_InternalServerError() throws Exception {
        // Given
        String email = "test@example.com";
        when(userService.checkEmailExists(email)).thenThrow(new RuntimeException("Something went wrong"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/checkEmail")
                        .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void updateUser_Success() throws Exception {
        // Given

        Long userId = 1L;

        UserRequestDto updatedUserRequestDto = UserRequestDto.builder()
                .email("updated@example.com")
                .password("newpassword")
                .name("Updated User")
                .phone("987-654-3210")
                .address("456 Updated St")
                .role(Role.ADMIN)
                .build();

        when(userService.updateUser(userId, updatedUserRequestDto)).thenReturn(userResponseDto);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedUserRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteUser() {
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/loginFormPage"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"));
    }

    @Test
    void testSignupPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/signupPage"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("signup"));
    }


//    @Test
//    @WithUserDetails("chan")
//    void testMyPage() throws Exception {
//        String tokenValue = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjaGFubnlAbmF2ZXIuY29tIiwibmFtZSI6ImNoYW4iLCJhdXRoIjoiVV" +
//                "NFUiIsImV4cCI6MTcwODQzMDgzMCwiaWF0IjoxNzA4MDcwODMwfQ.d_IMa26HgZQFNVFHJX_JK4m_5dMupptIewFm4PDN4S4";
//
//        Cookie authCookie = new Cookie("Authorization", "Bearer" + tokenValue);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/mypagePage")
//                        .cookie(authCookie).with(csrf()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.view().name("myPage"));
//    }

    @Test
    void testErrorPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/errorPage"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }
}