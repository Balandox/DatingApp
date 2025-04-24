package org.suai.auth.control;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.suai.auth.model.dto.UserRegisterRequestDTO;
import org.suai.auth.service.KeycloakAdminService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AdminKeycloakController {

    private final KeycloakAdminService keycloakAdminService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequestDTO request) {
        try {
            keycloakAdminService.createUser(request);
            return ResponseEntity.ok("User created");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/delete/{profileId}")
    public ResponseEntity<String> deleteUser(@PathVariable String profileId){
        try {
            keycloakAdminService.deleteUser(profileId);
            return ResponseEntity.ok().body("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete user: " + e.getMessage());
        }
    }
}
