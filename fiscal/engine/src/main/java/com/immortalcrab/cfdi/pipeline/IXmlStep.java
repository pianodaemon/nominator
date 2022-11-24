package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;

@FunctionalInterface
interface IXmlStep<T extends PacRes, R extends Request> {

    public T render(R cfdiReq, IStamp stamper) throws FormatError, StorageError;
}