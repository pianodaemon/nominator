package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
class NominaRequestDTO extends JsonRequest {

    public static final String CFDI_VER = "4.0";
    public static final String NOMINA_VER = "1.2";
    public static final String TIPO_COMPROBANTE = "N";

    RegularRootAttributes _docAttribs;
    RegularReceptor _pr;
    RegularEmisor _pe;
    List<RegularConcepto> _pcs;
    NomPrincipalAttributes _nomAttribs;
    NomEmisorAttributes _nomEmisorAttribs;
    NomReceptorAttributes _nomReceptorAttribs;
    NomPercepcionesAttributes _nomPercepcionesAttribs;
    NomDeduccionesAttributes _nomDeduccionesAttribs;
    NomOtrosPagosAttributes _nomNomOtrosPagosAttribs;

    public NominaRequestDTO(InputStreamReader reader) throws RequestError, DecodeError {

        super(reader);
        _docAttribs = shapeDocAttribs();
        _pr = shapeRp();
        _pe = shapeEp();
        _pcs = shapePcs();
        _nomNomOtrosPagosAttribs = shapeNomOtrosPagosAttribs();
        _nomAttribs = shapeNomAttribs();
        _nomEmisorAttribs = shapeNomEmisorAttribs();
        _nomReceptorAttribs = shapeNomReceptorAttribs();
        _nomPercepcionesAttribs = shapeNomPercepcionesAttribs();
        _nomDeduccionesAttribs = shapeNomDeduccionesAttribs();
    }

    public NomPrincipalAttributes getNomAttributes() {
        return _nomAttribs;
    }

    public NomEmisorAttributes getNomEmisorAttribs() {
        return _nomEmisorAttribs;
    }

    public NomReceptorAttributes getNomReceptorAttribs() {
        return _nomReceptorAttribs;
    }

    public NomPercepcionesAttributes getNomPercepcionesAttribs() {
        return this._nomPercepcionesAttribs;
    }

    public NomDeduccionesAttributes getNomDeduccionesAttribs() {
        return _nomDeduccionesAttribs;
    }

    public NomOtrosPagosAttributes getNomNomOtrosPagosAttribs() {
        return _nomNomOtrosPagosAttribs;
    }

    public RegularRootAttributes getDocAttributes() {
        return _docAttribs;
    }

    public RegularReceptor getPseudoReceptor() {
        return _pr;
    }

    public RegularEmisor getPseudoEmisor() {
        return _pe;
    }

    public List<RegularConcepto> getPseudoConceptos() {
        return _pcs;
    }

    private RegularRootAttributes shapeDocAttribs() throws RequestError {

        RegularRootAttributes rattrs = new RegularRootAttributes();

        try {

            String serie = LegoAssembler.obtainObjFromKey(this.getDs(), "serie");
            String folio = LegoAssembler.obtainObjFromKey(this.getDs(), "folio");
            String lugar = LegoAssembler.obtainObjFromKey(this.getDs(), "lugar_expedicion");
            String fecha = LegoAssembler.obtainObjFromKey(this.getDs(), "fecha");
            String moneda = LegoAssembler.obtainObjFromKey(this.getDs(), "moneda");
            String exportacion = LegoAssembler.obtainObjFromKey(this.getDs(), "exportacion");
            String metodoPago = LegoAssembler.obtainObjFromKey(this.getDs(), "metodo_pago");

            {
                Double total = LegoAssembler.obtainObjFromKey(this.getDs(), "total");
                rattrs.setTotal(new BigDecimal(total.toString()));
            }

            {
                Double subtotal = LegoAssembler.obtainObjFromKey(this.getDs(), "subtotal");
                rattrs.setSubtotal(new BigDecimal(subtotal.toString()));
            }

            {
                Double descuento = LegoAssembler.obtainObjFromKey(this.getDs(), "descuento");
                rattrs.setDescuento(new BigDecimal(descuento.toString()));
            }

            rattrs.setSerie(serie);
            rattrs.setFolio(folio);
            rattrs.setLugarExpedicion(lugar);
            rattrs.setFecha(fecha);
            rattrs.setMoneda(moneda);
            rattrs.setExportacion(exportacion);
            rattrs.setMetodoPago(metodoPago);

            return rattrs;
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Comprobante tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private List<RegularConcepto> shapePcs() throws RequestError {

        Object cs = NominaRequestDTO.LegoAssembler.obtainObjFromKey(this.getDs(), "conceptos");

        try {

            List<Map<String, Object>> items = (List<Map<String, Object>>) cs;

            return items.stream().map(i -> {

                RegularConcepto p = new RegularConcepto();
                p.setClaveProdServ(LegoAssembler.obtainObjFromKey(i, "clave_prod_serv"));
                p.setDescripcion(LegoAssembler.obtainObjFromKey(i, "descripcion"));
                p.setClaveUnidad(LegoAssembler.obtainObjFromKey(i, "clave_unidad"));
                p.setObjImp(LegoAssembler.obtainObjFromKey(i, "objeto_imp"));

                {
                    Double cantidad = LegoAssembler.obtainObjFromKey(i, "cantidad");
                    p.setCantidad(new BigDecimal(cantidad.intValue()));
                }

                {
                    Double valorUnitario = LegoAssembler.obtainObjFromKey(i, "valor_unitario");
                    p.setValorUnitario(new BigDecimal(valorUnitario.toString()));
                }

                {
                    Double importe = LegoAssembler.obtainObjFromKey(i, "importe");
                    p.setImporte(new BigDecimal(importe.toString()));
                }

                {
                    Double descuento = LegoAssembler.obtainObjFromKey(i, "descuento");
                    p.setDescuento(new BigDecimal(descuento.toString()));
                }

                return p;

            }).collect(Collectors.toList());

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Concepto tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private RegularEmisor shapeEp() throws RequestError {

        try {

            RegularEmisor emisor = new RegularEmisor();

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "emisor");
            String emirfc = LegoAssembler.obtainObjFromKey(dic, "rfc");
            String eminom = LegoAssembler.obtainObjFromKey(dic, "nombre");
            String regimen = LegoAssembler.obtainObjFromKey(dic, "regimen_fiscal");

            emisor.setRfc(emirfc);
            emisor.setNombre(eminom);
            emisor.setRegimenFiscal(regimen);

            return emisor;
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Emisor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private RegularReceptor shapeRp() throws RequestError {

        try {

            RegularReceptor receptor = new RegularReceptor();

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "receptor");
            String cterfc = LegoAssembler.obtainObjFromKey(dic, "rfc");
            String ctenom = LegoAssembler.obtainObjFromKey(dic, "nombre");
            String proposito = LegoAssembler.obtainObjFromKey(dic, "uso_cfdi");
            String df = LegoAssembler.obtainObjFromKey(dic, "domicilio_fiscal_receptor");
            String rf = LegoAssembler.obtainObjFromKey(dic, "regimen_fiscal_receptor");

            receptor.setRfc(cterfc);
            receptor.setNombre(ctenom);
            receptor.setProposito(proposito);
            receptor.setDomicilioFiscal(df);
            receptor.setRegimenFiscal(rf);

            return receptor;
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Receptor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomPrincipalAttributes shapeNomAttribs() throws RequestError {

        try {

            NomPrincipalAttributes nomAttrs = new NomPrincipalAttributes();

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "nomina");
            String tipoNomina = LegoAssembler.obtainObjFromKey(dic, "tipo_nomina");
            String fechaPago = LegoAssembler.obtainObjFromKey(dic, "fecha_pago");
            String fechaInicialPago = LegoAssembler.obtainObjFromKey(dic, "fecha_inicial_pago");
            String fechaFinalPago = LegoAssembler.obtainObjFromKey(dic, "fecha_final_pago");

            {
                Integer numDiasPagados = LegoAssembler.obtainObjFromKey(dic, "num_dias_pagados");
                nomAttrs.setDiasPagados(new BigDecimal(numDiasPagados));
            }

            {
                Double totalPercepciones = LegoAssembler.obtainObjFromKey(dic, "total_percepciones");
                nomAttrs.setTotalPercepciones(new BigDecimal(totalPercepciones.toString()));
            }

            {
                Double totalDeducciones = LegoAssembler.obtainObjFromKey(dic, "total_deducciones");
                nomAttrs.setTotalDeducciones(new BigDecimal(totalDeducciones.toString()));
            }

            nomAttrs.setTipoNomina(tipoNomina);
            nomAttrs.setFechaPago(fechaPago);
            nomAttrs.setFechaInicialPago(fechaInicialPago);
            nomAttrs.setFechaFinalPago(fechaFinalPago);

            return nomAttrs;
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomEmisorAttributes shapeNomEmisorAttribs() throws RequestError {
        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "emisor");
            return new NomEmisorAttributes((String) LegoAssembler.obtainObjFromKey(dic, "registro_patronal"));
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:Emisor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomReceptorAttributes shapeNomReceptorAttribs() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "receptor");

            Double sdi = LegoAssembler.obtainObjFromKey(dic, "salario_diario_integrado");

            return new NomReceptorAttributes(
                    LegoAssembler.obtainObjFromKey(dic, "fecha_inicio_rel_laboral"),
                    LegoAssembler.obtainObjFromKey(dic, "clave_ent_fed"),
                    LegoAssembler.obtainObjFromKey(dic, "num_seguridad_social"),
                    LegoAssembler.obtainObjFromKey(dic, "curp"),
                    LegoAssembler.obtainObjFromKey(dic, "tipo_contrato"),
                    LegoAssembler.obtainObjFromKey(dic, "tipo_regimen"),
                    LegoAssembler.obtainObjFromKey(dic, "num_empleado"),
                    LegoAssembler.obtainObjFromKey(dic, "riesgo_puesto"),
                    LegoAssembler.obtainObjFromKey(dic, "periodicidad_pago"),
                    new BigDecimal(sdi.toString()),
                    LegoAssembler.obtainObjFromKey(dic, "antiguedad"));
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:Receptor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomPercepcionesAttributes shapeNomPercepcionesAttribs() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "percepciones");

            List<Map<String, Object>> lms = NominaRequestDTO.LegoAssembler.obtainObjFromKey(dic, "lista");

            Double ts = LegoAssembler.obtainObjFromKey(dic, "total_sueldos");
            Double tg = LegoAssembler.obtainObjFromKey(dic, "total_gravado");
            Double te = LegoAssembler.obtainObjFromKey(dic, "total_exento");

            List<PercepcionItem> items = lms.stream().map(m -> {

                Double importeGravado = LegoAssembler.obtainObjFromKey(m, "importe_gravado");
                Double importeExento = LegoAssembler.obtainObjFromKey(m, "importe_exento");

                PercepcionItem p = new PercepcionItem(
                        LegoAssembler.obtainObjFromKey(m, "clave"),
                        LegoAssembler.obtainObjFromKey(m, "concepto"),
                        LegoAssembler.obtainObjFromKey(m, "tipo_percepcion"),
                        new BigDecimal(importeGravado.toString()),
                        new BigDecimal(importeExento.toString()));

                return p;
            }).collect(Collectors.toList());

            return new NomPercepcionesAttributes(
                    new BigDecimal(ts.toString()),
                    new BigDecimal(tg.toString()),
                    new BigDecimal(te.toString()),
                    items);
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:Percepciones tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomDeduccionesAttributes shapeNomDeduccionesAttribs() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "deducciones");

            List<Map<String, Object>> lms = NominaRequestDTO.LegoAssembler.obtainObjFromKey(dic, "lista");

            Double tod = LegoAssembler.obtainObjFromKey(dic, "total_otras_deducciones");
            Double tir = LegoAssembler.obtainObjFromKey(dic, "total_impuestos_retenidos");

            List<DeduccionItem> items = lms.stream().map(m -> {

                Double importe = LegoAssembler.obtainObjFromKey(m, "importe");

                DeduccionItem p = new DeduccionItem(
                        LegoAssembler.obtainObjFromKey(m, "clave"),
                        LegoAssembler.obtainObjFromKey(m, "concepto"),
                        LegoAssembler.obtainObjFromKey(m, "tipo_deduccion"),
                        new BigDecimal(importe.toString()));

                return p;
            }).collect(Collectors.toList());

            return new NomDeduccionesAttributes(
                    new BigDecimal(tod.toString()),
                    new BigDecimal(tir.toString()),
                    items);
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:Deducciones tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomOtrosPagosAttributes shapeNomOtrosPagosAttribs() throws RequestError {

        try {

            List<Map<String, Object>> lms = NominaRequestDTO.LegoAssembler.obtainObjFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "otros_pagos");

            return new NomOtrosPagosAttributes(lms.stream().map(m -> {

                Double sub = LegoAssembler.obtainObjFromKey(m, "subsidio_causado");
                Double importe = LegoAssembler.obtainObjFromKey(m, "importe");

                OtrosPagosItem p = new OtrosPagosItem(
                        LegoAssembler.obtainObjFromKey(m, "clave"),
                        LegoAssembler.obtainObjFromKey(m, "concepto"),
                        LegoAssembler.obtainObjFromKey(m, "tipo_otro_pago"),
                        new BigDecimal(sub.toString()),
                        new BigDecimal(importe.toString()));

                return p;
            }).collect(Collectors.toList()));

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:OtrosPagos tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class RegularRootAttributes {

        private String fecha;
        private String lugarExpedicion;
        private String serie;
        private String folio;
        private String moneda;
        private String exportacion;
        private String metodoPago;
        private BigDecimal descuento;
        private BigDecimal subtotal;
        private BigDecimal total;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class RegularReceptor {

        private String rfc;
        private String nombre;
        private String proposito;
        private String domicilioFiscal;
        private String regimenFiscal;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class RegularEmisor {

        private String rfc;
        private String nombre;
        private String regimenFiscal;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class RegularConcepto {

        private String claveProdServ;
        private String claveUnidad;
        private String descripcion;
        private String objImp;
        private BigDecimal cantidad;
        private BigDecimal valorUnitario;
        private BigDecimal importe;
        private BigDecimal descuento;

    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class NomPrincipalAttributes {

        private String tipoNomina;
        private String fechaPago;
        private String fechaInicialPago;
        private String fechaFinalPago;
        private BigDecimal diasPagados;
        private BigDecimal totalPercepciones;
        private BigDecimal totalDeducciones;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class NomEmisorAttributes {

        private String registroPatronal;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class NomReceptorAttributes {

        private String fechaInicioRelLaboral;
        private String claveEntFed;
        private String numSeguridadSocial;
        private String curp;
        private String tipoContrato;
        private String tipoRegimen;
        private String numEmpleado;
        private String riesgoPuesto;
        private String periodicidadPago;
        private BigDecimal salarioDiarioIntegrado;
        private String antiguedad;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class NomPercepcionesAttributes {

        BigDecimal totalSueldos;
        BigDecimal totalGravado;
        BigDecimal totalExento;
        List<PercepcionItem> items;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class PercepcionItem {

        private String clave;
        private String concepto;
        private String tipoPercepcion;
        private BigDecimal importeGravado;
        private BigDecimal importeExento;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class NomDeduccionesAttributes {

        private BigDecimal totalOtrasDeducciones;
        private BigDecimal totalImpuestosRetenidos;
        List<DeduccionItem> items;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class DeduccionItem {

        private String clave;
        private String concepto;
        private String tipoDeduccion;
        private BigDecimal importe;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class NomOtrosPagosAttributes {

        List<OtrosPagosItem> items;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class OtrosPagosItem {

        private String clave;
        private String concepto;
        private String tipoOtroPago;
        private BigDecimal subsidioCausado;
        private BigDecimal importe;
    }

    private static class LegoAssembler {

        private static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
            return LegoAssembler.obtainObjFromKey(m, k);
        }

        private static <T> T obtainObjFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
            return (T) Optional.ofNullable(m.get(k)).orElseThrow();
        }
    }
}
