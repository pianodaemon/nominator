package com.immortalcrab.cfdi.error;

public class StorageError extends EngineError {

    public StorageError(String message) {
        super(message, ErrorCodes.STORAGE_PROVIDEER_ISSUES);
    }

    public StorageError(String message, Throwable cause) {
        super(message, cause, ErrorCodes.STORAGE_PROVIDEER_ISSUES);
    }
}
