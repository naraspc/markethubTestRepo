package org.hanghae.markethub.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.global.constant.Role;

@Getter
@NoArgsConstructor

// 현재 인증받은 사용자의 정보를 담는 DTO
public class UserDetailsDto {
    private String email;
    private String username;
    private Role role;

    @Builder
    public UserDetailsDto(String email, String username, Role role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }
}
