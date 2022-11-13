package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;

class FacturaRequestDTO extends JsonRequest {

    public FacturaRequestDTO(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
    }

}
