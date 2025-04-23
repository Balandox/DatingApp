package org.suai.gateway.control;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.suai.gateway.model.dto.UserRegisterRequestDTO;
import org.suai.gateway.service.KeycloakAdminService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AdminKeycloakController {

    private final RestTemplate userServiceRestTemplate;
    private final KeycloakAdminService keycloakAdminService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequestDTO request) {
        try {
            keycloakAdminService.createUser(request);
            return ResponseEntity.ok("User created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user: " + e.getMessage());
        }
    }
}
