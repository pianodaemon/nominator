package com.immortalcrab.cfdi.error;

public class CfdiRequestError extends EngineError {

    public CfdiRequestError(String message) {
        super(message, ErrorCodes.REQUEST_INVALID);
    }

    public CfdiRequestError(String message, Throwable cause) {
        super(message, cause, ErrorCodes.REQUEST_INVALID);
    }
}
