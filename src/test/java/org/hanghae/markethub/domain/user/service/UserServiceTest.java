package org.hanghae.markethub.domain.user.service;

import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
    @DisplayName("사용자 생성")
    void createUser() {
        // given
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        User savedUser = User.builder()
                .email(userRequestDto.getEmail())
                .password(encodedPassword)
                .name(userRequestDto.getName())
                .phone(userRequestDto.getPhone())
                .address(userRequestDto.getAddress())
                .role(userRequestDto.getRole())
                .build();
        when(userRepository.save(any())).thenReturn(savedUser);


        // when
        UserResponseDto responseDto = userService.createUser(userRequestDto);

        // then
        // Repository에 저장된 값 불러오기
        User savedUserInRepository = userRepository.save(any());

        assertNotEquals(userRequestDto.getPassword(), encodedPassword);
        assertTrue(passwordEncoder.matches(userRequestDto.getPassword(), encodedPassword));

        assertEquals(responseDto.getEmail(), savedUserInRepository.getEmail());
        assertEquals(responseDto.getName(), savedUserInRepository.getName());
        assertEquals(responseDto.getPhone(), savedUserInRepository.getPhone());
        assertEquals(responseDto.getAddress(), savedUserInRepository.getAddress());
        assertEquals(responseDto.getRole(), savedUserInRepository.getRole());
    }

    @Test
    @DisplayName("사용자 생성 실패 - 중복된 이메일")
    void createUserFail() {
        // given

        // when
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true); // 중복된 이메일이 존재한다고 설정

        // then
        // 중복된 이메일로 사용자 생성 시 예외 발생
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRequestDto));
    }


    @Test
    @DisplayName("사용자 조회 성공")
    void getUser() {
    // given
        UserResponseDto responseDto = userService.createUser(userRequestDto);

        // when
        UserResponseDto userResponseDto = userService.getUser(responseDto.getId());

        // then
        assertThat(responseDto).isNotNull(); // 사용자가 생성되었는지 확인
        assertThat(userResponseDto).isNotNull();
        assertThat(userResponseDto.getEmail()).isEqualTo(responseDto.getEmail());
        assertThat(userResponseDto.getName()).isEqualTo(responseDto.getName());
    }



    @Test
    void getUsers() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}