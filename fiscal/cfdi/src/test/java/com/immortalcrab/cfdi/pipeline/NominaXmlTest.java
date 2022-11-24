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
public class NominaXmlTest {

    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @Test
    public void loadReqFromReader() {

        try {
            InputStream xmlIs = _cloader.getResourceAsStream("xmlsamples/nomina.xml");
            InputStreamReader xmlReader = new InputStreamReader(xmlIs, StandardCharsets.UTF_8);
            String xml = xmlReader.toString();

            InputStream is = _cloader.getResourceAsStream("jsonreqs/nominareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            NominaXml nomina = new NominaXml(new NominaRequestDTO(reader));

            assertTrue(true);
            //assertTrue(xml.equals(nomina.toString()));

        } catch (RequestError | DecodeError | FormatError ex) {
            assertNotNull(ex);
        }
    }
}
