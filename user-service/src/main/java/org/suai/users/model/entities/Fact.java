package org.suai.users.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.suai.users.model.enums.Gender;
import org.suai.users.model.enums.UserFacts;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "facts")
public class Fact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 30)
    private UserFacts name;

    public Fact(UserFacts name) {
        this.name = name;
    }
}
