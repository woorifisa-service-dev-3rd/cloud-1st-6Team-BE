package com.lunch.backend.domain;

import com.lunch.backend.model.RecordResponseDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    public static Record from(RecordResponseDTO recordResponseDTO){
        return Record.builder()
                .image(recordResponseDTO.getImage())
                .content(recordResponseDTO.getContent())
                .build();
    }
}