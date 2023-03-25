package com.immortalcrab.cfdi.processor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(Lifecycle.PER_CLASS)
public class ResourceDescriptorTest {
    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @Test
    void loadResourceFromReader() {

        try {
            InputStream is = _cloader.getResourceAsStream("jsonprofiles/default.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            ResourceDescriptor resDescriptor = new ResourceDescriptor(reader);

            ResourceDescriptor.Prefixes prefixes = resDescriptor.getPrefixes();
            assertEquals("ssl", prefixes.getSsl());
            assertEquals("xslt", prefixes.getXslt());

            Optional<ResourceDescriptor.Issuer> subs = resDescriptor.getIssuer("RRM031001QE7");
            assertEquals("RRM031001QE7", subs.orElseThrow().getRfc());
            assertEquals("00001000000505709200.cer", subs.orElseThrow().getCer());
	    assertEquals("CSD_UNIDAD_RRM031001QE7_20201117_185818.pem", subs.orElseThrow().getPem());

            Optional<ResourceDescriptor.Pac> pac = resDescriptor.getPacSettings("servisim");
            assertEquals("servisim", pac.orElseThrow().getCarrier());
            assertEquals("1", pac.orElseThrow().getLogin());
            assertEquals("Oryx216", pac.orElseThrow().getPasswd());

        } catch (Exception ex) {
            assertNull(ex);
        }
    }
}
