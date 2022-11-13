package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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

    @Test
    void loadReqFromReader() {

        try {
            InputStream is = _cloader.getResourceAsStream("jsonreqs/nominareq.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            NominaRequestDTO dto = new NominaRequestDTO(reader);

            // We verify the mandatory document attributes
            assertTrue("01".equals(dto.getDocAttributes().getExportacion()));
            assertTrue("RRM".equals(dto.getDocAttributes().getSerie()));
            assertTrue("5457".equals(dto.getDocAttributes().getFolio()));
            assertTrue("2022-10-26T20:00:18".equals(dto.getDocAttributes().getFecha()));
            assertTrue("MXN".equals(dto.getDocAttributes().getMoneda()));
            assertTrue(new BigDecimal("1185.82").equals(dto.getDocAttributes().getDescuento()));
            assertTrue(new BigDecimal("7185.82").equals(dto.getDocAttributes().getSubtotal()));
            assertTrue(new BigDecimal("6000.0").equals(dto.getDocAttributes().getTotal()));
            assertTrue("PUE".equals(dto.getDocAttributes().getMetodoPago()));
            assertTrue("67100".equals(dto.getDocAttributes().getLugarExpedicion()));

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

            // We verify the mandatory attributes of nomina´s complement
            assertTrue("2022-10-25".equals(dto.getNomAttributes().getFechaPago()));
            assertTrue("2022-10-16".equals(dto.getNomAttributes().getFechaInicialPago()));
            assertTrue("2022-10-31".equals(dto.getNomAttributes().getFechaFinalPago()));
            assertTrue(new BigDecimal("15").equals(dto.getNomAttributes().getDiasPagados()));
            assertTrue(new BigDecimal("7185.82").equals(dto.getNomAttributes().getTotalPercepciones()));
            assertTrue(new BigDecimal("1185.82").equals(dto.getNomAttributes().getTotalDeducciones()));
        } catch (RequestError | DecodeError ex) {
            assertNotNull(ex);
        }
    }
}
