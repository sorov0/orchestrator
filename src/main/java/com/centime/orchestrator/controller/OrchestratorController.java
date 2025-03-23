package com.centime.orchestrator.controller;

import com.centime.orchestrator.dto.PersonRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/orchestrator")
@RequiredArgsConstructor
public class OrchestratorController {

    @Autowired
    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(OrchestratorController.class);

    @Value("${service.greetings.url}")
    private String greetingServiceUrl;

    @Value("${service.concatenation.url}")
    private String concatenationServiceUrl;

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("Up");
    }

    @PostMapping("/process")
    public ResponseEntity<String> processRequest(@RequestBody PersonRequest request) {

        String traceId = UUID.randomUUID().toString();
        logger.info("[{}] Received request: {}", traceId, request);

        // Call Service 2 (GET)
        ResponseEntity<String> greetResponse = restTemplate.getForEntity(greetingServiceUrl, String.class);

        // Call Service 3 (POST)
        ResponseEntity<String> concatResponse = restTemplate.postForEntity(concatenationServiceUrl, request, String.class);

        String finalResponse = greetResponse.getBody() + " " + concatResponse.getBody();
        logger.info("[{}] Final response: {}", traceId, finalResponse);

        return ResponseEntity.ok(finalResponse);
    }
}
