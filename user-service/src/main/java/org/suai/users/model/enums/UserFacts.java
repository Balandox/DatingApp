package org.suai.users.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserFacts {
    HEIGHT, RELIGION, GOAL, CHILDREN, ALCOHOL, CIGARETTES;

    @JsonCreator
    public static UserFacts fromString(String value) {
        return value == null ? null : UserFacts.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
