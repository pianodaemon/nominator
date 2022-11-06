package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.pipeline.JsonRequest;
import java.io.InputStreamReader;

class NominaRequest extends JsonRequest {

    private NominaRequest(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
    }

    public static NominaRequest render(InputStreamReader reader) throws RequestError, DecodeError {
        NominaRequest req = new NominaRequest(reader);
        return req;
    }
}