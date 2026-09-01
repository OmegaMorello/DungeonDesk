package com.marcomoretta.dungeondesk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple controller to check the server status
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Get the server status
     *
     * @return Json status running
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        return ResponseEntity.ok(Map.of("status", "running"));
    }
}
