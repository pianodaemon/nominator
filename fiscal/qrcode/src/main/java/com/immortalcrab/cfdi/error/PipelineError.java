package com.immortalcrab.cfdi.error;

public class PipelineError extends EngineError {

    public PipelineError(String message) {
        super(message, ErrorCodes.PIPELINE_NOT_SPINNED_UP);
    }

    public PipelineError(String message, Throwable cause) {
        super(message, cause, ErrorCodes.PIPELINE_NOT_SPINNED_UP);
    }
}
