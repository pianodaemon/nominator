package com.immortalcrab.cfdi.pipeline;

import com.amazonaws.services.s3.AmazonS3;

import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Optional;

public class FakeStorage implements IStorage {

    private final AmazonS3 _amazonS3;

    private final Optional<String> _target;

    public FakeStorage(AmazonS3 amazonS3, Optional<String> target) {

        _amazonS3 = amazonS3;
        _target = target;
    }

    public FakeStorage(AmazonS3 amazonS3, final String target) {

        this(amazonS3, Optional.ofNullable(target));
    }

    @Override
    public void upload(String cType, long len, String fileName, InputStream inputStream) throws StorageError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public BufferedInputStream download(String fileName) throws StorageError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
