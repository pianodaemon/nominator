package com.immortalcrab.cfdi.pipeline;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import io.findify.s3mock.S3Mock;
import java.util.Optional;

import com.immortalcrab.cfdi.error.StorageError;

class FakeBucketService {

    private static final String FAKE_SERVICE_IP = "127.0.0.1";
    private static final Integer FAKE_SERVICE_LOCAL_PORT = 8001;

    private final S3Mock _api;
    private AmazonS3 _client;

    public FakeBucketService() {

        this._api = new S3Mock.Builder().withPort(FAKE_SERVICE_LOCAL_PORT).withInMemoryBackend().build();
    }

    public AmazonS3 engage() throws StorageError {
        _api.start();
        _client = this.setupClient();
        return _client;
    }

    public void shutdown() {
        _api.stop();
    }

    private AmazonS3 setupClient() throws StorageError {

        EndpointConfiguration endpoint = new EndpointConfiguration(
                String.format("http://%s:%d", FAKE_SERVICE_IP, FAKE_SERVICE_LOCAL_PORT),
                "us-west-2");

        return AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();

    }

    Optional<AmazonS3> getClient() {
        return Optional.ofNullable(_client);
    }
}
