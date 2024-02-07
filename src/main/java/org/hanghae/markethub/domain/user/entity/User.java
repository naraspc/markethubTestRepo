package org.hanghae.markethub.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;


    public void update(UserRequestDto requestDto) {
        this.password = requestDto.getPassword();
        this.phone = requestDto.getPhone();
        this.address = requestDto.getAddress();
    }

    public void delete() {
        this.status = Status.DELETED;
    }
}
