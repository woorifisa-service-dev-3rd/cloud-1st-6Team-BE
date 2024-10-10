package com.lunch.backend.controller;

import com.lunch.backend.model.RecordResponseDTO;
import com.lunch.backend.model.ResponseDTO;
import com.lunch.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;
    
    @GetMapping
    public ResponseDTO<RecordResponseDTO> chat(@RequestParam(name = "prompt") String prompt){
        return new ResponseDTO<>(HttpStatus.CREATED, recordService.showGptResponse(prompt));
    }

}