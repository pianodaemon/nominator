package com.immortalcrab.cfdi.processor;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.processor.Processor.IStorage;

import lombok.AllArgsConstructor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
class S3BucketStorage implements IStorage {

    private final AmazonS3 amazonS3;

    private final Optional<String> target;

    private Map<String, String> prefixes;

    public S3BucketStorage(AmazonS3 amazonS3, final String target) {

        this(amazonS3, Optional.ofNullable(target), null);
    }

    @Override
    public void upload(
            final String cType,
            final long len,
            final String fileName,
            InputStream inputStream) throws EngineError {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(cType);
        objectMetadata.setContentLength(len);

        try {
            amazonS3.putObject(
                    getTargetName(),
                    fileName, inputStream,
                    objectMetadata);
        } catch (AmazonServiceException ex) {
            log.error(String.format("File %s can not be uploaded", fileName));
            throw new EngineError("A failure detected when attempting to write upon the bucket storage",
                    ex, ErrorCodes.STORAGE_PROVIDEER_ISSUES);
        }
    }

    @Override
    public BufferedInputStream download(final String fileName) throws EngineError {

        GetObjectRequest gobj = new GetObjectRequest(target.orElseThrow(
                () -> new EngineError("aws bucket was not fed", ErrorCodes.STORAGE_PROVIDEER_ISSUES)),
                fileName);
        S3Object object = amazonS3.getObject(gobj);

        return new BufferedInputStream(object.getObjectContent());
    }

    @Override
    public String getTargetName() throws EngineError {
        return target.orElseThrow(
                () -> new EngineError("aws bucket was not fed", ErrorCodes.STORAGE_PROVIDEER_ISSUES));
    }

    @Override
    public Optional<Map<String, String>> getPathPrefixes() {
        return Optional.ofNullable(prefixes);
    }

    public void setPathPrefixes(Map<String, String> pathPrefixes) {
        prefixes = pathPrefixes;
    }
}
