package com.example.shippingbotserver.contants;

import lombok.Getter;

public enum Constants {
    LIKE("like"),
    DISLIKE("dislike"),
    SUDAR_MALE("Сударь"),
    SUDAR_FEMALE("Сударыня"),
    BOY("boy"),
    GIRL("girl"),
    ALL("all"),
    RIGHT("Вправо"),
    LEFT("Влево"),
    MATCH("Взаимность"),
    YOU_ARE_LOVED("Вы любимы"),
    LOVE_YOU("Любимъ Вами");

    @Getter
    final String name;

    Constants(String name) {
        this.name = name;
    }
}
