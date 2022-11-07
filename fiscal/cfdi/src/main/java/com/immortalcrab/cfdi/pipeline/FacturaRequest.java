package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;

class FacturaRequest extends JsonRequest {

    private FacturaRequest(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
    }

    public static FacturaRequest render(InputStreamReader reader) throws RequestError, DecodeError {
        FacturaRequest facturaRequest = new FacturaRequest(reader);
        return facturaRequest;
    }
}
