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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
class NominaRequestDTO extends JsonRequest {

    public static final String VERSION = "4.0";

    DocPrincipalAttributes _docAttribs;
    PseudoReceptor _pr;
    PseudoEmisor _pe;
    List<PseudoConcepto> _pcs;

    private NominaRequestDTO(InputStreamReader reader) throws RequestError, DecodeError {

        super(reader);
        _docAttribs = new DocPrincipalAttributes();
        _pr = new PseudoReceptor();
        _pe = new PseudoEmisor();
        _pcs = new LinkedList<>();
        shapeDocAttribs();
        shapeRp();
        shapeEp();
        shapePcs();
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

    public static NominaRequestDTO render(InputStreamReader reader) throws RequestError, DecodeError {
        return new NominaRequestDTO(reader);
    }

    private void shapeDocAttribs() throws RequestError {

        try {

            Optional<Object> serie = LegoAssembler.obtainObjFromKey(this.getDs(), "serie");
            Optional<Object> folio = LegoAssembler.obtainObjFromKey(this.getDs(), "folio");
            Optional<Object> lugar = LegoAssembler.obtainObjFromKey(this.getDs(), "lugar_expedicion");
            Optional<Object> fecha = LegoAssembler.obtainObjFromKey(this.getDs(), "fecha");
            Optional<Object> moneda = LegoAssembler.obtainObjFromKey(this.getDs(), "moneda");

            _docAttribs.setSerie((String) serie.orElseThrow());
            _docAttribs.setFolio((String) folio.orElseThrow());
            _docAttribs.setLugarExpedicion((String) lugar.orElseThrow());
            _docAttribs.setFecha((String) fecha.orElseThrow());
            _docAttribs.setMoneda((String) moneda.orElseThrow());
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

    @NoArgsConstructor
    @Getter
    @Setter
    public static class DocPrincipalAttributes {

        private String fecha;
        private String lugarExpedicion;
        private String serie;
        private String folio;
        private String moneda;
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
