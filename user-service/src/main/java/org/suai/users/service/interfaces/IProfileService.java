package org.suai.users.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.suai.users.model.dto.serverRequest.CreateProfileRequestDTO;
import org.suai.users.model.dto.serverRequest.UpdateProfileRequestDTO;
import org.suai.users.model.dto.serverResponse.GetProfileResponseDTO;

public interface IProfileService {

    GetProfileResponseDTO createProfile(CreateProfileRequestDTO requestDTO);

    GetProfileResponseDTO getProfile(Long profileId);

    GetProfileResponseDTO updateProfile(Long profileId, UpdateProfileRequestDTO updateRequestDTO);

    GetProfileResponseDTO deleteProfile(Long profileId, HttpServletRequest request);
}
