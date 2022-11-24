package com.immortalcrab.cfdi.error;

public class FormatError extends EngineError {

    public FormatError(String message) {
        super(message, ErrorCodes.FORMAT_BUILDER_ISSUE);
    }

    public FormatError(String message, Throwable cause) {
        super(message, cause, ErrorCodes.FORMAT_BUILDER_ISSUE);
    }
}
