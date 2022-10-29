package com.immortalcrab.cfdi.pipeline.lola;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.pipeline.Request;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class FacturaRequest extends Request {

    public static FacturaRequest render(InputStreamReader reader) throws RequestError, DecodeError, IOException {

        ObjectMapper mapper = new ObjectMapper();

        TypeReference<Map<String, Object>> tr = new TypeReference<Map<String, Object>>() {
        };
        
        return new FacturaRequest(mapper.readValue(reader, tr));
    }

    public FacturaRequest(Map<String, Object> ds) {
        super(ds);
    }

    @Override
    protected Map<String, Object> craftImpt() throws RequestError {
        throw new UnsupportedOperationException("Not supported and needed yet.");
    }
}
