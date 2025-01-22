package ru.unus.sonus.exception;

import com.thamuz.gprc.coordinator.NavigateErrorCode;
import lombok.Getter;

@Getter
public class NavigateException extends RuntimeException {

    private NavigateErrorCode errorCode;

    public NavigateException(NavigateErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
