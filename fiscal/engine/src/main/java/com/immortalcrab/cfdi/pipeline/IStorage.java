package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

interface IStorage {

    public void upload(final String cType,
            final long len,
            final String fileName,
            InputStream inputStream) throws StorageError;

    public BufferedInputStream download(final String fileName) throws StorageError;

    public String getTargetName() throws StorageError;

    Optional<Map<String, String>> getPathPrefixes();

    default Optional<String> getPathPrefix(final String label) {

        if (getPathPrefixes().isPresent()) {

            Map<String, String> prefixes = getPathPrefixes().get();
            return Optional.ofNullable(prefixes.get(label));
        }

        return Optional.ofNullable(null);
    }
}
