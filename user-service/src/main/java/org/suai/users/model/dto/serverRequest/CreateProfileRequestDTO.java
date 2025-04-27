package org.suai.users.model.dto.serverRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import org.suai.users.model.dto.PhotoDTO;
import org.suai.users.model.enums.Gender;
import org.suai.users.model.enums.UserFacts;
import org.suai.users.model.enums.UserInterest;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CreateProfileRequestDTO {

    private String name;

    private Integer age;

    private Gender gender;

    private String description;

    private LocationDTO location;

    private List<UserInterest> interests; // список интересов

    private Map<UserFacts, String> facts; // пример: {"RELIGION": "Христианство", "GOAL": "дружба"}

    private List<MultipartFile> photos;
}
