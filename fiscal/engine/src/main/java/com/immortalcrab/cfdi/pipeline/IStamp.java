package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

@FunctionalInterface
interface IStamp<V extends PacReq, T extends PacRes> {

    public T impress(final V target) throws FormatError;
}
