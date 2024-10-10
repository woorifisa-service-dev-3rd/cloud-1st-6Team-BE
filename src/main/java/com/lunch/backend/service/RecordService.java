package com.lunch.backend.service;


import com.lunch.backend.domain.Record;
import com.lunch.backend.model.RecordResponseDTO;
import com.lunch.backend.model.gpt.ChatGPTImageRequest;
import com.lunch.backend.model.gpt.ChatGPTImageResponse;
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

    @Value("${openai.api.chat-url}")
    private String chatApiURL;

    @Value("${openai.api.image-url}")
    private String imageApiURL;

    private final RestTemplate template;
    private final RecordRepository recordRepository;

    public RecordResponseDTO showGptResponse(String prompt){
        // 텍스트 응답 요청
        ChatGPTRequest request = new ChatGPTRequest(model, prompt + ". 요청하는데 단어 하나만 응답해주고 영어로 해줘. (예: Pizza)");
        ChatGPTResponse chatGPTResponse = template.postForObject(chatApiURL, request, ChatGPTResponse.class);
        String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();

        // 이미지 생성 요청
        ChatGPTImageRequest imageRequest = new ChatGPTImageRequest(content, 1, "512x512");
        ChatGPTImageResponse imageResponse = template.postForObject(imageApiURL, imageRequest, ChatGPTImageResponse.class);
        String imageUrl = imageResponse.getData().get(0).getUrl();

        // RecordResponseDTO 작성 및 저장
        RecordResponseDTO recordResponseDTO = RecordResponseDTO.of(imageUrl, content);
        Record record = Record.from(recordResponseDTO);
        recordRepository.save(record);

        return recordResponseDTO;
    }
}

