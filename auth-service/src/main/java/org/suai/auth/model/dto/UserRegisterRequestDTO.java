package org.suai.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterRequestDTO {

    private String email;

    private String password;
}
