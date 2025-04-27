package org.suai.users.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.suai.users.model.dto.PhotoDTO;
import org.suai.users.model.dto.serverRequest.CreateProfileRequestDTO;
import org.suai.users.model.dto.serverRequest.LocationDTO;
import org.suai.users.model.dto.serverRequest.UpdateProfileRequestDTO;
import org.suai.users.model.dto.serverResponse.GetProfileResponseDTO;
import org.suai.users.model.entities.*;
import org.suai.users.model.entities.pk.ProfileFactId;
import org.suai.users.model.enums.UserFacts;
import org.suai.users.model.enums.UserInterest;
import org.suai.users.repositories.*;
import org.suai.users.service.interfaces.IProfileService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProfileService implements IProfileService {

    private ProfileRepository profileRepository;

    private InterestRepository interestRepository;

    private FactRepository factRepository;

    private PhotoRepository photoRepository;

    private ProfileFactRepository profileFactRepository;

    private RestTemplate authServiceRestTemplate;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public GetProfileResponseDTO createProfile(CreateProfileRequestDTO requestDTO) {
        Set<Interest> interests = new HashSet<>();
        if (requestDTO.getInterests() != null) {
            for (UserInterest interestName : requestDTO.getInterests()) {
                Interest interest = interestRepository.findByName(interestName)
                        .orElseGet(() -> new Interest(interestName));
                interests.add(interest);
            }
        }

        Profile profile = Profile.builder()
                .name(requestDTO.getName())
                .age(requestDTO.getAge())
                .description(requestDTO.getDescription())
                .gender(requestDTO.getGender())
                .location(geometryFactory.createPoint(new Coordinate(requestDTO.getLocation().getLongitude(), requestDTO.getLocation().getLatitude())))
                .registeredAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .notificationsEnabled(Boolean.TRUE)
                .isActive(Boolean.TRUE)
                .interests(interests)
                .email(this.getEmailFromSecurityContext())
                .build();

        profile = profileRepository.save(profile);

        if (requestDTO.getFacts() != null) {
            for (Map.Entry<UserFacts, String> entry : requestDTO.getFacts().entrySet()) {
                Fact fact = factRepository.findByName(entry.getKey())
                        .orElseGet(() -> factRepository.save(new Fact(entry.getKey())));

                profileFactRepository.save(ProfileFact.builder()
                                .id(new ProfileFactId(profile.getId(), fact.getId()))
                                .profile(profile)
                                .fact(fact)
                                .factValue(entry.getValue()).build());
            }
        }

        if (requestDTO.getPhotos() != null && !requestDTO.getPhotos().isEmpty()) {
            for (int i = 0; i < requestDTO.getPhotos().size(); i++) {
                MultipartFile file = requestDTO.getPhotos().get(i);
                try {
                    Photo photo = Photo.builder()
                            .profile(profile)
                            .image(file.getBytes())
                            .contentType(file.getContentType())
                            .fileName(file.getOriginalFilename())
                            .position(i + 1)
                            .uploadedAt(LocalDateTime.now())
                            .build();

                    profile.getPhotos().add(photo);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to process photo", e);
                }
            }
        }
        profile = profileRepository.save(profile);

        return GetProfileResponseDTO.builder()
                .profileId(profile.getId())
                .message("Profile was created")
                .build();
    }

    @Override
    public GetProfileResponseDTO getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        List<ProfileFact> facts = profileFactRepository.findByProfile(profile);
        List<UserInterest> interests = profile.getInterests().stream()
                .map(Interest::getName).toList();

        Map<UserFacts, String> factMap = facts.stream()
                .collect(Collectors.toMap(f -> f.getFact().getName(), ProfileFact::getFactValue));

        List<PhotoDTO> photoDTOs = profile.getPhotos().stream()
                .sorted(Comparator.comparingInt(Photo::getPosition))
                .map(photo -> PhotoDTO.builder()
                        .contentType(photo.getContentType())
                        .fileName(photo.getFileName())
                        .position(photo.getPosition())
                        .uploadedAt(photo.getUploadedAt())
                        .downloadUri("/profiles/" + profileId + "/photos/" + photo.getPosition())
                        .build())
                .toList();

        return GetProfileResponseDTO.builder()
                .profileId(profile.getId())
                .name(profile.getName())
                .age(profile.getAge())
                .gender(profile.getGender())
                .description(profile.getDescription())
                .location(new LocationDTO(profile.getLocation().getY(), profile.getLocation().getX()))
                .email(profile.getEmail())
                .isActive(profile.getIsActive())
                .lastLogin(profile.getLastLogin())
                .registeredAt(profile.getRegisteredAt())
                .notificationsEnabled(profile.getNotificationsEnabled())
                .interests(interests)
                .facts(factMap)
                .photos(photoDTOs)
                .build();
    }

    @Override
    public Photo getProfilePhoto(Long profileId, Integer photoId) {
        return photoRepository.findByPositionAndProfileId(photoId, profileId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Photo not found for profile: " + profileId));
    }

    @Override
    public GetProfileResponseDTO updateProfile(Long profileId, UpdateProfileRequestDTO updateDTO) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        if (updateDTO.getName() != null) profile.setName(updateDTO.getName());
        if (updateDTO.getAge() != null) profile.setAge(updateDTO.getAge());
        if(updateDTO.getGender() != null) profile.setGender(updateDTO.getGender());
        if (updateDTO.getDescription() != null) profile.setDescription(updateDTO.getDescription());
        if (updateDTO.getLocation() != null) {
            profile.setLocation(geometryFactory.createPoint(
                    new Coordinate(updateDTO.getLocation().getLongitude(), updateDTO.getLocation().getLatitude()))
            );
        }
        if(updateDTO.getIsActive() != null) profile.setIsActive(updateDTO.getIsActive());
        if(updateDTO.getNotificationsEnabled() != null) profile.setNotificationsEnabled(updateDTO.getNotificationsEnabled());
        if(updateDTO.getEmail() != null) profile.setEmail(updateDTO.getEmail());

        if (updateDTO.getInterests() != null) {
            Set<Interest> interests = updateDTO.getInterests().stream()
                    .map(name -> interestRepository.findByName(name)
                            .orElseGet(() -> interestRepository.save(new Interest(name))))
                    .collect(Collectors.toSet());
            profile.setInterests(interests);
        }

        profile = profileRepository.save(profile);

        if (updateDTO.getFacts() != null) {
            profileFactRepository.deleteAll(profileFactRepository.findByProfile(profile));

            for (Map.Entry<UserFacts, String> entry : updateDTO.getFacts().entrySet()) {
                Fact fact = factRepository.findByName(entry.getKey())
                        .orElseGet(() -> factRepository.save(new Fact(entry.getKey())));

                ProfileFact pf = ProfileFact.builder()
                        .id(new ProfileFactId(profile.getId(), fact.getId()))
                        .profile(profile)
                        .fact(fact)
                        .factValue(entry.getValue())
                        .build();

                profileFactRepository.save(pf);
            }
        }

/*        if (updateDTO.getPhotosToDelete() != null) {
            profile.getPhotos().removeIf(photo ->
                    updateDTO.getPhotosToDelete().contains(photo.getPosition())
            );
        }*/

        if (updateDTO.getNewPhotos() != null) {
            photoRepository.deleteByProfileId(profileId);
            profile.getPhotos().clear();
            int newPhotoPosition = 1;
            for (MultipartFile file : updateDTO.getNewPhotos()) {
                try {
                    Photo photo = Photo.builder()
                            .profile(profile)
                            .image(file.getBytes())
                            .contentType(file.getContentType())
                            .fileName(file.getOriginalFilename())
                            .position(newPhotoPosition++)
                            .uploadedAt(LocalDateTime.now())
                            .build();
                    profile.getPhotos().add(photo);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload photo", e);
                }
            }
        }

        profileRepository.save(profile);

        return getProfile(profile.getId());
    }

    @Override
    public GetProfileResponseDTO deleteProfile(Long profileId, HttpServletRequest request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        String token = request.getHeader("Authorization");
        System.out.println(token);
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalStateException("Missing or invalid Authorization header");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        ResponseEntity<Void> response = authServiceRestTemplate.exchange(
                "http://localhost:9000/auth/delete/" + profileId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        if(!response.getStatusCode().is2xxSuccessful())
            return GetProfileResponseDTO.builder()
                    .message("Error on deletion" + profileId)
                    .build();

        profileFactRepository.deleteByProfileId(profileId);
        profile.getProfileFacts().clear();
        profileRepository.delete(profile);

        return GetProfileResponseDTO.builder()
                .profileId(profileId)
                .email(this.getEmailFromSecurityContext())
                .message("Profile with id " + profileId + " was deleted")
                .build();
    }

    private String getEmailFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getClaimAsString("email");
        }
        throw new RuntimeException("Unable to extract email from token");
    }
}
