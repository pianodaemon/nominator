package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.Request;
import com.immortalcrab.cfdi.pipeline.IStorage;

import javax.xml.datatype.DatatypeFactory;

import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CPais;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Optional;
import javax.xml.datatype.DatatypeConfigurationException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import mx.gob.sat.cfd._4.Comprobante;

@AllArgsConstructor
@Getter
public class FacturaXml {

    private final @NonNull Request cfdiReq;
    private final @NonNull IStorage st;

    private static final String NATIONAL_CURRENCY = "MXN";
    private static final String NO_CURRENCY = "XXX";

    private Comprobante shapeComprobanteTag(ObjectFactory cfdiFactory,
            final String fechor,
            final String serie,
            final String folio,
            final String nocert,
            final String certb64,
            final String emizip,
            final String moneda,
            final Optional<BigDecimal> tpocam,
            final BigDecimal subtot,
            final BigDecimal total,
            final String metpag,
            final String conpag,
            final String forpag
    ) throws DatatypeConfigurationException {

        Comprobante comprobante = cfdiFactory.createComprobante();
        comprobante.setVersion("4.0");
        comprobante.setLugarExpedicion(emizip);
        CMetodoPago metpagVal = CMetodoPago.fromValue(metpag);
        comprobante.setMetodoPago(metpagVal);
        comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
        comprobante.setTotal(total);
        comprobante.setMoneda(CMoneda.fromValue(moneda));

        if (tpocam.isPresent()
                && !moneda.equals(FacturaXml.NATIONAL_CURRENCY)
                && !moneda.equals(FacturaXml.NO_CURRENCY)) {

            comprobante.setTipoCambio(tpocam.get());
        }

        comprobante.setCertificado(certb64);
        comprobante.setSubTotal(subtot);
        comprobante.setCondicionesDePago(conpag);
        comprobante.setNoCertificado(nocert);
        comprobante.setFormaPago(forpag);
        comprobante.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(fechor));
        comprobante.setSerie(serie);
        comprobante.setFolio(folio);

        return comprobante;
    }

    private Comprobante.Emisor shapeEmisorTag(ObjectFactory cfdiFactory,
            final String emirfc,
            final String eminom,
            final String regimen) {

        Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();

        emisor.setRfc(emirfc);
        emisor.setNombre(eminom);
        emisor.setRegimenFiscal(regimen);

        return emisor;
    }

    public static String render(Request cfdiReq, IStorage st) throws FormatError, StorageError {

        FacturaXml ic = new FacturaXml(cfdiReq, st);
        StringWriter cfdi = ic.shape();

        return "It must be slightly implemented as it was in lola";
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();

        ObjectFactory cfdiFactory = new ObjectFactory();
        var cfdi = cfdiFactory.createComprobante();
        cfdi.setVersion("4.0");
        cfdi.setTipoDeComprobante(CTipoDeComprobante.I);

        return sw;
    }
}
