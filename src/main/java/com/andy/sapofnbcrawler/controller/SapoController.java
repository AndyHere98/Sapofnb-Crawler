package com.andy.sapofnbcrawler.controller;

import com.andy.sapofnbcrawler.service.SapoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${sapo-api.version}")
@RequiredArgsConstructor
public class SapoController {
    
    private final SapoService sapoService;
    
    @GetMapping("/get-menu")
    public ResponseEntity<?> getMenu() {
        return ResponseEntity.ok(sapoService.getMenu());
    }
}
