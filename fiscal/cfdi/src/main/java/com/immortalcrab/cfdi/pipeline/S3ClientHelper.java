package com.immortalcrab.cfdi.pipeline;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.immortalcrab.cfdi.error.StorageError;

import java.util.Optional;

class S3ClientHelper {

    static public AmazonS3 setupWithEnv() throws StorageError {

        Optional<String> region = Optional.ofNullable(System.getenv("AWS_REGION"));
        Optional<String> key = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID"));
        Optional<String> secret = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY"));

        region.orElseThrow(() -> new StorageError("aws region was not fed"));
        key.orElseThrow(() -> new StorageError("aws key was not fed"));
        secret.orElseThrow(() -> new StorageError("aws secret was not fed"));

        AWSCredentials awsCredentials = new BasicAWSCredentials(key.get(), secret.get());

        return AmazonS3ClientBuilder.standard().withRegion(region.get()).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }

    private S3ClientHelper() {
        throw new IllegalStateException("Helper class");
    }
}
