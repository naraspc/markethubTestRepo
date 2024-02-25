package org.hanghae.markethub.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hanghae.markethub.domain.user.dto.LoginRequestDto;
import org.hanghae.markethub.domain.user.dto.UserDetailsDto;
import org.hanghae.markethub.global.constant.Role;
import org.hanghae.markethub.global.constant.SuccessMessage;

import org.hanghae.markethub.domain.user.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try{
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
        }catch (Exception e){

            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        UserDetailsDto userDetailsDto = userDetails.getUserDetailsDto();

        String email = userDetailsDto.getEmail();
        String username = userDetailsDto.getUsername();
        Role role = userDetailsDto.getRole();

        String token = jwtUtil.createToken(email, username, role);
        jwtUtil.addJwtToCookie(token, response);

        response.getWriter().write(SuccessMessage.LOGIN_SUCCESS_MESSAGE.getSuccessMessage());
        String queryString = request.getQueryString();
        //String baseURL = "/";
        String baseURL = queryString.substring(queryString.indexOf('=') + 1);
//        if (queryString != null && queryString.contains("=")) {
//            baseURL = queryString.substring(queryString.indexOf('=') + 1);
//        }

        if(baseURL.equals("")) {
            baseURL = "/";
        }
        response.sendRedirect(baseURL);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}