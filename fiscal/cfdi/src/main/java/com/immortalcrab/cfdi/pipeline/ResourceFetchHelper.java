package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

class ResourceFetchHelper {

    static final String ISSUER_IDENTIFIER_ATTRIB = "rfc";
    static final String PREFIX_SSL = "prefix_ssl";
    static final String CER = "cer";
    static final String KEY = "key";

    public static BufferedInputStream obtain(Pipeline.IStorage resources, Map<String, String> issuerAttribs, String prefix, String item) throws StorageError {
        Optional<String> prefixResource = resources.getPathPrefix(prefix);
        Optional<String> baseName = Optional.ofNullable(issuerAttribs.get(item));
        Optional<String> issuerIdentifier = Optional.ofNullable(issuerAttribs.get(ISSUER_IDENTIFIER_ATTRIB));
        try {
            final String itemPath = String.format("%s/%s/%s", issuerIdentifier.orElseThrow(), prefixResource.orElseThrow(), baseName.orElseThrow());
            return resources.download(itemPath);
        } catch (NoSuchElementException ex) {
            throw new StorageError("The issuer's resource can not be obtained");
        }
    }

    public static BufferedInputStream obtainCert(Pipeline.IStorage resources, final Map<String, String> issuerAttribs) throws StorageError {

        return ResourceFetchHelper.obtain(resources, issuerAttribs, PREFIX_SSL, CER);
    }

    public static BufferedInputStream obtainKey(Pipeline.IStorage resources, Map<String, String> issuerAttribs) throws StorageError {

        return ResourceFetchHelper.obtain(resources, issuerAttribs, PREFIX_SSL, KEY);
    }

    private ResourceFetchHelper() {
        throw new IllegalStateException("Helper class");
    }
}
