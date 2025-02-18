package com.example.minglethedog.common;

import lombok.Getter;

@Getter
public enum EntityType {
    FOLLOW("follow"),
    POST("post"),
    JWT("jwt"),
    USER("user"),
    ERROR("error");

    private final String value;

    EntityType(String value) {
        this.value = value;
    }
}
