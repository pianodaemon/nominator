package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;

import java.io.InputStreamReader;

class FakeRequest extends JsonRequest {

    public FakeRequest(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
    }

    public static FakeRequest render(InputStreamReader reader) throws RequestError, DecodeError {

        FakeRequest req = new FakeRequest(reader);
        return req;
    }
}
