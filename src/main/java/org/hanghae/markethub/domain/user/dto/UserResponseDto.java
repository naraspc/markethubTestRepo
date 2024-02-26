package org.hanghae.markethub.domain.user.dto;

import lombok.*;
import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private Role role;

    @Builder
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
    }

}
