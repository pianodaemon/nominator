package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

class ResourceFetchHelper {

    public static BufferedInputStream obtain(Pipeline.IStorage resources, Map<String, String> issuerAttribs, String prefix, String item) throws StorageError {
        Optional<String> prefixResource = resources.getPathPrefix(prefix);
        Optional<String> key = Optional.ofNullable(issuerAttribs.get(item));

        try {
            final String signerKEY = String.format("%s/%s", prefixResource.orElseThrow(), key.orElseThrow());
            return resources.download(signerKEY);
        } catch (NoSuchElementException ex) {
            throw new StorageError("The issuer's resource can not be obtained");
        }
    }
}
