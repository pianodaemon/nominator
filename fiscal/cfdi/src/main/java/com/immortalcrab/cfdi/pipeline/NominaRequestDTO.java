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

    List<PseudoConcepto> _pcs = new LinkedList<>();

    private NominaRequestDTO(InputStreamReader reader) throws RequestError, DecodeError {
        super(reader);
        this.shapePcs();
    }

    public List<PseudoConcepto> getPseudoConceptos() {
        return _pcs;
    }

    public static NominaRequestDTO render(InputStreamReader reader) throws RequestError, DecodeError {
        NominaRequestDTO req = new NominaRequestDTO(reader);
        return req;
    }

    private List<PseudoConcepto> shapePcs() throws RequestError {

        Optional<Object> cs = NominaRequestDTO.LegoTagAssembler.obtainObjFromKey(this.getDs(), "conceptos");

        try {

            List<Map<String, Object>> items = (List<Map<String, Object>>) cs.orElseThrow();

            items.stream().map(i -> {

                PseudoConcepto p = new PseudoConcepto();
                p.setClaveProdServ((String) LegoTagAssembler.obtainObjFromKey(i, "clave_prod_serv").orElseThrow());
                p.setDescripcion((String) LegoTagAssembler.obtainObjFromKey(i, "descripcion").orElseThrow());
                p.setClaveUnidad((String) LegoTagAssembler.obtainObjFromKey(i, "clave_unidad").orElseThrow());
                p.setObjImp((String) LegoTagAssembler.obtainObjFromKey(i, "objeto_imp").orElseThrow());

                {
                    Double cantidad = (Double) LegoTagAssembler.obtainObjFromKey(i, "cantidad").orElseThrow();
                    p.setCantidad(new BigDecimal(cantidad));
                }

                {
                    Double valorUnitario = (Double) LegoTagAssembler.obtainObjFromKey(i, "valor_unitario").orElseThrow();
                    p.setValorUnitario(new BigDecimal(valorUnitario));
                }

                {
                    Double importe = (Double) LegoTagAssembler.obtainObjFromKey(i, "importe").orElseThrow();
                    p.setImporte(new BigDecimal(importe));
                }

                {
                    Double descuento = (Double) LegoTagAssembler.obtainObjFromKey(i, "descuento").orElseThrow();
                    p.setDescuento(new BigDecimal(descuento));
                }

                return p;

            }).forEachOrdered(p -> {
                _pcs.add(p);
            });

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of Concepto tag is missing");
            throw new RequestError("mandatory element in request is missing", ex);
        }

        return _pcs;
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
