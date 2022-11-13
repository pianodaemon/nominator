package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@TestInstance(Lifecycle.PER_CLASS)
public class FacturaRequestDTOTest {

    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
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
