package com.immortalcrab.cfdi.error;

public class RequestError extends EngineError {

    public RequestError(String message) {
        super(message, ErrorCodes.REQUEST_INVALID);
    }

    public RequestError(String message, Throwable cause) {
        super(message, cause, ErrorCodes.REQUEST_INVALID);
    }
}
