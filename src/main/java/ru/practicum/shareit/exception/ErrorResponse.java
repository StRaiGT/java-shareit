package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String Error;

    public ErrorResponse(String error) {
        Error = error;
    }
}
