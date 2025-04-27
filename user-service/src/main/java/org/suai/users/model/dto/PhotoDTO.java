package org.suai.users.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private String contentType;
    private String fileName;
    private Integer position;
    private LocalDateTime uploadedAt;
    private String downloadUri;
}
