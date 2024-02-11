package org.hanghae.markethub.domain.user.service;

import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto_duplicateEmail;

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

        // 중복된 이메일로 사용자 생성 시 예외 발생
        userRequestDto_duplicateEmail = UserRequestDto.builder()
                .email("test@example.com")
                .password("password2")
                .name("Test User222")
                .phone("123-456-7890222")
                .address("123 Test St222")
                .role(Role.USER)
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 데이터 삭제
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 생성")
    void createUser() {
        // given


        // when
        UserResponseDto responseDto = userService.createUser(userRequestDto);

        // then
        // Repository에 저장된 값 불러오기
        User savedUserInRepository = userRepository.findByEmail(userRequestDto.getEmail());

        assertNotNull(savedUserInRepository);
        assertEquals(userRequestDto.getEmail(), responseDto.getEmail());
        assertEquals(userRequestDto.getName(), responseDto.getName());
        assertEquals(userRequestDto.getPhone(), responseDto.getPhone());
        assertEquals(userRequestDto.getAddress(), responseDto.getAddress());
        assertEquals(userRequestDto.getRole(), responseDto.getRole());}

    @Test
    @DisplayName("사용자 생성 실패 - 중복된 이메일")
    void createUserFail() {
        // given

        // when
        UserResponseDto responseDto = userService.createUser(userRequestDto);

        // then
        // 중복된 이메일로 사용자 생성 시 예외 발생
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRequestDto_duplicateEmail));
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
    void testGetUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void testUpdateUser() {
    }

    @Test
    void testDeleteUser() {
    }

    @Test
    void getUserByEmail() {
    }
}