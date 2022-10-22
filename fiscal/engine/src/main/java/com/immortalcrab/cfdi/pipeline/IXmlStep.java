package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;

@FunctionalInterface
public interface IXmlStep {

    public String render(Request cfdiReq, IStorage st) throws FormatError, StorageError;
}
