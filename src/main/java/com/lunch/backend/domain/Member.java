package com.lunch.backend.domain;

import com.lunch.backend.model.GoogleUserInfoDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTime {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String name;

    private String password;

    private String authorities;

    private Member(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static Member FromGoogle(GoogleUserInfoDTO googleUserInfoDTO) {
        return new Member(googleUserInfoDTO.getEmail(), googleUserInfoDTO.getName());
    }
}