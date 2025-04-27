package org.suai.users.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.suai.users.model.dto.serverRequest.CreateProfileRequestDTO;
import org.suai.users.model.dto.serverRequest.UpdateProfileRequestDTO;
import org.suai.users.model.dto.serverResponse.GetProfileResponseDTO;
import org.suai.users.model.entities.Photo;
import org.suai.users.service.interfaces.IProfileService;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected IProfileService profileService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GetProfileResponseDTO> createProfile(
            @RequestPart("profileData") String profileDataJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            ObjectMapper objectMapper) throws JsonProcessingException {

        CreateProfileRequestDTO profileData = objectMapper.readValue(profileDataJson, CreateProfileRequestDTO.class);
        profileData.setPhotos(photos);
        return ResponseEntity.ok(profileService.createProfile(profileData));
    }

    @GetMapping(value = "/{profileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileResponseDTO> getProfile(@PathVariable(name = "profileId") Long profileId){
        return ResponseEntity.ok(profileService.getProfile(profileId));
    }

    @GetMapping("/{profileId}/photos/{photoPositionId}")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable Long profileId, @PathVariable Integer photoPositionId) {

        Photo photo = profileService.getProfilePhoto(profileId, photoPositionId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + photo.getFileName() + "\"")
                .body(photo.getImage());
    }

    @PutMapping(
            value = "/{profileId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GetProfileResponseDTO> updateProfile(@PathVariable(name = "profileId") Long profileId,
                                                               @RequestPart(value = "profileData", required = false) String profileDataJson,
                                                               @RequestPart(value = "newPhotos", required = false) List<MultipartFile> newPhotos,
                                                               ObjectMapper objectMapper) throws JsonProcessingException {
        UpdateProfileRequestDTO updateDTO;
        if(profileDataJson != null && !profileDataJson.isEmpty())
            updateDTO = objectMapper.readValue(profileDataJson, UpdateProfileRequestDTO.class);
        else
            updateDTO = new UpdateProfileRequestDTO();

        updateDTO.setNewPhotos(newPhotos);
        return ResponseEntity.ok(profileService.updateProfile(profileId, updateDTO));
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<GetProfileResponseDTO> deleteProfile(HttpServletRequest request, @PathVariable(name = "profileId") Long profileId) {
        return ResponseEntity.ok(profileService.deleteProfile(profileId, request));
    }
}
