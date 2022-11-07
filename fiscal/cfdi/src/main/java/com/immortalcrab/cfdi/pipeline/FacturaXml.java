package com.immortalcrab.cfdi.pipeline;

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
import java.util.LinkedList;
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
import lombok.extern.log4j.Log4j;

@AllArgsConstructor
@Log4j
@Getter
class FacturaXml {

    private final @NonNull
    Request cfdiReq;
    private final @NonNull
    IStorage st;

    private static final String CFDI_PREFIX = "cfdi";
    private static final String CFDI_URI = "http://www.sat.gob.mx/cfd/4";
    private static final String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

    private static final String NATIONAL_CURRENCY = "MXN";
    private static final String NO_CURRENCY = "XXX";

    private List<PseudoConcepto> shapePcs() throws FormatError {

        Optional<Object> cs = LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "conceptos");
        List<PseudoConcepto> pcs = new LinkedList<>();

        try {

            List<Map<String, Object>> items = (List<Map<String, Object>>) cs.orElseThrow();

            items.stream().map(i -> {

                PseudoConcepto p = new PseudoConcepto();
                p.setClaveProdServ((String) LegoTagAssembler.obtainObjFromKey(i, "prodserv").orElseThrow());
                p.setDescripcion((String) LegoTagAssembler.obtainObjFromKey(i, "descripcion").orElseThrow());
                p.setUnidad((String) LegoTagAssembler.obtainObjFromKey(i, "unidad").orElseThrow());
                p.setSku((String) LegoTagAssembler.obtainObjFromKey(i, "sku").orElseThrow());

                return p;

            }).forEachOrdered(p -> {
                pcs.add(p);
            });

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Concepto tag is missing");
            throw new FormatError("mandatory element in request is missing", ex);
        }

        return pcs;
    }

    private Comprobante.Conceptos shapeConceptosTag(
            ObjectFactory cfdiFactory,
            List<PseudoConcepto> listPscs) {

        Comprobante.Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

        for (PseudoConcepto psc : listPscs) {
            conceptos.getConcepto().add(psc.shapeConceptoTag(cfdiFactory));
        }

        return conceptos;
    }

    private Comprobante.Receptor shapeReceptorTag(ObjectFactory cfdiFactory) throws FormatError {

        Comprobante.Receptor rec = cfdiFactory.createComprobanteReceptor();

        try {

            Map<String, Object> dic = LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "receptor");
            Optional<Object> cterfc = Optional.ofNullable(dic.get("rfc"));
            Optional<Object> ctenom = Optional.ofNullable(dic.get("razon_social"));
            Optional<Object> proposito = Optional.ofNullable(dic.get("uso_cfdi"));

            CUsoCFDI uso = CUsoCFDI.fromValue((String) proposito.orElseThrow());

            rec.setRfc((String) cterfc.orElseThrow());
            rec.setNombre((String) ctenom.orElseThrow());
            rec.setUsoCFDI(uso);
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Receptor tag is missing");
            throw new FormatError("mandatory element in request is missing", ex);
        }
        return rec;
    }

    private Comprobante.Emisor shapeEmisorTag(ObjectFactory cfdiFactory) throws FormatError {

        Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();

        try {

            Map<String, Object> dic = LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "emisor");
            Optional<Object> emirfc = Optional.ofNullable(dic.get("rfc"));
            Optional<Object> eminom = Optional.ofNullable(dic.get("razon_social"));
            Optional<Object> regimen = Optional.ofNullable(dic.get("regimen_fiscal"));

            emisor.setRfc((String) emirfc.orElseThrow());
            emisor.setNombre((String) eminom.orElseThrow());
            emisor.setRegimenFiscal((String) regimen.orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Emisor tag is missing");
            throw new FormatError("mandatory element in request is missing", ex);
        }

        return emisor;
    }

    private Comprobante shapeComprobanteTag(ObjectFactory cfdiFactory) throws FormatError {

        Comprobante comprobante = cfdiFactory.createComprobante();

        try {

            Map<String, Object> controlDic = LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "control");
            Optional<Object> serie = Optional.ofNullable(controlDic.get("serie"));
            Optional<Object> folio = Optional.ofNullable(controlDic.get("folio"));

            Map<String, Object> monedaDic = LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "moneda");
            Optional<Object> moneda = Optional.ofNullable(monedaDic.get("iso_4217"));
            Optional<Object> tpocam = Optional.ofNullable(monedaDic.get("tipo_de_cambio"));

            Map<String, Object> totalesDic = LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "totales");
            Optional<Object> subtot = Optional.ofNullable(totalesDic.get("importe_sum"));
            Optional<Object> total = Optional.ofNullable(totalesDic.get("monto_total"));

            Map<String, Object> formaPagoDic = LegoTagAssembler.obtainMapFromKey(cfdiReq.getDs(), "forma_pago");
            Optional<Object> clave = Optional.ofNullable(formaPagoDic.get("clave"));

            Optional<Object> emizip = LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "lugar_expedicion");
            Optional<Object> nocert = LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "numero_certificado");
            Optional<Object> metpago = LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "metodo_pago");
            Optional<Object> timeStamp = LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "time_stamp");
            XMLGregorianCalendar timeStampGregorianCalendar = DatatypeFactory.
                    newInstance().newXMLGregorianCalendar((String) timeStamp.orElseThrow());

            comprobante.setVersion("4.0");
            comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
            comprobante.setLugarExpedicion((String) emizip.orElseThrow());
            CMetodoPago metpagVal = CMetodoPago.fromValue((String) metpago.orElseThrow());
            comprobante.setMetodoPago(metpagVal);
            comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
            comprobante.setTotal(new BigDecimal((String) total.orElseThrow()));
            comprobante.setMoneda(CMoneda.fromValue((String) moneda.orElseThrow()));

            if (tpocam.isPresent()
                    && !moneda.equals(FacturaXml.NATIONAL_CURRENCY)
                    && !moneda.equals(FacturaXml.NO_CURRENCY)) {

                comprobante.setTipoCambio(new BigDecimal((String) tpocam.get()));
            }

            comprobante.setCertificado("");
            comprobante.setSubTotal(new BigDecimal((String) subtot.orElseThrow()));
            comprobante.setCondicionesDePago("");
            comprobante.setNoCertificado((String) nocert.orElseThrow());
            comprobante.setFormaPago((String) clave.orElseThrow());
            comprobante.setFecha(timeStampGregorianCalendar);
            comprobante.setSerie((String) serie.orElseThrow());
            comprobante.setFolio((String) folio.orElseThrow());

        } catch (DatatypeConfigurationException ex) {
            log.error("Comprobante tag can not include a time stamp badly formated");
            throw new FormatError("time stamp incorrect format", ex);
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Comprobante tag is missing");
            throw new FormatError("mandatory element in request is missing", ex);
        }

        return comprobante;
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();
        ObjectFactory cfdiFactory = new ObjectFactory();

        Comprobante comprobante = this.shapeComprobanteTag(cfdiFactory);
        Comprobante.Emisor emisor = this.shapeEmisorTag(cfdiFactory);
        Comprobante.Receptor receptor = shapeReceptorTag( cfdiFactory);
        Comprobante.Conceptos conceptos = this.shapeConceptosTag(cfdiFactory, shapePcs());
        return sw;
    }

    public static String render(Request cfdiReq, IStorage st) throws FormatError, StorageError {

        FacturaXml ic = new FacturaXml(cfdiReq, st);

        StringWriter cfdi = ic.shape();

        return "It must be slightly implemented as it was in lola";
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

        // The optional one out of gob regulations
        private String sku;
  
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

        private static Optional<Object> obtainObjFromKey(Map<String, Object> m, final String k) {
            return Optional.ofNullable(m.get(k));
        }
    }
}
