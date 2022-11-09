package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.io.InputStream;

interface IStorage {

    public void upload(final String cType,
            final long len,
            final String fileName,
            InputStream inputStream) throws StorageError;

    public BufferedInputStream download(final String fileName) throws StorageError;
}
