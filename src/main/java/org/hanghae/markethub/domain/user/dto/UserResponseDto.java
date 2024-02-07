package org.hanghae.markethub.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;

@Getter
@RequiredArgsConstructor
public class UserResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final String address;
    private final Role role;


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
    }
}
