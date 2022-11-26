package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.RequestError;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
class FacturaRequestDTO extends Request {

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
            Double subTotal = LegoAssembler.obtainObjFromKey(this.getDs(), "subtotal");
            Double descuento = LegoAssembler.obtainObjFromKey(this.getDs(), "descuento");
            String moneda = LegoAssembler.obtainObjFromKey(this.getDs(), "moneda");
            Double tipoCambio = LegoAssembler.obtainObjFromKey(this.getDs(), "tipo_cambio");
            Double total = LegoAssembler.obtainObjFromKey(this.getDs(), "total");
            String exportacion = LegoAssembler.obtainObjFromKey(this.getDs(), "exportacion");
            String metodoPago = LegoAssembler.obtainObjFromKey(this.getDs(), "metodo_pago");
            String lugarExpedicion = LegoAssembler.obtainObjFromKey(this.getDs(), "lugar_expedicion");

            _comprobante.setSerie(serie);
            _comprobante.setFolio(folio);
            _comprobante.setFecha(fecha);
            _comprobante.setFormaPago(formaPago);
            _comprobante.setNoCertificado(noCertificado);
            _comprobante.setSubTotal(new BigDecimal(subTotal.toString()));
            _comprobante.setDescuento(new BigDecimal(descuento.toString()));
            _comprobante.setMoneda(moneda);
            _comprobante.setTipoCambio(new BigDecimal(tipoCambio.toString()));
            _comprobante.setTotal(new BigDecimal(total.toString()));
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
            _emisor.setRfc(LegoAssembler.obtainObjFromKey(dic, "rfc"));
            _emisor.setNombre(LegoAssembler.obtainObjFromKey(dic, "nombre"));
            _emisor.setRegimenFiscal(LegoAssembler.obtainObjFromKey(dic, "regimen_fiscal"));

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Emisor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeReceptor() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "receptor");
            _receptor.setRfc(LegoAssembler.obtainObjFromKey(dic, "rfc"));
            _receptor.setNombre(LegoAssembler.obtainObjFromKey(dic, "nombre"));
            _receptor.setDomicilioFiscalReceptor(LegoAssembler.obtainObjFromKey(dic, "domicilio_fiscal_receptor"));
            _receptor.setResidenciaFiscal(LegoAssembler.obtainObjFromKey(dic, "residencia_fiscal"));
            _receptor.setRegimenFiscalReceptor(LegoAssembler.obtainObjFromKey(dic, "regimen_fiscal_receptor"));
            _receptor.setUsoCfdi(LegoAssembler.obtainObjFromKey(dic, "uso_cfdi"));

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
                Double cantidad = LegoAssembler.obtainObjFromKey(i, "cantidad");
                p.setCantidad(new BigDecimal(cantidad.toString()));
                p.setClaveUnidad(LegoAssembler.obtainObjFromKey(i, "clave_unidad"));
                p.setUnidad(LegoAssembler.obtainObjFromKey(i, "unidad"));
                p.setDescripcion(LegoAssembler.obtainObjFromKey(i, "descripcion"));
                Double valorUnitario = LegoAssembler.obtainObjFromKey(i, "valor_unitario");
                p.setValorUnitario(new BigDecimal(valorUnitario.toString()));
                Double importe = LegoAssembler.obtainObjFromKey(i, "importe");
                p.setImporte(new BigDecimal(importe.toString()));
                Double descuento = LegoAssembler.obtainObjFromKey(i, "descuento");
                p.setDescuento(new BigDecimal(descuento.toString()));
                p.setObjetoImp(LegoAssembler.obtainObjFromKey(i, "objeto_imp"));

                List<ConceptoTrasladoAttributes> psTraslados = new LinkedList<>();
                List<Map<String, Object>> traslados = LegoAssembler.obtainObjFromKey(i, "traslados");
                traslados.stream().map(j -> {
                    ConceptoTrasladoAttributes psTraslado = new ConceptoTrasladoAttributes();
                    Double base = LegoAssembler.obtainObjFromKey(j, "base");
                    psTraslado.setBase(new BigDecimal(base.toString()));
                    psTraslado.setImpuesto(LegoAssembler.obtainObjFromKey(j, "impuesto"));
                    psTraslado.setTipoFactor(LegoAssembler.obtainObjFromKey(j, "tipo_factor"));
                    Double tasaOCuota = LegoAssembler.obtainObjFromKey(j, "tasa_o_cuota");
                    psTraslado.setTasaOCuota(new BigDecimal(tasaOCuota.toString()));
                    Double importeTrasl = LegoAssembler.obtainObjFromKey(j, "importe");
                    psTraslado.setImporte(new BigDecimal(importeTrasl.toString()));
                    return psTraslado;
                }).forEachOrdered(t -> {
                    psTraslados.add(t);
                });
                p.setTraslados(psTraslados);

                List<ConceptoRetencionAttributes> psRetenciones = new LinkedList<>();
                List<Map<String, Object>> retenciones = LegoAssembler.obtainObjFromKey(i, "retenciones");
                retenciones.stream().map(k -> {
                    ConceptoRetencionAttributes psRetencion = new ConceptoRetencionAttributes();
                    Double base = LegoAssembler.obtainObjFromKey(k, "base");
                    psRetencion.setBase(new BigDecimal(base.toString()));
                    psRetencion.setImpuesto(LegoAssembler.obtainObjFromKey(k, "impuesto"));
                    psRetencion.setTipoFactor(LegoAssembler.obtainObjFromKey(k, "tipo_factor"));
                    Double tasaOCuota = LegoAssembler.obtainObjFromKey(k, "tasa_o_cuota");
                    psRetencion.setTasaOCuota(new BigDecimal(tasaOCuota.toString()));
                    Double importeReten = LegoAssembler.obtainObjFromKey(k, "importe");
                    psRetencion.setImporte(new BigDecimal(importeReten.toString()));
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
            {
                Double d = LegoAssembler.obtainObjFromKey(dic, "total_impuestos_retenidos");
                _impuestos.setTotalImpuestosRetenidos(new BigDecimal(d.toString()));
            }
            {
                Double d = LegoAssembler.obtainObjFromKey(dic, "total_impuestos_trasladados");
                _impuestos.setTotalImpuestosTrasladados(new BigDecimal(d.toString()));
            }

            List<Map<String, Object>> retenciones = LegoAssembler.obtainObjFromKey(dic, "retenciones");
            for (Map<String, Object> r : retenciones) {
                var ret = new ImpuestosRetencionAttributes();
                ret.setImpuesto((String) r.get("impuesto"));
                Double d = (Double) r.get("importe");
                ret.setImporte(new BigDecimal(d.toString()));
                _impuestosRetenciones.add(ret);
            }
            List<Map<String, Object>> traslados = LegoAssembler.obtainObjFromKey(dic, "traslados");
            for (Map<String, Object> t : traslados) {
                var tras = new ImpuestosTrasladoAttributes();
                {
                    Double d = (Double) t.get("base");
                    tras.setBase(new BigDecimal(d.toString()));
                }
                tras.setImpuesto((String) t.get("impuesto"));
                tras.setTipoFactor((String) t.get("tipo_factor"));
                {
                    Double d = (Double) t.get("tasa_o_cuota");
                    tras.setTasaOCuota(new BigDecimal(d.toString()));
                }
                {
                    Double d = (Double) t.get("importe");
                    tras.setImporte(new BigDecimal(d.toString()));
                }
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
