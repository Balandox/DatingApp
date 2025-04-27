package org.suai.users.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.suai.users.model.dto.serverRequest.CreateProfileRequestDTO;
import org.suai.users.model.dto.serverRequest.UpdateProfileRequestDTO;
import org.suai.users.model.dto.serverResponse.GetProfileResponseDTO;
import org.suai.users.model.entities.Photo;

import java.util.List;

public interface IProfileService {

    GetProfileResponseDTO createProfile(CreateProfileRequestDTO profileData);

    GetProfileResponseDTO getProfile(Long profileId);

    GetProfileResponseDTO updateProfile(Long profileId, UpdateProfileRequestDTO updateRequestDTO);

    GetProfileResponseDTO deleteProfile(Long profileId, HttpServletRequest request);

    Photo getProfilePhoto(Long profileId, Integer photoPosition);
}
