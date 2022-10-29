package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;

public class NominaRequest extends JsonRequest {

    private NominaRequest(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
    }

    public static NominaRequest render(InputStreamReader reader) throws RequestError, DecodeError {
        NominaRequest req = new NominaRequest(reader);
        return req;
    }
}
