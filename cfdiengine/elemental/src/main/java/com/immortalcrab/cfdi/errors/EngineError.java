package com.immortalcrab.cfdi.errors;

public final class EngineError extends Exception {

    final int errorCode;

    public EngineError(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public EngineError(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCodes.UNKNOWN_ISSUE.getCode();
    }

    public EngineError(String message, Throwable cause, ErrorCodes errorCode) {
        super(message, cause);
        this.errorCode = errorCode.getCode();
    }

    public int getErrorCode() {
        return errorCode;
    }
}
