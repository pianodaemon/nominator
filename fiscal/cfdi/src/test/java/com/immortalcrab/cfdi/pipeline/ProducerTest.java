package com.immortalcrab.cfdi.pipeline;

import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.ImmutableMap;
import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.pipeline.Pipeline.IDecodeStep;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.javatuples.Pair;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junitpioneer.jupiter.SetEnvironmentVariable;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProducerTest {

    private FakeBucketService _fbs, _fbr;
    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @BeforeEach
    void setup() throws StorageError {

        _fbs = new FakeBucketService(18001);
        _fbr = new FakeBucketService(18002);
    }

    @AfterEach
    void teardown() {

        _fbr.shutdown();
        _fbs.shutdown();
    }

    @SetEnvironmentVariable(key = "BUCKET_RESOURCES", value = "cfdi-datares-dummy")
    @SetEnvironmentVariable(key = "BUCKET_DATA_LAKE", value = "cfdi-datalake-dummy")
    @Test
    void verifyProductionPipeline() {

        IStamp stamper = new FakeStamp();

        try {

            IStorage resources = new S3BucketStorage(_fbr.engage(), System.getenv("BUCKET_RESOURCES"));
            IStorage storage = new S3BucketStorage(_fbs.engage(), System.getenv("BUCKET_DATA_LAKE"));
            AmazonS3 client = _fbs.getClient().orElseThrow(() -> new StorageError("aws client was never initialized"));
            client.createBucket(storage.getTargetName());
            client.createBucket(resources.getTargetName());

            InputStream is = _cloader.getResourceAsStream("jsonreqs/nominareq.json");
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);

            Pair<IDecodeStep, IXmlStep> pair = new Pair<>((IDecodeStep) (InputStreamReader reader) -> {
                            try {
                                return new NominaRequestDTO(reader);
                            } catch (IOException ex) {
                                throw new DecodeError("Nomina request can not be decoded");
                            }
                        }, FakeXml::render);
            Producer producer = new Producer(stamper, storage, resources, ImmutableMap.of("fake", pair));

            producer.engage("fake", isr);

            BufferedInputStream buff = storage.download(expectedNameFormer(isr, storage.getTargetName()));
            assertTrue(buff.readAllBytes().length > 0);

        } catch (StorageError | PipelineError | RequestError | FormatError | DecodeError | IOException ex) {
            assertNotNull(ex);
        }
    }

    private String expectedNameFormer(InputStreamReader isr, String bucketName) throws  IOException, RequestError {

        NominaRequestDTO req = new NominaRequestDTO(isr);
        return String.format("%s/%s/%s.%s", bucketName, req.getDocAttributes().getSerie(), req.getDocAttributes().getFolio(), ".xml");
    }
}
