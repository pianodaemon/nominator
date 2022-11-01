package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;

@FunctionalInterface
public interface IDecodeStep {

    public Request render(InputStreamReader inReader) throws RequestError, DecodeError;
}
