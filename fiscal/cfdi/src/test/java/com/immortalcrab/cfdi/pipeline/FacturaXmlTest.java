package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.RequestError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class FacturaXmlTest {

    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @Test
    public void loadReqFromReader() {

        try {
            InputStream isTesting = _cloader.getResourceAsStream("xmlsamples/factura.xml");
            InputStreamReader readerTesting = new InputStreamReader(isTesting, StandardCharsets.UTF_8);
            String facturaTesting = readerTesting.toString();

            InputStream is = _cloader.getResourceAsStream("jsonreqs/facturareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            FacturaXml facturaGenerated = new FacturaXml(new FacturaRequestDTO(reader));

            assertTrue(facturaTesting.equals(facturaGenerated.toString()));

        } catch (RequestError | FormatError | DecodeError ex) {
            assertNotNull(ex);
        }
    }
}
