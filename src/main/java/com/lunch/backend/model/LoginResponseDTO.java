package com.lunch.backend.model;

import com.lunch.backend.domain.Member;
import lombok.Getter;

@Getter
public class LoginResponseDTO {
    private final Long id;
    private final String email;
    private final String name;

    private LoginResponseDTO (Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static LoginResponseDTO fromEntity (Member member) {
        return new LoginResponseDTO(member.getId(), member.getEmail(), member.getName());
    }
}
