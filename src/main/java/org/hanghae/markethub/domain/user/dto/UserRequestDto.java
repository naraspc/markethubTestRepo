package org.hanghae.markethub.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.Status;

@Getter
@NoArgsConstructor
public class UserRequestDto {
	private String email;
	private String password;
	private String name;
	private String phone;
	private String address;
	private Role role;
	private Status status;

	@Builder
	private UserRequestDto(String email, String password, String name, String phone, String address, Role role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.role = role;
		this.status = Status.EXIST;
	}
}
