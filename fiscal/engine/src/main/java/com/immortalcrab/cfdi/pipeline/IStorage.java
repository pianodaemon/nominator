package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import java.io.InputStream;

@FunctionalInterface
public interface IStorage {

    public void upload(final String cType,
            final long len,
            final String fileName,
            InputStream inputStream) throws StorageError;
}