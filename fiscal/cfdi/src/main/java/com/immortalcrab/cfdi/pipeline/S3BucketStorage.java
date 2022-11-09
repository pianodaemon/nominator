package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Optional;
import lombok.NonNull;

@AllArgsConstructor
@Log4j
class S3BucketStorage implements IStorage {

    private final @NonNull
    AmazonS3 amazonS3;

    private final @NonNull
    Optional<String> target;

    public S3BucketStorage(AmazonS3 amazonS3, final String target) {

        this(amazonS3, Optional.ofNullable(target));
    }

    @Override
    public void upload(
            final String cType,
            final long len,
            final String fileName,
            InputStream inputStream) throws StorageError {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(cType);
        objectMetadata.setContentLength(len);

        try {

            amazonS3.putObject(
                    target.orElseThrow(() -> new StorageError("aws bucket was not fed")),
                    fileName, inputStream,
                    objectMetadata);

        } catch (AmazonServiceException ex) {
            log.error(String.format("File %s can not be uploaded", fileName));
            throw new StorageError("A failure detected when attempting to write upon the bucket storage", ex);
        }
    }

    @Override
    public BufferedInputStream download(final String fileName) throws StorageError {

        GetObjectRequest gobj = new GetObjectRequest(target.orElseThrow(() -> new StorageError("aws bucket was not fed")), fileName);
        S3Object object = amazonS3.getObject(gobj);

        return new BufferedInputStream(object.getObjectContent());
    }
}
