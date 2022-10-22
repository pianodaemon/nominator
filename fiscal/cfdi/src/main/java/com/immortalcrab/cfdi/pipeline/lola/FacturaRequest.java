package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.pipeline.Request;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;


public class FacturaRequest extends Request {

    public static FacturaRequest render(InputStreamReader reader) throws RequestError, DecodeError {

        return new FacturaRequest(new TreeMap<>());
    }

    public FacturaRequest(Map<String, Object> ds) {
        super(ds);
    }

    @Override
    protected Map<String, Object> craftImpt() throws RequestError {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
