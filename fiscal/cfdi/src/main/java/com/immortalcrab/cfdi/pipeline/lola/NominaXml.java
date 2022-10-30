package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.Request;
import com.immortalcrab.cfdi.pipeline.IStorage;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.nomina12.Nomina;

import java.io.StringWriter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class NominaXml {

    private final @NonNull Request cfdiReq;
    private final @NonNull IStorage st;

    public static String render(Request cfdiReq, IStorage st) throws FormatError, StorageError {

        NominaXml ic = new NominaXml(cfdiReq, st);
        StringWriter cfdi = ic.shape();

        return "It must be slightly implemented as it was in lola";
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();

        ObjectFactory cfdiFactory = new ObjectFactory();
        var cfdi = cfdiFactory.createComprobante();
        cfdi.setVersion("4.0");
        cfdi.setTipoDeComprobante(CTipoDeComprobante.N);

        return sw;
    }
}
