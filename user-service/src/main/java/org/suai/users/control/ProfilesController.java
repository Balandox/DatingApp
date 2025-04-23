package org.suai.users.control;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.suai.users.model.dto.serverRequest.CreateProfileRequestDTO;
import org.suai.users.model.dto.serverRequest.UpdateProfileRequestDTO;
import org.suai.users.model.dto.serverResponse.GetProfileResponseDTO;
import org.suai.users.service.interfaces.IProfileService;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected IProfileService profileService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileResponseDTO> createProfile(@RequestBody CreateProfileRequestDTO requestDTO){
        return ResponseEntity.ok(profileService.createProfile(requestDTO));
    }

    @GetMapping(value = "/{profileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileResponseDTO> createProfile(@PathVariable(name = "profileId") Long profileId){
        return ResponseEntity.ok(profileService.getProfile(profileId));
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<GetProfileResponseDTO> updateProfile(@PathVariable(name = "profileId") Long profileId,
                                                               @RequestBody UpdateProfileRequestDTO updateDTO) {
        return ResponseEntity.ok(profileService.updateProfile(profileId, updateDTO));
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<GetProfileResponseDTO> deleteProfile(@PathVariable(name = "profileId") Long profileId) {
        return ResponseEntity.ok(profileService.deleteProfile(profileId));
    }
}
