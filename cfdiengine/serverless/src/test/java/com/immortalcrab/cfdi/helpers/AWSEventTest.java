package com.immortalcrab.cfdi.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class AWSEventTest {

    // If the event were changed, this value would be modified as well
    private static final Integer EXPECTED_HASH_CODE = -959941537;
    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

    @Test
    public void loadReqFromReader() {

        try {

            InputStream is = _cloader.getResourceAsStream("jsonevents/facturaevent.json");

            AWSEvent<Payload> body = AWSEvent.unmarshallEvent(new String(is.readAllBytes(), StandardCharsets.UTF_8), Payload.class);
            Payload payl = body.getDetail();
            Integer hc = payl.hashCode();

            assertTrue(hc.equals(EXPECTED_HASH_CODE));
        } catch (IOException ex) {
            assertNotNull(ex);
        }
    }
}
