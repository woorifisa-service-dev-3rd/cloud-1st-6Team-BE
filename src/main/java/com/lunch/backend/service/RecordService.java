package com.lunch.backend.service;


import com.lunch.backend.core.exception.Exception404;
import com.lunch.backend.domain.Member;
import com.lunch.backend.domain.Record;
import com.lunch.backend.model.RecordResponseDTO;
import com.lunch.backend.model.gpt.ChatGPTImageRequest;
import com.lunch.backend.model.gpt.ChatGPTImageResponse;
import com.lunch.backend.model.gpt.ChatGPTRequest;
import com.lunch.backend.model.gpt.ChatGPTResponse;
import com.lunch.backend.repository.MemberRepository;
import com.lunch.backend.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    private final MemberRepository memberRepository;

    public RecordResponseDTO showGptResponse(String prompt, Long memberId){

        Member member = findMemberByMemberId(memberId);

        String content = findGptChat(prompt);
        String imageUrl = findGptImage(content);

        RecordResponseDTO recordResponseDTO = RecordResponseDTO.of(imageUrl, content);
        Record record = Record.from(recordResponseDTO, member);
        recordRepository.save(record);

        return recordResponseDTO;
    }

    private String findGptChat(String prompt){
        ChatGPTRequest request = new ChatGPTRequest(model, prompt + ". 요청하는데 단어 하나만 응답해주고 영어로 해줘. (예: Pizza)");
        ChatGPTResponse chatGPTResponse = template.postForObject(chatApiURL, request, ChatGPTResponse.class);
        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }

    private String findGptImage(String content){
        ChatGPTImageRequest imageRequest = new ChatGPTImageRequest(content, 1, "512x512");
        ChatGPTImageResponse imageResponse = template.postForObject(imageApiURL, imageRequest, ChatGPTImageResponse.class);
        return imageResponse.getData().get(0).getUrl();
    }

    private Member findMemberByMemberId(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new Exception404("해당 멤버를 찾을 수 없습니다."));
    }
}

