package com.immortalcrab.cfdi.errors;

public enum ErrorCodes {

    SUCCESS(0),
    UNKNOWN_ISSUE(195),
    STORAGE_PROVIDEER_ISSUES(196), // Lack interacting with storage provideer entity
    PIPELINE_NOT_SPINNED_UP(197), // An element searched for the pipeline setup was not found
    REQUEST_INVALID(198), // It is not possible to consume request as it is comformed
    FORMAT_BUILDER_ISSUE(199), // Problems related to docbuilder factory stuff (missing builders)
    PAC_PARTY_ISSUES(200);         // Lack interacting with PAC party entity

    protected int code;

    ErrorCodes(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
