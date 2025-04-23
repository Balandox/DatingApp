package org.suai.gateway.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserRegisterRequestDTO {

    private String email;

    private String password;
}
