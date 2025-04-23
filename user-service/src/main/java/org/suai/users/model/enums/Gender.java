package org.suai.users.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Gender {

    MALE("Мужской"),
    FEMALE("Женский");

    private final String displayName;

    @JsonCreator
    public static Gender fromValue(String value) {
        if (value == null) return null;

        for (Gender gender : values())
            if (gender.displayName.equalsIgnoreCase(value))
                return gender;

        throw new IllegalArgumentException("Unknown Gender value: " + value);
    }

    @JsonValue
    public String toValue() {
        return displayName;
    }
}
