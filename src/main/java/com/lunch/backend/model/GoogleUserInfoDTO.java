package com.lunch.backend.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleUserInfoDTO {
    private String id;
    private String email;
    private String name;
    private String picture;
}
