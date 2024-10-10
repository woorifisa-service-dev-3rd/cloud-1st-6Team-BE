package com.lunch.backend.service;


import com.lunch.backend.domain.Record;
import com.lunch.backend.model.RecordResponseDTO;
import com.lunch.backend.model.gpt.ChatGPTRequest;
import com.lunch.backend.model.gpt.ChatGPTResponse;
import com.lunch.backend.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    private final RestTemplate template;
    private final RecordRepository recordRepository;

    public RecordResponseDTO showGptResponse(String prompt){
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse chatGPTResponse =  template.postForObject(apiURL, request, ChatGPTResponse.class);
        String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        String image = "";

        RecordResponseDTO recordResponseDTO = RecordResponseDTO.of(image, content);
        Record record = Record.from(recordResponseDTO);
        recordRepository.save(record);

        return recordResponseDTO;
    }
}
