package org.hanghae.markethub.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

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


    @Transactional
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );
        return new UserResponseDto(user);
    }


    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage())
        );
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

    @Transactional
    public void login(UserRequestDto requestDto, HttpServletResponse res) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND.getErrorMessage());
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_NOT_MATCH.getErrorMessage());
        }

        String token = jwtUtil.createToken(user.getEmail(), user.getRole());
        jwtUtil.addJwtToCookie(token, res);
    }


}
