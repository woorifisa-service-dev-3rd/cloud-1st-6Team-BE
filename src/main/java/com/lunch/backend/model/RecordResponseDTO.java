package com.lunch.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class RecordResponseDTO {
    private String image;
    private String content;

    public static RecordResponseDTO of(String image, String content){
        return RecordResponseDTO.builder().image(image).content(content).build();
    }
}
