package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacRegularStamp implements IStamp<PacRegularRequest, PacRes> {

    @Override
    public PacRes impress(final PacRegularRequest target) throws FormatError {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static PacRegularStamp setup(ResourceDescriptor.Pac pac) {

        // It is still pending to add code here
        return new PacRegularStamp();
    }
}
