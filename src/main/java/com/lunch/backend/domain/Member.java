package com.lunch.backend.domain;

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
}