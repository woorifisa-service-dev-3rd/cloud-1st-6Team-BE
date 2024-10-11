package com.lunch.backend.model.gpt;

import lombok.Data;

@Data
public class ChatGPTImageRequest {
    private String prompt;
    private int n;
    private String size;

    public ChatGPTImageRequest(String prompt, int n, String size) {
        this.prompt = prompt;
        this.n = n;
        this.size = size;
    }
}
