package com.immortalcrab.cfdi.pipeline;

import com.amazonaws.services.s3.AmazonS3;
import com.immortalcrab.cfdi.error.StorageError;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ProducerTest {
    
    static final String DEFAULT_BUCKET = "PACE8001104V2/defaultbucket";
    
    FakeBucketService _fbs;
    FakeStorage _storage;
    FakeStamp _stamper;
    
    @BeforeEach
    void setup() throws StorageError {
        
        _fbs = new FakeBucketService();
        _storage = new FakeStorage(_fbs.engage(), DEFAULT_BUCKET);
    }
    
    @AfterEach
    void teardown() {
        
        _fbs.shutdown();
    }
    
    @Test
    void verifyProduction() {
        
        try {
            
            AmazonS3 client = _fbs.getClient().orElseThrow(() -> new StorageError("aws client was never initialized"));
            client.createBucket(DEFAULT_BUCKET);
            /* try {
            
            new Producer(_stamper, _storage);
            
            } catch (StorageError ex) {
            assertNotNull(ex);
            }*/
        } catch (StorageError ex) {
            Logger.getLogger(ProducerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
