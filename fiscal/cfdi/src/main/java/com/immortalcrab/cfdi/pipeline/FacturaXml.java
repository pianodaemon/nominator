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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.*;

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

        List<PseudoConcepto> pcs = new LinkedList<>();

        try {

            List<Map<String, Object>> items = (List<Map<String, Object>>) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "conceptos");

            items.stream().map(i -> {

                PseudoConcepto p = new PseudoConcepto();
                p.setClaveProdServ((String) LegoTagAssembler.obtainObjFromKey(i, "clave_prod_serv"));
                p.setNoIdentificacion((String) LegoTagAssembler.obtainObjFromKey(i, "no_identificacion"));
                p.setCantidad((BigDecimal) LegoTagAssembler.obtainObjFromKey(i, "cantidad"));
                p.setClaveUnidad((String) LegoTagAssembler.obtainObjFromKey(i, "clave_unidad"));
                p.setUnidad((String) LegoTagAssembler.obtainObjFromKey(i, "unidad"));
                p.setDescripcion((String) LegoTagAssembler.obtainObjFromKey(i, "descripcion"));
                p.setValorUnitario((BigDecimal) LegoTagAssembler.obtainObjFromKey(i, "valor_unitario"));
                p.setImporte((BigDecimal) LegoTagAssembler.obtainObjFromKey(i, "importe"));
                p.setDescuento((BigDecimal) LegoTagAssembler.obtainObjFromKey(i, "descuento"));
                p.setObjetoImp((String) LegoTagAssembler.obtainObjFromKey(i, "objeto_imp"));

                List<PseudoConceptoTraslado> psTraslados = new LinkedList<>();
                var traslados = (List<Map<String, Object>>) LegoTagAssembler.obtainObjFromKey(i, "traslados");
                traslados.stream().map(j -> {
                    PseudoConceptoTraslado psTraslado = new PseudoConceptoTraslado();
                    psTraslado.setBase((BigDecimal) LegoTagAssembler.obtainObjFromKey(j, "base"));
                    psTraslado.setImpuesto((String) LegoTagAssembler.obtainObjFromKey(j, "impuesto"));
                    psTraslado.setTipoFactor((String) LegoTagAssembler.obtainObjFromKey(j, "tipo_factor"));
                    psTraslado.setTasaOCuota((BigDecimal) LegoTagAssembler.obtainObjFromKey(j, "tasa_o_cuota"));
                    psTraslado.setImporte((BigDecimal) LegoTagAssembler.obtainObjFromKey(j, "importe"));
                    return psTraslado;
                }).forEachOrdered(t -> {
                    psTraslados.add(t);
                });
                p.setTraslados(psTraslados);

                List<PseudoConceptoRetencion> psRetenciones = new LinkedList<>();
                var retenciones = (List<Map<String, Object>>) LegoTagAssembler.obtainObjFromKey(i, "retenciones");
                retenciones.stream().map(k -> {
                    PseudoConceptoRetencion psRetencion = new PseudoConceptoRetencion();
                    psRetencion.setBase((BigDecimal) LegoTagAssembler.obtainObjFromKey(k, "base"));
                    psRetencion.setImpuesto((String) LegoTagAssembler.obtainObjFromKey(k, "impuesto"));
                    psRetencion.setTipoFactor((String) LegoTagAssembler.obtainObjFromKey(k, "tipo_factor"));
                    psRetencion.setTasaOCuota((BigDecimal) LegoTagAssembler.obtainObjFromKey(k, "tasa_o_cuota"));
                    psRetencion.setImporte((BigDecimal) LegoTagAssembler.obtainObjFromKey(k, "importe"));
                    return psRetencion;
                }).forEachOrdered(r -> {
                    psRetenciones.add(r);
                });
                p.setRetenciones(psRetenciones);

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
            Optional<Object> ctenom = Optional.ofNullable(dic.get("nombre"));
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
            Optional<Object> eminom = Optional.ofNullable(dic.get("nombre"));
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

            var serie = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "serie");
            var folio = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "folio");
            var fecha = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "fecha");
            var formaPago = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "forma_pago");
            var noCertificado = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "no_certificado");
            var subtotal = (BigDecimal) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "subtotal");
            var moneda = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "moneda");
            var tipoCambio = (BigDecimal) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "tipo_cambio");
            var total = (BigDecimal) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "total");
            var metodoPago = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "metodo_pago");
            var lugarExpedicion = (String) LegoTagAssembler.obtainObjFromKey(cfdiReq.getDs(), "lugar_expedicion");
            XMLGregorianCalendar fechaGregorianCalendar = DatatypeFactory.
                    newInstance().newXMLGregorianCalendar(fecha);

            comprobante.setVersion("4.0");
            comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
            comprobante.setLugarExpedicion(lugarExpedicion);
            CMetodoPago metpagVal = CMetodoPago.fromValue(metodoPago);
            comprobante.setMetodoPago(metpagVal);
            comprobante.setTipoDeComprobante(CTipoDeComprobante.I);
            comprobante.setTotal(total);
            comprobante.setMoneda(CMoneda.fromValue(moneda));

            if (!moneda.equals(FacturaXml.NATIONAL_CURRENCY)
                && !moneda.equals(FacturaXml.NO_CURRENCY)) {

                comprobante.setTipoCambio(tipoCambio);
            }

            comprobante.setCertificado("");
            comprobante.setSubTotal(subtotal);
            comprobante.setCondicionesDePago("");
            comprobante.setNoCertificado(noCertificado);
            comprobante.setFormaPago(formaPago);
            comprobante.setFecha(fechaGregorianCalendar);
            comprobante.setSerie(serie);
            comprobante.setFolio(folio);

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

   public static String render(Request cfdiReq, IStamp<PacRegularRequest, PacRegularResponse> stamper, IStorage st) throws FormatError, StorageError {

        FacturaXml ic = new FacturaXml(cfdiReq, st);

        StringWriter cfdi = ic.shape();
        PacRegularRequest pacReq = new PacRegularRequest(cfdi.toString());
        PacRegularResponse pacRes = stamper.impress(pacReq);
 
        return "It must be slightly implemented as it was in lola";
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoConcepto {

        private String claveProdServ;
        private BigDecimal cantidad;
        private String claveUnidad;
        private String descripcion;
        private BigDecimal valorUnitario;
        private BigDecimal importe;
        private BigDecimal descuento;
        private String objetoImp;
        private List<PseudoConceptoTraslado> traslados;
        private List<PseudoConceptoRetencion> retenciones;

        // The optional ones, proper of issuer's operations
        private String noIdentificacion;
        private String unidad;
  
        public Comprobante.Conceptos.Concepto shapeConceptoTag(
                ObjectFactory cfdiFactory) {

            Comprobante.Conceptos.Concepto c = cfdiFactory.createComprobanteConceptosConcepto();

            c.setClaveProdServ(this.getClaveProdServ());
            c.setCantidad(this.getCantidad());
            c.setClaveUnidad(this.getClaveUnidad());
            c.setDescripcion(this.getDescripcion());
            c.setValorUnitario(this.getValorUnitario());
            c.setImporte(this.getImporte());
            c.setDescuento(this.getDescuento());
            c.setObjetoImp(this.getObjetoImp());
            c.setNoIdentificacion(this.getNoIdentificacion());
            c.setUnidad(this.getUnidad());

            var traslados = cfdiFactory.createComprobanteConceptosConceptoImpuestosTraslados();
            List<PseudoConceptoTraslado> psTraslados = this.getTraslados();
            for (PseudoConceptoTraslado t: psTraslados) {
                var cTraslado = t.shapeConceptoTrasladoTag(cfdiFactory);
                traslados.getTraslado().add(cTraslado);
            }

            var retenciones = cfdiFactory.createComprobanteConceptosConceptoImpuestosRetenciones();
            List<PseudoConceptoRetencion> psRetenciones = this.getRetenciones();
            for (PseudoConceptoRetencion r: psRetenciones) {
                var cRetencion = r.shapeConceptoRetencionTag(cfdiFactory);
                retenciones.getRetencion().add(cRetencion);
            }

            var impuestos = cfdiFactory.createComprobanteConceptosConceptoImpuestos();
            impuestos.setTraslados(traslados);
            impuestos.setRetenciones(retenciones);
            c.setImpuestos(impuestos);

            return c;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoConceptoTraslado {

        private BigDecimal base;
        private String impuesto;
        private String tipoFactor;
        private BigDecimal tasaOCuota;
        private BigDecimal importe;

        public Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado shapeConceptoTrasladoTag(
                ObjectFactory cfdiFactory) {

            Comprobante.Conceptos.Concepto.Impuestos.Traslados.Traslado t = cfdiFactory.createComprobanteConceptosConceptoImpuestosTrasladosTraslado();

            t.setBase(base);
            t.setImpuesto(impuesto);
            t.setTipoFactor(CTipoFactor.fromValue(tipoFactor));
            t.setTasaOCuota(tasaOCuota);
            t.setImporte(importe);

            return t;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoConceptoRetencion {

        private BigDecimal base;
        private String impuesto;
        private String tipoFactor;
        private BigDecimal tasaOCuota;
        private BigDecimal importe;

        public Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion shapeConceptoRetencionTag(
                ObjectFactory cfdiFactory) {

            Comprobante.Conceptos.Concepto.Impuestos.Retenciones.Retencion r = cfdiFactory.createComprobanteConceptosConceptoImpuestosRetencionesRetencion();

            r.setBase(base);
            r.setImpuesto(impuesto);
            r.setTipoFactor(CTipoFactor.fromValue(tipoFactor));
            r.setTasaOCuota(tasaOCuota);
            r.setImporte(importe);

            return r;
        }
    }

    private static class LegoTagAssembler {

        private static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {

            Optional<Object> dict = Optional.ofNullable(m.get(k));
            return (Map<String, Object>) dict.orElseThrow();
        }

        private static Object obtainObjFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {

            Optional<Object> optObj = Optional.ofNullable(m.get(k));
            return optObj.orElseThrow();
        }

        private static Optional<Object> obtainOptionalFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {

            return Optional.ofNullable(m.get(k));
        }
    }
}
