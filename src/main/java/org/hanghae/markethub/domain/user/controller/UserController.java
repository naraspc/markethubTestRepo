package org.hanghae.markethub.domain.user.controller;

import jakarta.validation.Valid;
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

    @PostMapping("/user/join")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDto requestDto) {
        return handleRequest(() -> {
            userService.createUser(requestDto);
            return new ResponseEntity<>(SuccessMessage.JOIN_SUCCESS_MESSAGE.getSuccessMessage(), HttpStatus.CREATED);
        });
    }

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

    private ResponseEntity<String> handleRequest(RequestHandler handler) {
        try {
            return handler.handle();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @FunctionalInterface
    private interface RequestHandler {
        ResponseEntity<String> handle();
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

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
