package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
            FacturaRequestDTO dto = new FacturaRequestDTO(reader);

            // We verify the mandatory document attributes
            assertTrue("RRM".equals(dto.getComprobanteAttributes().getSerie()));
            assertTrue("5457".equals(dto.getComprobanteAttributes().getFolio()));
            assertTrue("2022-10-11T20:37:18".equals(dto.getComprobanteAttributes().getFecha()));
            assertTrue("03".equals(dto.getComprobanteAttributes().getFormaPago()));
            assertTrue(new BigDecimal("100.0").equals(dto.getComprobanteAttributes().getSubTotal()));
            assertTrue(new BigDecimal("0.0").equals(dto.getComprobanteAttributes().getDescuento()));
            assertTrue("MXN".equals(dto.getComprobanteAttributes().getMoneda()));
            assertTrue(new BigDecimal("116.0").equals(dto.getComprobanteAttributes().getTotal()));
            assertTrue("01".equals(dto.getComprobanteAttributes().getExportacion()));
            assertTrue("PUE".equals(dto.getComprobanteAttributes().getMetodoPago()));
            assertTrue("67100".equals(dto.getComprobanteAttributes().getLugarExpedicion()));

            // We verify the mandatory pseudo-emisor attributes
            assertTrue("RRM031001QE7".equals(dto.getEmisorAttributes().getRfc()));
            assertTrue("RR MEDICA S.A. DE C.V.".equals(dto.getEmisorAttributes().getNombre()));
            assertTrue("601".equals(dto.getEmisorAttributes().getRegimenFiscal()));

            // We verify the mandatory pseudo-receptor attributes
            assertTrue("IMS421231I45".equals(dto.getReceptorAttributes().getRfc()));
            assertTrue("INSTITUTO MEXICANO DEL SEGURO SOCIAL/ HOSPITAL GENERAL DE ZONA #12".equals(dto.getReceptorAttributes().getNombre()));
            assertTrue("G01".equals(dto.getReceptorAttributes().getUsoCfdi()));

            // We verify the only concepto that exists
            final int firstExpected = 0;
            {
                var item = dto.getPseudoConceptos().get(firstExpected);
                assertTrue("42181606".equals(item.getClaveProdServ()));
                assertTrue("379.107.0109.00.01".equals(item.getNoIdentificacion()));
                assertTrue(new BigDecimal("1.0").equals(item.getCantidad()));
                assertTrue("H87".equals(item.getClaveUnidad()));
                assertTrue("379.107.0109.00.01 BRAZALETE ADULTO ESTANDAR. MARCA: DATEX-OHMEDA. MODELO: AESTIVA. NUMERO DE CATALOGO: 572435.".equals(item.getDescripcion()));
                assertTrue(new BigDecimal("100.0").equals(item.getValorUnitario()));
                assertTrue(new BigDecimal("100.0").equals(item.getImporte()));

                // Concepto's Traslado check
                final int first = 0;
                var traslItem = item.getTraslados().get(first);
                assertTrue(new BigDecimal("100.0").equals(traslItem.getBase()));
                assertTrue("002".equals(traslItem.getImpuesto()));
                assertTrue("Tasa".equals(traslItem.getTipoFactor()));
                assertTrue(new BigDecimal("0.16").equals(traslItem.getTasaOCuota()));
                assertTrue(new BigDecimal("16.0").equals(traslItem.getImporte()));
            }

            // Impuestos check
            assertTrue(new BigDecimal("0.0").equals(dto.getImpuestosAttributes().getTotalImpuestosRetenidos()));
            assertTrue(new BigDecimal("16.0").equals(dto.getImpuestosAttributes().getTotalImpuestosTrasladados()));

            final int firstImpTrasl = 0;
            {
                var impTraslItem = dto.getImpuestosTraslados().get(firstImpTrasl);
                assertTrue("002".equals(impTraslItem.getImpuesto()));
                assertTrue("Tasa".equals(impTraslItem.getTipoFactor()));
                assertTrue(new BigDecimal("0.16").equals(impTraslItem.getTasaOCuota()));
                assertTrue(new BigDecimal("16.0").equals(impTraslItem.getImporte()));
            }

        } catch (RequestError | DecodeError ex) {
            assertNotNull(ex);
        }
    }
}
