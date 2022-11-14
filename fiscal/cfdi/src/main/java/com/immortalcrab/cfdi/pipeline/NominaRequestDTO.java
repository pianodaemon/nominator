package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    DocPrincipalAttributes _docAttribs;
    PseudoReceptor _pr;
    PseudoEmisor _pe;
    List<PseudoConcepto> _pcs;
    NomPrincipalAttributes _nomAttribs;
    NomEmisorAttributes _nomEmisorAttribs;
    NomReceptorAttributes _nomReceptorAttribs;

    public NominaRequestDTO(InputStreamReader reader) throws RequestError, DecodeError {

        super(reader);
        _docAttribs = new DocPrincipalAttributes();
        _pr = new PseudoReceptor();
        _pe = new PseudoEmisor();
        _pcs = new LinkedList<>();
        shapeDocAttribs();
        shapeRp();
        shapeEp();
        shapePcs();
        _nomAttribs = new NomPrincipalAttributes();
        shapeNomAttribs();
        _nomEmisorAttribs = _shapeNomEmisorAttribs();
        _nomReceptorAttribs = _shapeNomReceptorAttribs();
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

    public DocPrincipalAttributes getDocAttributes() {
        return _docAttribs;
    }

    public PseudoReceptor getPseudoReceptor() {
        return _pr;
    }

    public PseudoEmisor getPseudoEmisor() {
        return _pe;
    }

    public List<PseudoConcepto> getPseudoConceptos() {
        return _pcs;
    }

    private void shapeDocAttribs() throws RequestError {

        try {

            Optional<Object> serie = LegoAssembler.obtainObjFromKey(this.getDs(), "serie");
            Optional<Object> folio = LegoAssembler.obtainObjFromKey(this.getDs(), "folio");
            Optional<Object> lugar = LegoAssembler.obtainObjFromKey(this.getDs(), "lugar_expedicion");
            Optional<Object> fecha = LegoAssembler.obtainObjFromKey(this.getDs(), "fecha");
            Optional<Object> moneda = LegoAssembler.obtainObjFromKey(this.getDs(), "moneda");
            Optional<Object> exportacion = LegoAssembler.obtainObjFromKey(this.getDs(), "exportacion");
            Optional<Object> metodoPago = LegoAssembler.obtainObjFromKey(this.getDs(), "metodo_pago");

            {
                Double total = (Double) LegoAssembler.obtainObjFromKey(this.getDs(), "total").orElseThrow();
                _docAttribs.setTotal(new BigDecimal(total.toString()));
            }

            {
                Double subtotal = (Double) LegoAssembler.obtainObjFromKey(this.getDs(), "subtotal").orElseThrow();
                _docAttribs.setSubtotal(new BigDecimal(subtotal.toString()));
            }

            {
                Double descuento = (Double) LegoAssembler.obtainObjFromKey(this.getDs(), "descuento").orElseThrow();
                _docAttribs.setDescuento(new BigDecimal(descuento.toString()));
            }

            _docAttribs.setSerie((String) serie.orElseThrow());
            _docAttribs.setFolio((String) folio.orElseThrow());
            _docAttribs.setLugarExpedicion((String) lugar.orElseThrow());
            _docAttribs.setFecha((String) fecha.orElseThrow());
            _docAttribs.setMoneda((String) moneda.orElseThrow());
            _docAttribs.setExportacion((String) exportacion.orElseThrow());
            _docAttribs.setMetodoPago((String) metodoPago.orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Comprobante tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapePcs() throws RequestError {

        Optional<Object> cs = NominaRequestDTO.LegoAssembler.obtainObjFromKey(this.getDs(), "conceptos");

        try {

            List<Map<String, Object>> items = (List<Map<String, Object>>) cs.orElseThrow();

            items.stream().map(i -> {

                PseudoConcepto p = new PseudoConcepto();
                p.setClaveProdServ((String) LegoAssembler.obtainObjFromKey(i, "clave_prod_serv").orElseThrow());
                p.setDescripcion((String) LegoAssembler.obtainObjFromKey(i, "descripcion").orElseThrow());
                p.setClaveUnidad((String) LegoAssembler.obtainObjFromKey(i, "clave_unidad").orElseThrow());
                p.setObjImp((String) LegoAssembler.obtainObjFromKey(i, "objeto_imp").orElseThrow());

                {
                    Double cantidad = (Double) LegoAssembler.obtainObjFromKey(i, "cantidad").orElseThrow();
                    p.setCantidad(new BigDecimal(cantidad.intValue()));
                }

                {
                    Double valorUnitario = (Double) LegoAssembler.obtainObjFromKey(i, "valor_unitario").orElseThrow();
                    p.setValorUnitario(new BigDecimal(valorUnitario.toString()));
                }

                {
                    Double importe = (Double) LegoAssembler.obtainObjFromKey(i, "importe").orElseThrow();
                    p.setImporte(new BigDecimal(importe.toString()));
                }

                {
                    Double descuento = (Double) LegoAssembler.obtainObjFromKey(i, "descuento").orElseThrow();
                    p.setDescuento(new BigDecimal(descuento.toString()));
                }

                return p;

            }).forEachOrdered(p -> {
                _pcs.add(p);
            });

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Concepto tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeEp() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "emisor");
            Optional<Object> emirfc = LegoAssembler.obtainObjFromKey(dic, "rfc");
            Optional<Object> eminom = LegoAssembler.obtainObjFromKey(dic, "nombre");
            Optional<Object> regimen = LegoAssembler.obtainObjFromKey(dic, "regimen_fiscal");

            _pe.setRfc((String) emirfc.orElseThrow());
            _pe.setNombre((String) eminom.orElseThrow());
            _pe.setRegimenFiscal((String) regimen.orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Emisor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeRp() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "receptor");
            Optional<Object> cterfc = LegoAssembler.obtainObjFromKey(dic, "rfc");
            Optional<Object> ctenom = LegoAssembler.obtainObjFromKey(dic, "nombre");
            Optional<Object> proposito = LegoAssembler.obtainObjFromKey(dic, "uso_cfdi");
            Optional<Object> df = LegoAssembler.obtainObjFromKey(dic, "domicilio_fiscal_receptor");
            Optional<Object> rf = LegoAssembler.obtainObjFromKey(dic, "regimen_fiscal_receptor");

            _pr.setRfc((String) cterfc.orElseThrow());
            _pr.setNombre((String) ctenom.orElseThrow());
            _pr.setProposito((String) proposito.orElseThrow());
            _pr.setDomicilioFiscal((String) df.orElseThrow());
            _pr.setRegimenFiscal((String) rf.orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Receptor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private void shapeNomAttribs() throws RequestError {

        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(this.getDs(), "nomina");
            Optional<Object> tipoNomina = LegoAssembler.obtainObjFromKey(dic, "tipo_nomina");
            Optional<Object> fechaPago = LegoAssembler.obtainObjFromKey(dic, "fecha_pago");
            Optional<Object> fechaInicialPago = LegoAssembler.obtainObjFromKey(dic, "fecha_inicial_pago");
            Optional<Object> fechaFinalPago = LegoAssembler.obtainObjFromKey(dic, "fecha_final_pago");

            {
                Integer numDiasPagados = (Integer) LegoAssembler.obtainObjFromKey(dic, "num_dias_pagados").orElseThrow();
                _nomAttribs.setDiasPagados(new BigDecimal(numDiasPagados));
            }

            {
                Double totalPercepciones = (Double) LegoAssembler.obtainObjFromKey(dic, "total_percepciones").orElseThrow();
                _nomAttribs.setTotalPercepciones(new BigDecimal(totalPercepciones.toString()));
            }

            {
                Double totalDeducciones = (Double) LegoAssembler.obtainObjFromKey(dic, "total_deducciones").orElseThrow();
                _nomAttribs.setTotalDeducciones(new BigDecimal(totalDeducciones.toString()));
            }

            _nomAttribs.setTipoNomina((String) tipoNomina.orElseThrow());
            _nomAttribs.setFechaPago((String) fechaPago.orElseThrow());
            _nomAttribs.setFechaInicialPago((String) fechaInicialPago.orElseThrow());
            _nomAttribs.setFechaFinalPago((String) fechaFinalPago.orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomEmisorAttributes _shapeNomEmisorAttribs() throws RequestError {
        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "emisor");
            return new NomEmisorAttributes((String) LegoAssembler.obtainObjFromKey(dic, "registro_patronal").orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:Emisor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    private NomReceptorAttributes _shapeNomReceptorAttribs() throws RequestError {
        try {

            Map<String, Object> dic = LegoAssembler.obtainMapFromKey(
                    LegoAssembler.obtainMapFromKey(this.getDs(), "nomina"),
                    "receptor");

            Double sdi = (Double) LegoAssembler.obtainObjFromKey(dic, "salario_diario_integrado").orElseThrow();

            return new NomReceptorAttributes(
                    (String) LegoAssembler.obtainObjFromKey(dic, "fecha_inicio_rel_laboral").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "clave_ent_fed").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "num_seguridad_social").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "curp").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "tipo_contrato").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "tipo_regimen").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "num_empleado").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "riesgo_puesto").orElseThrow(),
                    (String) LegoAssembler.obtainObjFromKey(dic, "periodicidad_pago").orElseThrow(),
                    new BigDecimal(sdi.toString()),
                    (String) LegoAssembler.obtainObjFromKey(dic, "antiguedad").orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Complemento:Nomina:Receptor tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class DocPrincipalAttributes {

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
    public static class PseudoReceptor {

        private String rfc;
        private String nombre;
        private String proposito;
        private String domicilioFiscal;
        private String regimenFiscal;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoEmisor {

        private String rfc;
        private String nombre;
        private String regimenFiscal;
        private BigDecimal numDiasPagados;
        private BigDecimal totalPercepciones;
        private BigDecimal totalDeducciones;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PseudoConcepto {

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

    private static class LegoAssembler {

        private static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {

            Optional<Object> dict = Optional.ofNullable(m.get(k));
            return (Map<String, Object>) dict.orElseThrow();
        }

        private static Optional<Object> obtainObjFromKey(Map<String, Object> m, final String k) {
            return Optional.ofNullable(m.get(k));
        }
    }
}
