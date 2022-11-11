package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ProducerTest {

    FakeBucketService _fbs;
    FakeStorage _storage;
    FakeStamp _stamper;

    @BeforeEach
    void setup() throws StorageError {

        _fbs = new FakeBucketService();
        _storage = new FakeStorage(_fbs.engage(), "fake");
    }

    @AfterEach
    void teardown() {

        _fbs.shutdown();
    }

    @Test
    void verifyProduction() {

       /* try {

            new Producer(_stamper, _storage);

        } catch (StorageError ex) {
            assertNotNull(ex);
        }*/
    }
}
