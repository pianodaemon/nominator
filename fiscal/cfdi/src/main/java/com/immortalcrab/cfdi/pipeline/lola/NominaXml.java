package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.Request;
import com.immortalcrab.cfdi.pipeline.IStorage;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import java.io.StringWriter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class NominaXml {

    private final @NonNull Request cfdiReq;

    private final @NonNull IStorage st;

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();

        ObjectFactory cfdiFactory = new ObjectFactory();
        var cfdi = cfdiFactory.createComprobante();
        cfdi.setVersion("4.0");
        cfdi.setTipoDeComprobante(CTipoDeComprobante.N);

        return sw;
    }
}
