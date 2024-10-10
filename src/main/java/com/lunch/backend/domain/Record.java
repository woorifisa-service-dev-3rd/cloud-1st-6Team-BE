package com.lunch.backend.domain;

import com.lunch.backend.model.RecordResponseDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Record extends BaseTime {
    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String image;
    private String content;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public static Record from(RecordResponseDTO recordResponseDTO, Member member){
        return Record.builder()
                .member(member)
                .image(recordResponseDTO.getImage())
                .content(recordResponseDTO.getContent())
                .build();
    }
}