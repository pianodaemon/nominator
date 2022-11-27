package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;

@FunctionalInterface
interface IDecodeStep<R extends Request> {

    public R render(InputStreamReader ir) throws RequestError, DecodeError;
}
