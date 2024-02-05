package org.hanghae.markethub.global.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_ERROR_LOG = "Token Error";



}
