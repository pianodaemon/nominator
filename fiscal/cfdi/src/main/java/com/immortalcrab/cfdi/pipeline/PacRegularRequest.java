package com.immortalcrab.cfdi.pipeline;

import lombok.Getter;

@Getter
class PacRegularRequest extends PacReq {

    // https://es.wikipedia.org/wiki/Identificador_de_recursos_uniforme
    private static final String REGULAR_URI = "http://www.todito.com/facturacion";

    public PacRegularRequest(String xmlStr) {

        super(xmlStr,
                PacRegularRequest.REGULAR_URI, "algo"
        );
    }

}
