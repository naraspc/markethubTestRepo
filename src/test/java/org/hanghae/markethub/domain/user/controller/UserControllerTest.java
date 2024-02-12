/*
package org.hanghae.markethub.domain.user.controller;

import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.service.UserService;
import org.hanghae.markethub.global.constant.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    private UserRequestDto userRequestDto;
    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .phone("123-456-7890")
                .address("123 Test St")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("회원 생성 성공")
    void createUser_Success() {
        // given

        // Mock userService.createUser() 메서드가 성공적으로 호출될 때 반환될 응답
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("회원 가입 성공 메시지", HttpStatus.CREATED);


        // when
        ResponseEntity<String> response = userController.createUser(userRequestDto);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("회원 가입 성공 메시지", response.getBody());
        verify(userService, times(1)).createUser(any(UserRequestDto.class));
    }


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

}*/
