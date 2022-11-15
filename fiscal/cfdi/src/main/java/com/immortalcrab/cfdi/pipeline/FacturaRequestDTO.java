package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.RequestError;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

@Log4j
class FacturaRequestDTO extends JsonRequest {

    public static final String CFDI_VER = "4.0";
    public static final String TIPO_COMPROBANTE = "I";
    private static final String CFDI_PREFIX = "cfdi";
    private static final String CFDI_URI = "http://www.sat.gob.mx/cfd/4";
    private static final String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

    private static final String NATIONAL_CURRENCY = "MXN";
    private static final String NO_CURRENCY = "XXX";

    ComprobanteAttributes _comprobante;
    EmisorAttributes _emisor;
    ReceptorAttributes _receptor;
    List<PseudoConcepto> _conceptos;
    ImpuestosAttributes _impuestos;
    List<ImpuestosTrasladoAttributes> _impuestosTraslados;
    List<ImpuestosRetencionAttributes> _impuestosRetenciones;

    public FacturaRequestDTO(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
        _comprobante = new ComprobanteAttributes();
        _emisor = new EmisorAttributes();
        _receptor = new ReceptorAttributes();
        _conceptos = new LinkedList<>();
        _impuestos = new ImpuestosAttributes();
        _impuestosTraslados = new LinkedList<>();
        _impuestosRetenciones = new LinkedList<>();
        shapeComprobante();
        shapeEmisor();
        shapeReceptor();
        shapePseudoConceptos();
        shapeImpuestos();
    }

    public ComprobanteAttributes getComprobanteAttributes() { return _comprobante; }

    public EmisorAttributes getEmisorAttributes() { return _emisor; }

    public ReceptorAttributes getReceptorAttributes() { return _receptor; }

    public List<PseudoConcepto> getPseudoConceptos() { return _conceptos; }

    public ImpuestosAttributes getImpuestosAttributes() { return _impuestos; }

    public List<ImpuestosTrasladoAttributes> getImpuestosTraslados() { return _impuestosTraslados; }

    public List<ImpuestosRetencionAttributes> getImpuestosRetenciones() { return _impuestosRetenciones; }

    private void shapeComprobante() throws RequestError {

        try {
            String serie = LegoAssembler.obtainObjFromKey(this.getDs(), "serie");
            String folio = LegoAssembler.obtainObjFromKey(this.getDs(), "folio");
            String fecha = LegoAssembler.obtainObjFromKey(this.getDs(), "fecha");
            String formaPago = LegoAssembler.obtainObjFromKey(this.getDs(), "forma_pago");
            String noCertificado = LegoAssembler.obtainObjFromKey(this.getDs(), "no_certificado");
            BigDecimal subTotal = LegoAssembler.obtainObjFromKey(this.getDs(), "subtotal");
            BigDecimal descuento = LegoAssembler.obtainObjFromKey(this.getDs(), "descuento");
            String moneda = LegoAssembler.obtainObjFromKey(this.getDs(), "moneda");
            BigDecimal tipoCambio = LegoAssembler.obtainObjFromKey(this.getDs(), "tipo_cambio");
            BigDecimal total = LegoAssembler.obtainObjFromKey(this.getDs(), "total");
            String exportacion = LegoAssembler.obtainObjFromKey(this.getDs(), "exportacion");
            String metodoPago = LegoAssembler.obtainObjFromKey(this.getDs(), "metodo_pago");
            String lugarExpedicion = LegoAssembler.obtainObjFromKey(this.getDs(), "lugar_expedicion");

            _comprobante.setSerie(serie);
            _comprobante.setFolio(folio);
            _comprobante.setFecha(fecha);
            _comprobante.setFormaPago(formaPago);
            _comprobante.setNoCertificado(noCertificado);
            _comprobante.setSubTotal(subTotal);
            _comprobante.setDescuento(descuento);
            _comprobante.setMoneda(moneda);
            _comprobante.setTipoCambio(tipoCambio);
            _comprobante.setTotal(total);
            _comprobante.setExportacion(exportacion);
            _comprobante.setMetodoPago(metodoPago);
            _comprobante.setLugarExpedicion(lugarExpedicion);

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Comprobante tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeEmisor() throws RequestError {

        try {
            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "emisor");
            _emisor.setRfc((String) dic.get("rfc"));
            _emisor.setNombre((String) dic.get("nombre"));
            _emisor.setRegimenFiscal((String) dic.get("regimen_fiscal"));

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Emisor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeReceptor() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "receptor");
            _receptor.setRfc((String) dic.get("rfc"));
            _receptor.setNombre((String) dic.get("nombre"));
            _receptor.setDomicilioFiscalReceptor((String) dic.get("domicilio_fiscal_receptor"));
            _receptor.setResidenciaFiscal((String) dic.get("residencia_fiscal"));
            _receptor.setRegimenFiscalReceptor((String) dic.get("regimen_fiscal_receptor"));
            _receptor.setUsoCfdi((String) dic.get("uso_cfdi"));

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Receptor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapePseudoConceptos() throws RequestError {

        try {
            List<Map<String, Object>> items = LegoAssembler.obtainObjFromKey(this.getDs(), "conceptos");

            items.stream().map(i -> {

                PseudoConcepto p = new PseudoConcepto();
                p.setClaveProdServ(LegoAssembler.obtainObjFromKey(i, "clave_prod_serv"));
                p.setNoIdentificacion(LegoAssembler.obtainObjFromKey(i, "no_identificacion"));
                p.setCantidad(LegoAssembler.obtainObjFromKey(i, "cantidad"));
                p.setClaveUnidad(LegoAssembler.obtainObjFromKey(i, "clave_unidad"));
                p.setUnidad(LegoAssembler.obtainObjFromKey(i, "unidad"));
                p.setDescripcion(LegoAssembler.obtainObjFromKey(i, "descripcion"));
                p.setValorUnitario(LegoAssembler.obtainObjFromKey(i, "valor_unitario"));
                p.setImporte(LegoAssembler.obtainObjFromKey(i, "importe"));
                p.setDescuento(LegoAssembler.obtainObjFromKey(i, "descuento"));
                p.setObjetoImp(LegoAssembler.obtainObjFromKey(i, "objeto_imp"));

                List<ConceptoTrasladoAttributes> psTraslados = new LinkedList<>();
                List<Map<String, Object>> traslados = LegoAssembler.obtainObjFromKey(i, "traslados");
                traslados.stream().map(j -> {
                    ConceptoTrasladoAttributes psTraslado = new ConceptoTrasladoAttributes();
                    psTraslado.setBase(LegoAssembler.obtainObjFromKey(j, "base"));
                    psTraslado.setImpuesto(LegoAssembler.obtainObjFromKey(j, "impuesto"));
                    psTraslado.setTipoFactor(LegoAssembler.obtainObjFromKey(j, "tipo_factor"));
                    psTraslado.setTasaOCuota(LegoAssembler.obtainObjFromKey(j, "tasa_o_cuota"));
                    psTraslado.setImporte(LegoAssembler.obtainObjFromKey(j, "importe"));
                    return psTraslado;
                }).forEachOrdered(t -> {
                    psTraslados.add(t);
                });
                p.setTraslados(psTraslados);

                List<ConceptoRetencionAttributes> psRetenciones = new LinkedList<>();
                List<Map<String, Object>> retenciones = LegoAssembler.obtainObjFromKey(i, "retenciones");
                retenciones.stream().map(k -> {
                    ConceptoRetencionAttributes psRetencion = new ConceptoRetencionAttributes();
                    psRetencion.setBase(LegoAssembler.obtainObjFromKey(k, "base"));
                    psRetencion.setImpuesto(LegoAssembler.obtainObjFromKey(k, "impuesto"));
                    psRetencion.setTipoFactor(LegoAssembler.obtainObjFromKey(k, "tipo_factor"));
                    psRetencion.setTasaOCuota(LegoAssembler.obtainObjFromKey(k, "tasa_o_cuota"));
                    psRetencion.setImporte(LegoAssembler.obtainObjFromKey(k, "importe"));
                    return psRetencion;
                }).forEachOrdered(r -> {
                    psRetenciones.add(r);
                });
                p.setRetenciones(psRetenciones);

                return p;

            }).forEachOrdered(p -> {
                _conceptos.add(p);
            });

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Conceptos tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeImpuestos() throws RequestError {

        try {
            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "impuestos");
            _impuestos.setTotalImpuestosRetenidos((BigDecimal) dic.get("total_impuestos_retenidos"));
            _impuestos.setTotalImpuestosTrasladados((BigDecimal) dic.get("total_impuestos_trasladados"));

            List<Map<String, Object>> retenciones = LegoAssembler.obtainObjFromKey(dic, "retenciones");
            for (Map<String, Object> r : retenciones) {
                var ret = new ImpuestosRetencionAttributes();
                ret.setImpuesto((String) r.get("impuesto"));
                ret.setImporte((BigDecimal) r.get("importe"));
                _impuestosRetenciones.add(ret);
            }
            List<Map<String, Object>> traslados = LegoAssembler.obtainObjFromKey(dic, "traslados");
            for (Map<String, Object> t : traslados) {
                var tras = new ImpuestosTrasladoAttributes();
                tras.setImpuesto((String) t.get("impuesto"));
                tras.setTipoFactor((String) t.get("tipo_factor"));
                tras.setTasaOCuota((BigDecimal) t.get("tasa_o_cuota"));
                tras.setImporte((BigDecimal) t.get("importe"));
                _impuestosTraslados.add(tras);
            }

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Impuestos tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ComprobanteAttributes {

        private String serie;
        private String folio;
        private String fecha;
        private String formaPago;
        private String noCertificado;
        private BigDecimal subTotal;
        private BigDecimal descuento;
        private String moneda;
        private BigDecimal tipoCambio;
        private BigDecimal total;
        private String exportacion;
        private String metodoPago;
        private String lugarExpedicion;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class EmisorAttributes {

        private String rfc;
        private String nombre;
        private String regimenFiscal;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ReceptorAttributes {

        private String rfc;
        private String nombre;
        private String domicilioFiscalReceptor;
        private String residenciaFiscal;
        private String regimenFiscalReceptor;
        private String usoCfdi;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoConcepto {

        private String claveProdServ;
        private String noIdentificacion;
        private BigDecimal cantidad;
        private String claveUnidad;
        private String unidad;
        private String descripcion;
        private BigDecimal valorUnitario;
        private BigDecimal importe;
        private BigDecimal descuento;
        private String objetoImp;
        private List<ConceptoTrasladoAttributes> traslados;
        private List<ConceptoRetencionAttributes> retenciones;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ConceptoTrasladoAttributes {

        private BigDecimal base;
        private String impuesto;
        private String tipoFactor;
        private BigDecimal tasaOCuota;
        private BigDecimal importe;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ConceptoRetencionAttributes {

        private BigDecimal base;
        private String impuesto;
        private String tipoFactor;
        private BigDecimal tasaOCuota;
        private BigDecimal importe;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ImpuestosAttributes {

        private BigDecimal totalImpuestosRetenidos;
        private BigDecimal totalImpuestosTrasladados;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ImpuestosRetencionAttributes {

        private String impuesto;
        private BigDecimal importe;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ImpuestosTrasladoAttributes {

        private BigDecimal base;
        private String impuesto;
        private String tipoFactor;
        private BigDecimal tasaOCuota;
        private BigDecimal importe;
    }

    private static class LegoAssembler {

        private static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {

            Optional<Object> dict = Optional.ofNullable(m.get(k));
            return (Map<String, Object>) dict.orElseThrow();
        }

        private static <T> T obtainObjFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
            return (T) Optional.ofNullable(m.get(k)).orElseThrow();
        }
    }
}
