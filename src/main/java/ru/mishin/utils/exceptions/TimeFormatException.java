package ru.mishin.utils.exceptions;

import java.io.IOException;

public class TimeFormatException extends IOException {
    public TimeFormatException(String text) {
        super(text);
    }
}
