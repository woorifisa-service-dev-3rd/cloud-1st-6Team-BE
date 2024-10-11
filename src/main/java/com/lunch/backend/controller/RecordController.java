package com.lunch.backend.controller;

import com.lunch.backend.model.RecordRequestDTO;
import com.lunch.backend.model.RecordResponseDTO;
import com.lunch.backend.model.ResponseDTO;
import com.lunch.backend.service.RecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class RecordController {
    private final RecordService recordService;

    @PostMapping
    public ResponseDTO<RecordResponseDTO> getGptResponse(@RequestBody RecordRequestDTO recordRequestDTO, @CookieValue("accessToken") String accessToken){
        return new ResponseDTO<>(HttpStatus.CREATED, recordService.showGptResponse(recordRequestDTO, accessToken));
    }


    @GetMapping
    public ResponseDTO<List<RecordResponseDTO>> getRecords(HttpServletRequest request, @CookieValue("accessToken") String accessToken){
        return new ResponseDTO<>(HttpStatus.OK, recordService.showRecords(accessToken));
    }
}