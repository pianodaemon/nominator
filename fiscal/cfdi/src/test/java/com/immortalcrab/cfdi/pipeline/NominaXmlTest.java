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
public class NominaXmlTest {

    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @Test
    public void loadReqFromReader() {

        try {
            InputStream isTesting = _cloader.getResourceAsStream("xmlsamples/nomina.xml");
            String nominaTesting = new String(isTesting.readAllBytes(), StandardCharsets.UTF_8);

            InputStream is = _cloader.getResourceAsStream("jsonreqs/nominareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            NominaXml nominaGenerated = new NominaXml(new NominaRequestDTO(reader));

            char[] csc0 = nominaGenerated.toString().replace("\n", "").replace("\r", "").toCharArray();
            char[] csc1 = nominaTesting.toString().replace("\n", "").replace("\r", "").toCharArray();
            for (int i = 0; i < csc0.length; i++) {
                assertTrue(csc0[i] == csc1[i]);
            }

        } catch (Exception ex) {
            assertNull(ex);
        }
    }
}
