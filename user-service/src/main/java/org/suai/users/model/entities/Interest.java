package org.suai.users.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.suai.users.model.enums.Gender;
import org.suai.users.model.enums.UserInterest;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interests")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 20)
    private UserInterest name;

    public Interest(UserInterest name) {
        this.name = name;
    }
}
