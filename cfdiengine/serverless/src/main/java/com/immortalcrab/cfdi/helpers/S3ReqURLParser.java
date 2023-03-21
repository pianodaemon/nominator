package com.immortalcrab.cfdi.helpers;

import com.amazonaws.services.s3.AmazonS3URI;

public class S3ReqURLParser {

    private static final String PATH_SEPARATOR = "/";
    private final String bucket;
    private final String key;
    private final String[] particles;

    public static S3ReqURLParser parse(final String reqUrl) {

        AmazonS3URI awsS3URI = new AmazonS3URI(reqUrl);
        return new S3ReqURLParser(awsS3URI);
    }

    private S3ReqURLParser(AmazonS3URI awsS3URI) {

        bucket = awsS3URI.getBucket();
        particles = awsS3URI.getKey().split(PATH_SEPARATOR);
        key = awsS3URI.getKey();
    }

    public enum URIParticles {

        ISSUER,
        GROUP_OF_RESOURCES,
        KIND,
        LABEL,
        EVENT,
        MAX_URI_PARTICLE;
    }

    public String getBucket() {
        return bucket;
    }

    public String[] getParticles() {
        return particles;
    }

    public String getKey() {
        return key;
    }
}
