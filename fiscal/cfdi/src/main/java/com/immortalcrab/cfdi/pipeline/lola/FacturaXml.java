package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.FormatError;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import java.io.StringWriter;

public class FacturaXml {

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();

        ObjectFactory cfdiFactory = new ObjectFactory();
        var cfdi = cfdiFactory.createComprobante();
        cfdi.setVersion("4.0");
        cfdi.setTipoDeComprobante(CTipoDeComprobante.I);

        return sw;
    }
}
