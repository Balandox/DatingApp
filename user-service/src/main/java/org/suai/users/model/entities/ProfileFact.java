package org.suai.users.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.suai.users.model.entities.pk.ProfileFactId;

@Data
@Builder
@Entity
@Table(name = "profile_facts")
@NoArgsConstructor
@AllArgsConstructor
public class ProfileFact {
    @EmbeddedId
    private ProfileFactId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("profileId")
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("factId")
    @JoinColumn(name = "fact_id", nullable = false)
    private Fact fact;

    @Column(name = "fact_value", length = 100)
    private String factValue;
}
