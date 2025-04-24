package org.suai.users.model.dto.serverResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.suai.users.model.dto.serverRequest.LocationDTO;
import org.suai.users.model.enums.Gender;
import org.suai.users.model.enums.UserFacts;
import org.suai.users.model.enums.UserInterest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetProfileResponseDTO {

    private Long profileId;
    private String name;
    private Integer age;
    private Gender gender;
    private String description;
    private LocationDTO location;
    private List<UserInterest> interests;
    private Map<UserFacts, String> facts;
    private Boolean isActive;
    private Boolean notificationsEnabled;

    private String email;
    private LocalDateTime registeredAt;
    private LocalDateTime lastLogin;

    private String errorCode;

    private String message;
}
