package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;

@FunctionalInterface
interface IXmlStep<T extends PacRes> {

    public T render(Request cfdiReq, IStamp stamper) throws FormatError, StorageError;
}
