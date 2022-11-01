package com.immortalcrab.cfdi.toolbox.lola;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.pipeline.lola.FacturaRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.immortalcrab.cfdi.pipeline.lola.FacturaXml;

@TestInstance(Lifecycle.PER_CLASS)
public class FacturaXmlTest {

    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @AfterAll
    public void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void loadReqFromReader() {

        try {

            InputStream is = _cloader.getResourceAsStream("jsonreqs/facturareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

            FacturaRequest.render(reader);

        } catch (RequestError | DecodeError ex) {
            assertNotNull(ex);
        }
    }
}
