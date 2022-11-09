package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.InputStream;
import java.util.Optional;
import lombok.NonNull;

@Log4j
@AllArgsConstructor
class S3BucketStorage implements IStorage {

    private final @NonNull
    AmazonS3 amazonS3;

    @Override
    public void upload(
            final String cType,
            final long len,
            final String fileName,
            InputStream inputStream) throws StorageError {

        Optional<String> target = Optional.ofNullable(System.getenv("BUCKET_TARGET"));

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

    static public S3BucketStorage setupWithEnv() throws StorageError {

        return new S3BucketStorage(S3ClientHelper.setupWithEnv());
    }
}
