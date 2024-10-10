package com.lunch.backend.model;

import com.lunch.backend.domain.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class RecordResponseDTO {
    private String image;
    private String content;

    public static RecordResponseDTO of(String image, String content){
        return RecordResponseDTO.builder().image(image).content(content).build();
    }

    public static List<RecordResponseDTO> from(List<Record> records) {
        return records.stream()
                .map(record -> RecordResponseDTO.builder()
                        .image(record.getImage())
                        .content(record.getContent())
                        .build())
                .toList();
    }
}
