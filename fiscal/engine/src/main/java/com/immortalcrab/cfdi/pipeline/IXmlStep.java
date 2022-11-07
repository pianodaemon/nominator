package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;

@FunctionalInterface
interface IXmlStep {

    public String render(Request cfdiReq, IStamp stamper, IStorage st) throws FormatError, StorageError;
}
