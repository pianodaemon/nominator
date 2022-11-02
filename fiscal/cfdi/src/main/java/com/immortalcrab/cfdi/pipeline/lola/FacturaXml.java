package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.Request;
import com.immortalcrab.cfdi.pipeline.IStorage;

import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CPais;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoFactor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;

@AllArgsConstructor
@CommonsLog
@Getter
public class FacturaXml {

    private final @NonNull
    Request cfdiReq;
    private final @NonNull
    IStorage st;

    private static final String NATIONAL_CURRENCY = "MXN";
    private static final String NO_CURRENCY = "XXX";

    private Comprobante shapeComprobanteTag(Request cfdiReq, ObjectFactory cfdiFactory) throws FormatError {

        try {

            Optional<Object> timeStamp = Optional.ofNullable(cfdiReq.getDs().get("time_stamp"));
            XMLGregorianCalendar timeStampGregorianCalendar = DatatypeFactory.
                    newInstance().newXMLGregorianCalendar((String) timeStamp.orElseThrow());

            Map<String, Object> controlDic = this.obtainControlMap();
            Optional<Object> serie = Optional.ofNullable(controlDic.get("serie"));
            Optional<Object> folio = Optional.ofNullable(controlDic.get("folio"));

            Map<String, Object> monedaDic = this.obtainMonedaMap();
            Optional<Object> moneda = Optional.ofNullable(monedaDic.get("iso_4217"));
            Optional<Object> tpocam = Optional.ofNullable(monedaDic.get("tipo_de_cambio"));

            Map<String, Object> totalesDic = this.obtainTotalesMap();
            Optional<Object> subtot = Optional.ofNullable(totalesDic.get("importe_sum"));
            Optional<Object> total = Optional.ofNullable(totalesDic.get("monto_total"));

            Map<String, Object> formaPagoDic = this.obtainFormaPagoMap();
            Optional<Object> clave = Optional.ofNullable(formaPagoDic.get("clave"));

            Optional<Object> emizip = Optional.ofNullable(cfdiReq.getDs().get("lugar_expedicion"));
            Optional<Object> nocert = Optional.ofNullable(cfdiReq.getDs().get("numero_certificado"));
            Optional<Object> metpago = Optional.ofNullable(cfdiReq.getDs().get("metodo_pago"));

            return LegoTagAssembler.yieldComprobante(
                    cfdiFactory,
                    timeStampGregorianCalendar,
                    (String) serie.orElseThrow(),
                    (String) folio.orElseThrow(),
                    (String) nocert.orElseThrow(),
                    "",
                    (String) emizip.orElseThrow(),
                    (String) moneda.orElseThrow(),
                    Optional.ofNullable(tpocam.isPresent() ? new BigDecimal((String) tpocam.get()) : null),
                    new BigDecimal((String) subtot.orElseThrow()),
                    new BigDecimal((String) total.orElseThrow()),
                    (String) metpago.orElseThrow(),
                    "",
                    (String) clave.orElseThrow());
        } catch (DatatypeConfigurationException ex) {
            log.error("The time stamp is bad formated");
            throw new FormatError("time stamp incorrect format", ex);
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements is missing");
            throw new FormatError("mandatory element in request is missing", ex);
        }
    }

    public static String render(Request cfdiReq, IStorage st) throws FormatError, StorageError {

        FacturaXml ic = new FacturaXml(cfdiReq, st);
        ObjectFactory cfdiFactory = new ObjectFactory();

        Comprobante comprobante = ic.shapeComprobanteTag(cfdiReq, cfdiFactory);

        StringWriter sw = new StringWriter();

        return "It must be slightly implemented as it was in lola";
    }

    private Map<String, Object> obtainControlMap() throws NoSuchElementException {
        return LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "control");
    }

    private Map<String, Object> obtainMonedaMap() throws NoSuchElementException {

        return LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "moneda");
    }

    private Map<String, Object> obtainTotalesMap() throws NoSuchElementException {
        return LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "totales");
    }

    private Map<String, Object> obtainFormaPagoMap() throws NoSuchElementException {
        return LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "forma_pago");
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoConcepto {

        private String claveProdServ;
        private String claveUnidad;
        private String unidad;
        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal valorUnitario;
        private BigDecimal importe;

        public Comprobante.Conceptos.Concepto shapeConceptoTag(
                ObjectFactory cfdiFactory) {

            Comprobante.Conceptos.Concepto c = cfdiFactory.createComprobanteConceptosConcepto();

            c.setClaveProdServ(this.getClaveProdServ());
            c.setCantidad(this.getCantidad());
            c.setClaveUnidad(this.getClaveUnidad());
            c.setUnidad(this.getUnidad());
            c.setDescripcion(this.getDescripcion());
            c.setValorUnitario(this.getValorUnitario());
            c.setImporte(this.getImporte());

            return c;
        }
    }

    private static class LegoTagAssembler {

        private static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {

            Optional<Object> dict = Optional.ofNullable(m.get(k));
            return (Map<String, Object>) dict.orElseThrow();
        }

        private static Comprobante yieldComprobante(
                ObjectFactory cfdiFactory,
                final XMLGregorianCalendar fechor,
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
        ) {

            Comprobante comprobante = cfdiFactory.createComprobante();
            comprobante.setVersion("4.0");
            comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
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
            comprobante.setFecha(fechor);
            comprobante.setSerie(serie);
            comprobante.setFolio(folio);

            return comprobante;
        }
    }

    private static Comprobante.Receptor yieldReceptor(
            ObjectFactory cfdiFactory,
            final String cterfc,
            final String ctenom,
            CUsoCFDI uso) {

        Comprobante.Receptor rec = cfdiFactory.createComprobanteReceptor();
        rec.setRfc(cterfc);
        rec.setNombre(ctenom);
        rec.setUsoCFDI(uso);

        return rec;
    }

    private static Comprobante.Emisor yieldEmisor(
            ObjectFactory cfdiFactory,
            final String emirfc,
            final String eminom,
            final String regimen) {

        Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();

        emisor.setRfc(emirfc);
        emisor.setNombre(eminom);
        emisor.setRegimenFiscal(regimen);

        return emisor;
    }

    public static Comprobante.Conceptos yieldConceptos(
            ObjectFactory cfdiFactory,
            List<PseudoConcepto> listPscs) {

        Comprobante.Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

        for (PseudoConcepto psc : listPscs) {
            conceptos.getConcepto().add(psc.shapeConceptoTag(cfdiFactory));
        }

        return conceptos;
    }
}
