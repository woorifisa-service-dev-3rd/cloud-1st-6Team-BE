package com.lunch.backend.service;

import com.lunch.backend.model.CustomUserDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;


@PropertySource("classpath:application-prod.yml")
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final MemberService memberService;

    private final String HEADER_KEY = "Authorization";
    private final String PREFIX = "Bearer";

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Lazy MemberService memberService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.memberService = memberService;
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
        String email = userDetails.getEmail();
        Long id = userDetails.getId();
        String name = userDetails.getName();
        Date accessTokenExpire = Date.from(ZonedDateTime.now().plusMinutes(10).toInstant());
        return Jwts.builder()
                .subject(authentication.getName())
                .claim("id", id)
                .claim("email", email)
                .claim("name", name)
                .claim("auth", authorities)
                .expiration(accessTokenExpire)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Long userUid = (long) (int) claims.get("id");
        String userAuthorities = memberService.getAuthorityById(userUid);

        Collection<? extends GrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority(String.format("ROLE_%s", userAuthorities)));

        CustomUserDetail userDetail = CustomUserDetail.builder()
                .Id(userUid)
                .email((String) claims.get("email"))
                .name((String) claims.get("name"))
                .authorities(authorities)
                .build();


        return new UsernamePasswordAuthenticationToken(userDetail, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_KEY);
        if (!ObjectUtils.isEmpty(token) && token.startsWith(PREFIX)) {
            return token.substring(PREFIX.length()+1);
        }
        return null;
    }
}