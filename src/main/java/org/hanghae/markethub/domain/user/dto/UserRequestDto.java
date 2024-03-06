package org.hanghae.markethub.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.global.constant.Role;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Role role;
    private String adminToken = "";


    @Builder
    private UserRequestDto(String email, String password, String name, String phone, String address, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;

    }

    // 회원가입 할 때 adminToken은 관라자가입시 토큰을 받아서 입력해서 회원가입
    // role에 admin 체크하면 어드민 가능
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

}
