package org.hanghae.markethub.global.config;


import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.domain.user.repository.UserRepository;
import org.hanghae.markethub.global.constant.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserConfig {

    @Value("${jwt.token}")
    private String ADMIN_TOKEN;

    private final UserRepository userRepository;

    public Role checkRole(UserRequestDto requestDto) {
        Role role = Role.USER;
        if(requestDto.isAdmin() && requestDto.getAdminToken().equals(ADMIN_TOKEN)){
            role = Role.ADMIN;
        }
        return role;
    }

    public void checkEmail(String email) {
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("해당 이메일은 이미 존재합니다");
        }
    }

}