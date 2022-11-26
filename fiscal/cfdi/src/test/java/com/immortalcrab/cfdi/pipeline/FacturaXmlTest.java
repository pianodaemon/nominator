package com.immortalcrab.cfdi.pipeline;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertNull;
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
            String facturaTesting = new String(isTesting.readAllBytes(), StandardCharsets.UTF_8);

            InputStream is = _cloader.getResourceAsStream("jsonreqs/facturareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            FacturaXml facturaGenerated = new FacturaXml(new FacturaRequestDTO(reader));

            char[] csc0 = facturaGenerated.toString().replace("\n", "").replace("\r", "").toCharArray();
            char[] csc1 = facturaTesting.toString().replace("\n", "").replace("\r", "").toCharArray();
            for (int i = 0; i < csc0.length; i++) {
                assertTrue(csc0[i] == csc1[i]);
            }

        } catch (Exception ex) {
            assertNull(ex);
        }
    }
}
