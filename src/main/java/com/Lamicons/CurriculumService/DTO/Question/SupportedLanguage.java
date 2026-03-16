package com.Lamicons.CurriculumService.DTO.Question;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SupportedLanguage {
    CPP("cpp"),
    JAVA("java"),
    RUST("rust"),
    JS("js"),
    TS("ts"),
    PY("py");

    private final String value;

    SupportedLanguage(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
