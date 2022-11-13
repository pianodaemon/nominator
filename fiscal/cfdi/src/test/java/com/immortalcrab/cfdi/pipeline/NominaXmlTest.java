package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class NominaXmlTest {

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
    void loadReqFromReader() {

        try {
            InputStream is = _cloader.getResourceAsStream("jsonreqs/nominareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            NominaRequestDTO dto = new NominaRequestDTO(reader);

            // We verify the mandatory document attributes
            assertTrue("RRM".equals(dto.getDocAttributes().getSerie()));
            assertTrue("5457".equals(dto.getDocAttributes().getFolio()));
            assertTrue("2022-10-26T20:00:18".equals(dto.getDocAttributes().getFecha()));
            assertTrue("MXN".equals(dto.getDocAttributes().getMoneda()));
            assertTrue(new BigDecimal("1185.82").equals(dto.getDocAttributes().getDescuento()));
            assertTrue(new BigDecimal("7185.82").equals(dto.getDocAttributes().getSubtotal()));
            assertTrue(new BigDecimal("6000.0").equals(dto.getDocAttributes().getTotal()));

            // We verify the mandatory pseudo-emisor attributes
            assertTrue("RRM031001QE7".equals(dto.getPseudoEmisor().getRfc()));
            assertTrue("RR MEDICA S.A. DE C.V.".equals(dto.getPseudoEmisor().getNombre()));
            assertTrue("601".equals(dto.getPseudoEmisor().getRegimenFiscal()));

            // We verify the mandatory pseudo-receptor attributes
            assertTrue("TORG700702KZ5".equals(dto.getPseudoReceptor().getRfc()));
            assertTrue("JOSE GUADALUPE DE LA TORRE RIOS".equals(dto.getPseudoReceptor().getNombre()));
            assertTrue("45625".equals(dto.getPseudoReceptor().getDomicilioFiscal()));
            assertTrue("605".equals(dto.getPseudoReceptor().getRegimenFiscal()));
            assertTrue("CN01".equals(dto.getPseudoReceptor().getProposito()));

        } catch (RequestError | DecodeError ex) {
            assertNotNull(ex);
        }
    }
}
