package org.hanghae.markethub.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.domain.user.service.UserService;
import org.hanghae.markethub.global.constant.ErrorMessage;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.SuccessMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .email("channy@naver.com")
                .name("chan")
                .phone("010-1234-5678")
                .address("Seoul")
                .build();

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("channy@naver.com")
                .password("1234abcd")
                .name("chan")
                .phone("010-1234-5678")
                .address("Seoul")
                .role(Role.USER)
                .build();

        UserRequestDto userRequestDto_duplicated = UserRequestDto.builder()
                .email("channy@naver.com")
                .password("1234abcd")
                .name("chan")
                .phone("010-1234-5678")
                .address("Seoul")
                .role(Role.USER)
                .build();

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .user(user)
                .build();

        when(userService.createUser(userRequestDto)).thenReturn(userResponseDto);
        when(userService.createUser(userRequestDto_duplicated)).thenThrow(new IllegalArgumentException(ErrorMessage.EMAIL_ALREADY_EXISTS.getErrorMessage()));

    }

    @Test
    @DisplayName("사용자 생성 성공")
    void testCreateUser_success() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("channy@naver.com")
                .password("1234abcd")
                .name("chan")
                .phone("010-1234-5678")
                .address("Seoul")
                .role(Role.USER)
                .build();
        mockMvc.perform(
                post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto))
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

//    @Test
//    void testGetUser() throws Exception {
//        when(userService.getUser(1L)).thenReturn(userResponseDto);
//
//        mockMvc.perform(get("/api/user/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("John Doe"))
//                .andExpect(jsonPath("$.email").value("john@example.com"));
//
//        verify(userService, times(1)).getUser(1L);
//    }

//    @Test
//    void testGetAllUsers() throws Exception {
//        List<UserResponseDto> userList = Arrays.asList(userResponseDto, new UserResponseDto(2L, "Jane Doe", "jane@example.com"));
//        when(userService.getAllUsers()).thenReturn(userList);
//
//        mockMvc.perform(get("/api/user"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("John Doe"))
//                .andExpect(jsonPath("$[0].email").value("john@example.com"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].name").value("Jane Doe"))
//                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
//
//        verify(userService, times(1)).getAllUsers();
//    }

    // Add more tests for other controller methods following a similar pattern

    @Test
    @DisplayName("사용자 생성 실패")
    void createUserFail() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("개인 회원 조회 성공")
    void getUser() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("개인 회원 조회 실패")
    void getUserFail() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("개인 회원 정보 수정 성공")
    void updateUser() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("개인 회원 정보 수정 실패")
    void updateUserFail() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("개인 회원 탈퇴 성공")
    void deleteUser() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("개인 회원 탈퇴 실패")
    void deleteUserFail() {
        // given

        // when

        // then
    }

}