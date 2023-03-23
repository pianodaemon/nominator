package com.immortalcrab.cfdi.helpers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import java.util.Optional;

public class S3ClientHelper {

    public static AmazonS3 setupWithEnv(boolean seekout) throws EngineError {

        if (!seekout) {
            return AmazonS3ClientBuilder.standard().build();
        }

        Optional<String> region = Optional.ofNullable(System.getenv("AWS_REGION"));
        Optional<String> key = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID"));
        Optional<String> secret = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY"));

        AWSCredentials awsCredentials = new BasicAWSCredentials(
                key.orElseThrow(() -> new EngineError("aws key was not fed", ErrorCodes.STORAGE_PROVIDEER_ISSUES)),
                secret.orElseThrow(() -> new EngineError("aws secret was not fed", ErrorCodes.STORAGE_PROVIDEER_ISSUES)));

        return AmazonS3ClientBuilder.standard()
                .withRegion(region.orElseThrow(() -> new EngineError("aws region was not fed", ErrorCodes.STORAGE_PROVIDEER_ISSUES)))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }

    private S3ClientHelper() {
        throw new IllegalStateException("Helper class");
    }
}
