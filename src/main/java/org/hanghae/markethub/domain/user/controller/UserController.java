package org.hanghae.markethub.domain.user.controller;

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
		userService.createUser(userRequestDto);

		return "redirect:/api/user/login-page";
	}
}
