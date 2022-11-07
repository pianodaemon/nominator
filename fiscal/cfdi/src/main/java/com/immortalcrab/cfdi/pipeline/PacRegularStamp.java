package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacRegularStamp implements IStamp<PacRegularRequest, PacRegularResponse> {

    @Override
    public PacRegularResponse impress(final PacRegularRequest target) throws FormatError {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static PacRegularStamp setupWithEnv() {

        // It is still pending to add code here
        return new PacRegularStamp();
    }
}
