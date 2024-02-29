package org.hanghae.markethub.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.hanghae.markethub.domain.user.dto.LoginRequestDto;
import org.hanghae.markethub.domain.user.dto.UserDetailsDto;
import org.hanghae.markethub.domain.user.service.UserService;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.SuccessMessage;

import org.hanghae.markethub.global.security.impl.UserDetailsImpl;
import org.hanghae.markethub.global.security.jwt.JwtUtil;
import org.hanghae.markethub.global.security.service.SecurityRedisService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;

@Slf4j(topic = "로그인 및 JWT 생성")

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final SecurityRedisService securityRedisService;
    public JwtAuthenticationFilter(JwtUtil jwtUtil, SecurityRedisService securityRedisService){
        this.jwtUtil = jwtUtil;
        this.securityRedisService = securityRedisService;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");

        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email(request.getParameter("email"))
                .password(request.getParameter("password"))
                .build();

        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword(),
                        null
                )
        );

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        UserDetailsDto userDetailsDto = userDetails.getUserDetailsDto();

        String email = userDetailsDto.getEmail();
        String username = userDetailsDto.getUsername();
        Role role = userDetailsDto.getRole();

        String accessToken = jwtUtil.createAccessToken(email, username, role);
        jwtUtil.addJwtToCookie(accessToken, response, "Authorization");

        String refreshToken = jwtUtil.createRefreshToken(email, username, role);
        jwtUtil.addJwtToCookie(refreshToken, response, "AUTHORIZATION_REFRESH_TOKEN");

        // refresh token을 redis에 저장 ( key = Email, value = refreshToken )
        long refreshTokenExp = jwtUtil.REFRESH_TOKEN_EXPIRATION_TIME;
        log.info("redis 토큰 저장 refreshTokenExp : " + refreshTokenExp);
        securityRedisService.setValues(email, refreshToken, Duration.ofSeconds(refreshTokenExp));

        response.getWriter().write(SuccessMessage.LOGIN_SUCCESS_MESSAGE.getSuccessMessage());
        String queryString = request.getQueryString();
        String baseURL = queryString.substring(queryString.indexOf('=') + 1);


        if(baseURL.isEmpty()) {
            baseURL = "/";
        }
        response.sendRedirect(baseURL);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        // 만약 토큰이 만료되었고, Refresh 토큰이 아직 유효하다면 새로운 엑세스 토큰 발급
        String refreshToken = jwtUtil.getTokenFromRequest(request, "AUTHORIZATION_REFRESH_TOKEN");
        String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
        if (newAccessToken != null) {
            // 새로운 엑세스 토큰이 발급되었을 경우
            jwtUtil.addJwtToCookie(newAccessToken, response, "Authorization");
            response.sendRedirect(request.getRequestURI());
        } else {
            // 만약 새로운 엑세스 토큰을 발급할 수 없는 경우, 로그인 페이지로 리다이렉트
            response.sendRedirect("/api/user/loginFormPage?error");
            response.setStatus(401);
        }
    }



}