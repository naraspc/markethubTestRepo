package org.hanghae.markethub.domain.user.controller;

import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
	@GetMapping("/user/signup")
	public String signupPage() {
		return "signup";
	}

	@PostMapping("/user/signup")
	public String signup(@RequestBody UserRequestDto userRequestDto) {
		System.out.println(userRequestDto.getName());
		return "redirect:/api/user/login-page";
	}
}
