package com.svalero.asociation.controller;

import com.svalero.asociation.dto.MeResponseDto;
import com.svalero.asociation.dto.PrivateAreaRequestDto;
import com.svalero.asociation.service.MeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final MeService meService;

    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping
    public ResponseEntity<MeResponseDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(meService.getCurrentUser(authentication.getName()));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<PrivateAreaRequestDto>> getCurrentRequests(Authentication authentication) {
        return ResponseEntity.ok(meService.getCurrentRequests(authentication.getName()));
    }
}
