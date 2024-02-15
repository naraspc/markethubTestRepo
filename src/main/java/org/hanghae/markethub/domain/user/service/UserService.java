package org.hanghae.markethub.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import org.hanghae.markethub.domain.store.entity.Store;
import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.hanghae.markethub.global.config.UserConfig;
import org.hanghae.markethub.global.constant.ErrorMessage;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;
import org.hanghae.markethub.global.jwt.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserConfig userConfig;

    public User getUserValid(Long userId){
       return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        Role role = requestDto.getRole() != null ? requestDto.getRole() : Role.USER;
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .role(role)
                .status(Status.EXIST)
                .build();

        // 중복된 이메일 있는지 확인
        userConfig.checkEmail(requestDto.getEmail());

        userRepository.save(user);
        return new UserResponseDto(user);
    }



    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );
        return new UserResponseDto(user);
    }

    @Transactional
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (User user : users) {
            userResponseDtos.add(new UserResponseDto(user));
        }
        return userResponseDtos;
    }

    public User getUserEntity(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );
        return user;
    }

    public UserResponseDto getAuthenticatedUserResponseDto(UserDetailsImpl userDetails) {
        // UserDetailsImpl에서 인증된 사용자 정보 가져오기
        User user = userDetails.getUser();
        // UserResponseDto로 변환하여 반환
        return new UserResponseDto(user);
    }

    // 유저 id랑 status 체크하는 함수, 유저가 valid하지 않으면 에러 발생해서 함수 종료
    public void checkUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage());
        }
    }


    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );

        // 새로운 암호를 받아와서 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // UserRequestDto 빌더 패턴으로 암호를 설정
        requestDto = UserRequestDto.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .role(requestDto.getRole())
                .build();

        // 사용자 정보 업데이트
        user.update(requestDto);

        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );
        user.delete();
    }

<<<<<<< HEAD
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );
=======

    public void login(UserRequestDto requestDto, HttpServletResponse res) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
>>>>>>> abde21dfe52da4a6ed543d07e733dfa73f0d0b78

        return new UserResponseDto(user);
    }
}
