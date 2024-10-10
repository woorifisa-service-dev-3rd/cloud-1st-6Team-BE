package com.lunch.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.lunch.backend.domain.SocialType;
import com.lunch.backend.model.*;
import com.lunch.backend.repository.MemberRepository;
import com.lunch.backend.domain.Member;
import com.lunch.backend.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final TypeRepository typeRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    public String googleLogin(RequestCodeDTO requestCodeDTO) throws Exception {
        String accessToken = getAccessToken(requestCodeDTO.getAuthCode());
        GoogleUserInfoDTO googleUserInfoDTO = getGoogleUserInfo(accessToken);

        Member member = memberRepository.findByEmail(googleUserInfoDTO.getEmail()).orElse(null);
        LoginResponseDTO loginResponseDTO;
        if (member == null) {
            SocialType socialType = typeRepository.findByProvider("google");
            Member newMember = Member.builder()
                    .email(googleUserInfoDTO.getEmail())
                    .name(googleUserInfoDTO.getName())
                    .socialType(socialType)
                    .build();

            loginResponseDTO = LoginResponseDTO.fromEntity(memberRepository.save(newMember));
        } else {
            loginResponseDTO = LoginResponseDTO.fromEntity(member);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginResponseDTO.getEmail(), "");

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtTokenProvider.generateToken(authentication);
    }

    public String getAccessToken(String authCode) throws Exception {
        String tokenRequestURL = "https://oauth2.googleapis.com/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", authCode);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", "postmessage"); // 리다이렉트 URI

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GoogleOAuthToken> response = restTemplate.postForEntity(tokenRequestURL, request, GoogleOAuthToken.class);

        return response.getBody().getAccess_token();
    }


//    public Optional<GoogleIdToken> verifyIdToken(String idTokenString) throws Exception {
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
//                .setAudience(Collections.singletonList(googleClientId))
//                .build();
//
//        GoogleIdToken idToken = verifier.verify(idTokenString);
//        return Optional.ofNullable(idToken);
//    }

    public GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, GoogleUserInfoDTO.class);
    }

    public String getAuthorityById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
        return member.getAuthorities();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
        return new User(member.getEmail(), member.getPassword(), new ArrayList<>());
    }
}
