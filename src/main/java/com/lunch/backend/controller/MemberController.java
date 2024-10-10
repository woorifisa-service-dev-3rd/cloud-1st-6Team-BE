package com.lunch.backend.controller;

import com.lunch.backend.model.LoginRequestDTO;
import com.lunch.backend.model.RequestCodeDTO;
import com.lunch.backend.model.RequestTokenDTO;
import com.lunch.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestCodeDTO requestCodeDTO) throws Exception {
        try {
            Object[] dtoAndToken = memberService.googleLogin(requestCodeDTO);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, dtoAndToken[1].toString())
                    .body(dtoAndToken[0]);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
