package org.suai.users.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserInterest {
    BASKETBALL("Баскетбол"), FOOTBALL("Футбол"), VOLLEYBALL("Воллейбол"), SKIING("Лыжи"),
    HIKING("Хайкинг"), GYM("Спортивный зал"),

    THEATRE("Театр"), CINEMA("Кино"), MUSIC("Музыка"), FASHION("Мода"),
    READING("Чтение"), TRIPS("Путешествия"), COOKING("Готовка");

    private final String displayName;

    @JsonCreator
    public static UserInterest fromValue(String value) {
        if (value == null) return null;

        for (UserInterest interest : values())
            if (interest.displayName.equalsIgnoreCase(value))
                return interest;

        throw new IllegalArgumentException("Unknown Interest value: " + value);
    }

    @JsonValue
    public String toValue() {
        return displayName;
    }
}
