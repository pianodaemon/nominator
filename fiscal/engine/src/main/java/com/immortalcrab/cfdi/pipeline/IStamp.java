package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

@FunctionalInterface
interface IStamp<V, T> {

    public T impress(final V target) throws FormatError;
}
