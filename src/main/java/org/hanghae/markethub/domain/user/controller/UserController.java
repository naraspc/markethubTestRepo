package org.hanghae.markethub.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae.markethub.domain.user.dto.UserRequestDto;
import org.hanghae.markethub.domain.user.dto.UserResponseDto;
import org.hanghae.markethub.domain.user.service.UserService;
import org.hanghae.markethub.global.constant.SuccessMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long userId) {
        UserResponseDto userResponseDto = userService.getUser(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> userResponseDtoList = userService.getAllUsers();
        return ResponseEntity.ok(userResponseDtoList);
    }

    @PatchMapping("/user/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId, @RequestBody UserRequestDto requestDto) {
        UserResponseDto userResponseDto = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(SuccessMessage.DELETE_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
    }

    @FunctionalInterface
    private interface RequestHandler {
        ResponseEntity<String> handle();
    }

    @GetMapping("/user/loginForm")
    public String loginPage() {
        System.out.println();
        return "login";
    }


    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/user/signup")
    public String signup(@RequestBody UserRequestDto userRequestDto) {
        userService.createUser(userRequestDto);
        return "redirect:/api/user/loginForm";
    }
}
