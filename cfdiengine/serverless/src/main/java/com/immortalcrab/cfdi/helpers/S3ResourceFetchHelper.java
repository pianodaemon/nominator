package com.immortalcrab.cfdi.helpers;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.processor.Processor;
import com.immortalcrab.cfdi.processor.ResourceDescriptor.*;

import java.io.BufferedInputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class S3ResourceFetchHelper {

    public static BufferedInputStream obtain(Processor.IStorage resources, Map<String, String> issuerAttribs, String prefix, String item) throws EngineError {
        Optional<String> prefixResource = resources.getPathPrefix(prefix);
        Optional<String> baseName = Optional.ofNullable(issuerAttribs.get(item));
        Optional<String> issuerIdentifier = Optional.ofNullable(issuerAttribs.get(Issuer.K_RFC));
        try {
            final String itemPath = String.format("%s/%s/%s", issuerIdentifier.orElseThrow(), prefixResource.orElseThrow(), baseName.orElseThrow());
            return resources.download(itemPath);
        } catch (NoSuchElementException ex) {
            throw new EngineError("The issuer's resource can not be obtained", ex, ErrorCodes.STORAGE_PROVIDEER_ISSUES);
        }
    }

    public static BufferedInputStream obtainCert(Processor.IStorage resources, final Map<String, String> issuerAttribs) throws EngineError {

        return S3ResourceFetchHelper.obtain(resources, issuerAttribs, Prefixes.K_PREFIX_SSL, Issuer.K_CER);
    }

    public static BufferedInputStream obtainKey(Processor.IStorage resources, Map<String, String> issuerAttribs) throws EngineError {

        return S3ResourceFetchHelper.obtain(resources, issuerAttribs, Prefixes.K_PREFIX_SSL, Issuer.K_PEM);
    }

    private S3ResourceFetchHelper() {
        throw new IllegalStateException("Helper class");
    }
}
