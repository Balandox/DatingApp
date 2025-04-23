package org.suai.users.model.entities.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProfileFactId implements Serializable {
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "fact_id", nullable = false)
    private Long factId;
}
