package org.hanghae.markethub.global.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae.markethub.global.security.impl.UserDetailsServiceImpl;
import org.hanghae.markethub.global.security.jwt.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.getTokenFromRequest(req, jwtUtil.AUTHORIZATION_HEADER); // 변경된 쿠키 이름으로 수정
        System.out.println(accessToken + " :accessToken");

        if (accessToken == null || accessToken.isEmpty()) {
            filterChain.doFilter(req, res);
            return;
        }

        if (StringUtils.hasText(accessToken)) {
            // JWT 토큰 substring
            accessToken = jwtUtil.substringToken(accessToken);
            log.info(accessToken);
            System.out.println(jwtUtil.getTokenFromRequest(req, jwtUtil.REFRESHTOKEN_HEADER) + " refresh token");

            if (!jwtUtil.validateToken(accessToken)) {

                    String refreshToken = jwtUtil.getTokenFromRequest(req, jwtUtil.REFRESHTOKEN_HEADER);
                    String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
                    System.out.println(newAccessToken + " new Access Token");
                    if (newAccessToken != null) {
                        // 새로운 엑세스 토큰이 발급되었을 경우
                        System.out.println("새로운 토큰이 발급되었습니다 ");
                        jwtUtil.addJwtToCookie(newAccessToken, res, "Authorization");
                        res.sendRedirect(req.getRequestURI());
                    } else {
                    // 만약 새로운 엑세스 토큰을 발급할 수 없는 경우, 로그인 페이지로 리다이렉트
                    res.sendRedirect("/api/user/loginFormPage?error");
                    res.setStatus(401);
                }
            }

            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}