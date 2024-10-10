package com.lunch.backend.controller;

import com.lunch.backend.model.RecordResponseDTO;
import com.lunch.backend.model.ResponseDTO;
import com.lunch.backend.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @PostMapping
    public ResponseDTO<RecordResponseDTO> getGptResponse(@RequestParam(name = "prompt") String prompt){
        return new ResponseDTO<>(HttpStatus.CREATED, recordService.showGptResponse(prompt, 1L));
    }


    @GetMapping
    public ResponseDTO<List<RecordResponseDTO>> getRecords(){
        return new ResponseDTO<>(HttpStatus.OK, recordService.showRecords(1L));
    }
}