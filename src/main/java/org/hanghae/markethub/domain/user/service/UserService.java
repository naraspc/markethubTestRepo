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


    @Transactional
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

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );

        return new UserResponseDto(user);
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
