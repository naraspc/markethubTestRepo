package org.hanghae.markethub.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hanghae.markethub.global.constant.ErrorMessage;
import org.hanghae.markethub.global.constant.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    public final String AUTHORIZATION_HEADER = "Authorization";
    public final String REFRESHTOKEN_HEADER = "RefreshToken";

    private static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String JWT_LOG_HEAD = "JWT 관련 로그";
    public final long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L; // 60분
    public final long REFRESH_TOKEN_EXPIRATION_TIME = 14 * 60 * 60 * 24 * 1000L; // 14일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public static final Logger logger = LoggerFactory.getLogger(JWT_LOG_HEAD);

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(String email, String name, Role role) {
        return createToken(email, name, role, ACCESS_TOKEN_EXPIRATION_TIME, AUTHORIZATION_HEADER);
    }

    public String createRefreshToken(String email, String name, Role role) {
        return createToken(email, name, role, REFRESH_TOKEN_EXPIRATION_TIME, REFRESHTOKEN_HEADER);
    }


    public String createToken(String email,String name, Role role, final long TOKEN_TIME, String tokenType){
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + TOKEN_TIME);

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email) // 사용자 식별자값 (Sub)
                        .claim("tokenType", tokenType)
                        .claim("name", name)
                        .claim(AUTHORIZATION_KEY, role)
                        .setIssuedAt(now)
                        .setExpiration(expiredAt)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public void addJwtToCookie(String token, HttpServletResponse res, String cookieName) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(cookieName, token); // 새로운 이름으로 쿠키 생성
            cookie.setSecure(true); // HTTPS에서만 쿠키 전송
            cookie.setPath("/");

            if (cookieName.equals(AUTHORIZATION_HEADER)) {
                cookie.setMaxAge((int) ACCESS_TOKEN_EXPIRATION_TIME / 1000);
            } else if (cookieName.equals(REFRESHTOKEN_HEADER)){
                cookie.setMaxAge((int) REFRESH_TOKEN_EXPIRATION_TIME / 1000);
            }

            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e ) {
            logger.error(ErrorMessage.INVALID_JWT_ERROR_MESSAGE.getErrorMessage());
        } catch (ExpiredJwtException e) {
            logger.error(ErrorMessage.EXPIRED_JWT_ERROR_MESSAGE.getErrorMessage());
        } catch (UnsupportedJwtException e) {
            logger.error(ErrorMessage.UNSUPPORTED_JWT_ERROR_MESSAGE.getErrorMessage());
        } catch (IllegalArgumentException e) {
            logger.error(ErrorMessage.EMPTY_JWT_ERROR_MESSAGE.getErrorMessage());
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


    public String getUserEmailFromToken(HttpServletRequest req) {
        String accessToken = getTokenFromRequest(req, AUTHORIZATION_HEADER);
        if (accessToken != null && accessToken.startsWith(BEARER_PREFIX)) {
            accessToken = substringToken(accessToken);
            Claims claims = getUserInfoFromToken(accessToken);
            return claims.getSubject();
        }
        return null;
    }

    // 오버로딩
    public String getUserEmailFromToken(HttpServletRequest req, String cookieName) {
        String token = getTokenFromRequest(req, cookieName);
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            token = substringToken(token);
            Claims claims = getUserInfoFromToken(token);
            return claims.getSubject();
        }
        return null;
    }

    public String getTokenFromRequest(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) { // 변경된 쿠키 이름으로 수정
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public String refreshAccessToken(String refreshToken) {
        // Refresh 토큰이 유효하면 새로운 엑세스 토큰을 발급
        if (validateToken(refreshToken)) {
            Claims claims = getUserInfoFromToken(refreshToken);
            String email = claims.getSubject();
            String name = claims.get("name", String.class);
            Role role = Role.valueOf(claims.get(AUTHORIZATION_KEY, String.class));
            return createAccessToken(email, name, role);
        }
        return null;
    }

}
