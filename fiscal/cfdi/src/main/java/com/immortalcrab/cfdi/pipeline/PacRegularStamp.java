package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.Pipeline.IStamp;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacRegularStamp implements IStamp<PacRegularRequest, PacRes> {

    @Override
    public PacRes impress(final PacRegularRequest target) throws FormatError {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static PacRegularStamp setup(final String name, final String user, final String passwd) {

        // It is still pending to add code here
        return new PacRegularStamp();
    }
}
