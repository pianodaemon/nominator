package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.processor.Processor.IStamp;

import com.servisim.timbrado.CFDI;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import org.datacontract.schemas._2004._07.servisim_timbrado_ws.TimbradoCFDIRequest;
import org.datacontract.schemas._2004._07.servisim_timbrado_ws.TimbradoCFDIResponse;

@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PacSapienStamp implements IStamp<PacReply> {

    private @NonNull
    final String login;

    private @NonNull
    final String passwd;

    @Override
    public PacReply impress(final String payload) throws EngineError {

        CFDI client = new CFDI();
        TimbradoCFDIRequest treq = new TimbradoCFDIRequest();
        // SIGNED BY CUSTOMER
        treq.setIdComprobante("0001");
        treq.setTipoPeticion(1);
        treq.setXml(payload);

        TimbradoCFDIResponse tres = client.getBasicHttpBindingICFDI().timbrarCFDI(treq);
        if (tres.getCodigo() != 0) {
            final String emsg = String.format("Pac stamp experimenting problems: %s (%d)", tres.getDescripcion().toString(), tres.getCodigo());
            throw new EngineError(emsg, ErrorCodes.PAC_PARTY_ISSUES);
        }

        StringBuilder buffer = new StringBuilder();
        var cont = new PacReply.Content(buffer.append(tres.getXml().toString()), "", "");
        return new PacReply(0, cont);
    }

    public static PacSapienStamp setup(final String carrier, final String login, final String passwd) {
        log.info("Setting up pac: " + carrier);
        return new PacSapienStamp(login, passwd);
    }
}
