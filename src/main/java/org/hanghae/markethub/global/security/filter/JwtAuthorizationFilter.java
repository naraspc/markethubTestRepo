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
import org.hanghae.markethub.global.security.service.SecurityRedisService;
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
    private final SecurityRedisService securityRedisService;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("dofilterInternal 실행");
        String accessToken = jwtUtil.getTokenFromRequest(req, jwtUtil.AUTHORIZATION_HEADER); // 변경된 쿠키 이름으로 수정

        // access token이 없거나 유효하지 않은 경우 여기 걸림
        if (!jwtUtil.validateToken(accessToken)) {
            String refreshToken = jwtUtil.getTokenFromRequest(req, jwtUtil.REFRESHTOKEN_HEADER);
            String userEmailFromToken = jwtUtil.getUserEmailFromToken(req, jwtUtil.REFRESHTOKEN_HEADER);
            log.info("refreshToken : " + refreshToken);
            log.info("userEmailFromToken : " + userEmailFromToken);
            if (jwtUtil.validateToken(refreshToken)) {
                if (securityRedisService.getValues(userEmailFromToken).equals(refreshToken)) {
                    log.info("refreshToken이 일치합니다.");
                } else {
                    log.info("refreshToken이 일치하지 않습니다. 로그인이 필요합니다.");
                    filterChain.doFilter(req, res);
                    return;
                }
            }


            String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
            log.info("newAccessToken : " + newAccessToken);

            if (newAccessToken != null) {
                log.info("새로운 토큰이 발급되었습니다 ");
                jwtUtil.addJwtToCookie(newAccessToken, res, "Authorization");
                res.sendRedirect(req.getRequestURI());
            }
        }
        try {
            Claims info = jwtUtil.getUserInfoFromToken(accessToken);
            setAuthentication(info.getSubject());
        } catch (Exception e) {
            log.error(e.getMessage());
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