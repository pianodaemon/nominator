package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.pipeline.IStorage;
import com.immortalcrab.cfdi.error.StorageError;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.NonNull;

import java.io.InputStream;
import java.util.Optional;

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

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(cType);
        objectMetadata.setContentLength(len);

        try {
            amazonS3.putObject(System.getenv("BUCKET_TARGET"), fileName, inputStream, objectMetadata);

        } catch (AmazonServiceException ex) {
            log.error(String.format("File %s can not be uploaded", fileName));
            throw new StorageError("A failure detected when attempting to write upon the bucket storage", ex);
        }
    }

    static public S3BucketStorage setupWithEnv() throws StorageError {

        Optional<String> region = Optional.ofNullable(System.getenv("BUCKET_REGION"));
        Optional<String> key = Optional.ofNullable(System.getenv("BUCKET_KEY"));
        Optional<String> secret = Optional.ofNullable(System.getenv("BUCKET_SECRET"));
        Optional<String> target = Optional.ofNullable(System.getenv("BUCKET_TARGET"));

        if (region.isEmpty()) {
            throw new StorageError("aws region was not fed");
        }

        if (key.isEmpty()) {
            throw new StorageError("aws key was not fed");
        }

        if (secret.isEmpty()) {
            throw new StorageError("aws secret was not fed");
        }

        if (target.isEmpty()) {
            throw new StorageError("aws bucket was not fed");
        }

        AWSCredentials awsCredentials = new BasicAWSCredentials(key.get(), secret.get());

        return new S3BucketStorage(
                AmazonS3ClientBuilder.standard().withRegion(
                        region.get()).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build());
    }
}
