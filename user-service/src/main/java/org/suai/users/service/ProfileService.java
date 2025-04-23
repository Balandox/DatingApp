package org.suai.users.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.suai.users.model.dto.serverRequest.CreateProfileRequestDTO;
import org.suai.users.model.dto.serverRequest.LocationDTO;
import org.suai.users.model.dto.serverRequest.UpdateProfileRequestDTO;
import org.suai.users.model.dto.serverResponse.GetProfileResponseDTO;
import org.suai.users.model.entities.Fact;
import org.suai.users.model.entities.Interest;
import org.suai.users.model.entities.Profile;
import org.suai.users.model.entities.ProfileFact;
import org.suai.users.model.entities.pk.ProfileFactId;
import org.suai.users.model.enums.UserFacts;
import org.suai.users.model.enums.UserInterest;
import org.suai.users.repositories.FactRepository;
import org.suai.users.repositories.InterestRepository;
import org.suai.users.repositories.ProfileFactRepository;
import org.suai.users.repositories.ProfileRepository;
import org.suai.users.service.interfaces.IProfileService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProfileService implements IProfileService {

    private ProfileRepository profileRepository;

    private InterestRepository interestRepository;

    private FactRepository factRepository;

    private ProfileFactRepository profileFactRepository;

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
                .build();
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

        return getProfile(profile.getId());
    }

    @Override
    public GetProfileResponseDTO deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

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
