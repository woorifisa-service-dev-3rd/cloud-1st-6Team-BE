package com.lunch.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.lunch.backend.model.*;
import com.lunch.backend.repository.MemberRepository;
import com.lunch.backend.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-url}")
    private String googleRedirectUrl;

    public ResponseEntity<String> googleLogin(RequestCodeDTO requestCodeDTO) throws Exception {
        String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
        String authCode = requestCodeDTO.getAuthCode();
        RestTemplate restTemplate=new RestTemplate();

        Map<String, Object> params = new HashMap<>();
        params.put("code", authCode);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirectUrl);
        params.put("grant_type", "authorization_code");

        return restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, params, String.class);
    }

    public Optional<GoogleIdToken> verifyIdToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        return Optional.ofNullable(idToken);
    }

    public GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, GoogleUserInfoDTO.class);
    }

    public String getAuthorityById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
        return member.getAuthorities();
    }

}
