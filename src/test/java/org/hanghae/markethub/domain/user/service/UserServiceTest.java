//package org.hanghae.markethub.domain.user.service;
//
//import org.hanghae.markethub.domain.user.dto.UserRequestDto;
//import org.hanghae.markethub.domain.user.dto.UserResponseDto;
//import org.hanghae.markethub.domain.user.entity.User;
//import org.hanghae.markethub.domain.user.repository.UserRepository;
//import org.hanghae.markethub.global.constant.Role;
//import org.hanghae.markethub.global.constant.Status;
//import org.junit.jupiter.api.*;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class UserServiceTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Mock
//    private UserRepository mockUserRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private UserService userService;
//
//    private UserRequestDto userRequestDto;
//    private UserRequestDto userRequestDto2;
//    private UserRequestDto userRequestDto_duplicateEmail;
//
//    @BeforeEach
//    void setUp() {
//        userRequestDto = UserRequestDto.builder()
//                .email("test@example.com")
//                .password("password")
//                .name("Test User")
//                .phone("123-456-7890")
//                .address("123 Test St")
//                .role(Role.USER)
//                .build();
//
//
//        userRequestDto2 = UserRequestDto.builder()
//                .email("test2@example.com")
//                .password("password")
//                .name("Test User")
//                .phone("123-456-7890")
//                .address("123 Test St")
//                .role(Role.USER)
//                .build();
//
//
//
//        // 중복된 이메일로 사용자 생성 시 예외 발생
//        userRequestDto_duplicateEmail = UserRequestDto.builder()
//                .email("test@example.com")
//                .password("password2")
//                .name("Test User222")
//                .phone("123-456-7890222")
//                .address("123 Test St222")
//                .role(Role.USER)
//                .build();
//    }
//
//    @AfterEach
//    void tearDown() {
//        // 테스트 종료 후 데이터 삭제
//        userRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("사용자 생성")
//    void createUser() {
//        // given
//
//
//        // when
//        UserResponseDto responseDto = userService.createUser(userRequestDto);
//
//        // then
//        // Repository에 저장된 값 불러오기
//        Optional<User> OptionalUser =  userRepository.findByEmail(userRequestDto.getEmail());
//        if (OptionalUser.isEmpty()) {
//            fail("User not found in repository");
//        }
//        User savedUserInRepository = OptionalUser.get();
//        assertNotNull(savedUserInRepository);
//        assertEquals(userRequestDto.getEmail(), responseDto.getEmail());
//        assertEquals(userRequestDto.getName(), responseDto.getName());
//        assertEquals(userRequestDto.getPhone(), responseDto.getPhone());
//        assertEquals(userRequestDto.getAddress(), responseDto.getAddress());
//        assertEquals(userRequestDto.getRole(), responseDto.getRole());}
//
//    @Test
//    @DisplayName("사용자 생성 실패 - 중복된 이메일")
//    void createUserFail() {
//        // given
//
//        // when
//        UserResponseDto responseDto = userService.createUser(userRequestDto);
//
//        // then
//        // 중복된 이메일로 사용자 생성 시 예외 발생
//        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRequestDto_duplicateEmail));
//    }
//
//
//    @Test
//    @DisplayName("사용자 조회 성공")
//    void getUser() {
//    // given
//        UserResponseDto responseDto = userService.createUser(userRequestDto);
//
//        // when
//        UserResponseDto userResponseDto = userService.getUser(responseDto.getId());
//
//        // then
//        assertThat(responseDto).isNotNull(); // 사용자가 생성되었는지 확인
//        assertThat(userResponseDto).isNotNull();
//        assertThat(userResponseDto.getEmail()).isEqualTo(responseDto.getEmail());
//        assertThat(userResponseDto.getName()).isEqualTo(responseDto.getName());
//    }
//
//    @Test
//    @DisplayName("사용자 조회 실패 - 없는 사용자")
//    void getUserFail() {
//        // given
//        UserResponseDto responseDto = userService.createUser(userRequestDto);
//
//        // when
//        // 존재하지 않는 사용자 조회 시 예외 발생
//        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.getUser(-1L));
//    }
//
//    @Test
//    @DisplayName("모든 사용자 조회")
//    void getAllUsers() {
//        // given
//        UserResponseDto user1 = userService.createUser(userRequestDto);
//        UserResponseDto user2 = userService.createUser(userRequestDto2);
//
//        // when
//        List<UserResponseDto> allUsers = userService.getAllUsers();
//
//        // then
//        assertThat(allUsers).isNotNull();
//        assertThat(allUsers).hasSize(2); // 생성된 모든 사용자 수가 2개인지 확인
//    }
//
//    @Test
//    @DisplayName("사용자 정보 수정")
//    void testUpdateUser() {
//        // given
//        UserResponseDto createdUser = userService.createUser(userRequestDto);
//
//        // update 할 정보
//        UserRequestDto updateUserRequestDto = UserRequestDto.builder()
//                .email("updated@example.com")
//                .password("newpassword")
//                .name("Updated User")
//                .phone("987-654-3210")
//                .address("456 Updated St")
//                .role(Role.ADMIN)
//                .build();
//
//        // when
//        UserResponseDto updatedUser = userService.updateUser(createdUser.getId(), updateUserRequestDto);
//
//        // then
//        assertThat(updatedUser).isNotNull();
//        assertThat(updatedUser.getEmail()).isEqualTo(createdUser.getEmail()); // 이메일은 변경되지 않았는지 확인
//        assertThat(updatedUser.getEmail()).isNotEqualTo(updateUserRequestDto.getEmail());
//        assertThat(updatedUser.getName()).isEqualTo(createdUser.getName()); // 이름은 변경되지 않았는지 확인
//        assertThat(updatedUser.getName()).isNotEqualTo(updateUserRequestDto.getName());
//        assertThat(updatedUser.getPhone()).isEqualTo(updateUserRequestDto.getPhone());
//        assertThat(updatedUser.getAddress()).isEqualTo(updateUserRequestDto.getAddress());
//        assertThat(updatedUser.getRole()).isEqualTo(createdUser.getRole()); // 역할은 변경되지 않았는지 확인
//        assertThat(updatedUser.getRole()).isNotEqualTo(updateUserRequestDto.getRole());
//    }
//
//    @Test
//    @DisplayName("사용자 삭제")
//    void testDeleteUser() {
//        // given
//        UserResponseDto createdUser = userService.createUser(userRequestDto);
//
//        // when
//        userService.deleteUser(createdUser.getId());
//
//        // then
//        // 사용자 삭제 후 findById로 조회 시 null이 반환되는지 확인
//        User deletedUser = userRepository.findById(createdUser.getId()).orElse(null);
//        assertThat(deletedUser).isNull();
//    }
//
//    @Test
//    @DisplayName("DeleteUser 이후, 같은 email로 유저 생성")
//    void testDeleteUserAndCreateUser() {
//        // given
//        UserResponseDto createdUser = userService.createUser(userRequestDto);
//
//        // when
//        userService.deleteUser(createdUser.getId());
//
//        // then
//        // 사용자 삭제 후 같은 email로 생성시, 생성이 안 되는지 확인
//        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRequestDto));
//    }
//
//    @Test
//    @DisplayName("DeleteUser 이후, 수정 안되는지 확인")
//    void testDeleteUserAndUpdateUser() {
//        // given
//        UserResponseDto createdUser = userService.createUser(userRequestDto);
//
//        UserRequestDto updateUserRequestDto = UserRequestDto.builder()
//                .email("updated@example.com")
//                .password("newpassword")
//                .name("Updated User")
//                .phone("987-654-3210")
//                .address("456 Updated St")
//                .role(Role.ADMIN)
//                .build();
//
//        // when
//        userService.deleteUser(createdUser.getId());
//
//        // then
//        // 사용자 삭제 후 같은 email로 생성시, 생성이 안 되는지 확인
//        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.updateUser(createdUser.getId(),updateUserRequestDto));
//    }
//
//    @Test
//    @DisplayName("이메일로 사용자 조회")
//    void getUserByEmail() {
//        // given
//        UserResponseDto createdUser = userService.createUser(userRequestDto);
//
//        // when
//        UserResponseDto foundUser = userService.getUserByEmail(createdUser.getEmail());
//
//        // then
//        assertThat(foundUser).isNotNull();
//        assertThat(foundUser.getEmail()).isEqualTo(createdUser.getEmail());
//        assertThat(foundUser.getName()).isEqualTo(createdUser.getName());
//        assertThat(foundUser.getPhone()).isEqualTo(createdUser.getPhone());
//        assertThat(foundUser.getAddress()).isEqualTo(createdUser.getAddress());
//        assertThat(foundUser.getRole()).isEqualTo(createdUser.getRole());
//    }
//}