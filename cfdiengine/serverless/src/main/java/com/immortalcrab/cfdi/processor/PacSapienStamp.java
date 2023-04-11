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
    public PacReply impress(final String xid, final String payload) throws EngineError {

        log.info(String.format("Asking to PAC for stamping of %s", xid));

        CFDI client = new CFDI();
        TimbradoCFDIRequest treq = new TimbradoCFDIRequest();
        treq.setUser(Integer.parseInt(login));
        treq.setPass(passwd);
        // SIGNED BY CUSTOMER
        treq.setTipoPeticion(1);
        treq.setXml(payload);
        treq.setIdComprobante(xid);

        TimbradoCFDIResponse tres = client.getBasicHttpBindingICFDI().timbrarCFDI(treq);
        StringBuilder buffer = new StringBuilder();

        if (tres.getCodigo() != 0) {
            final String emsg = String.format("Pac stamp experimenting problems: %s (%d)",
                    tres.getDescripcion().getValue(),
                    tres.getCodigo());
            throw new EngineError(emsg, ErrorCodes.PAC_PARTY_ISSUES);
        }

        return new PacReply(
                buffer.append(tres.getXml().getValue()),
                xid);
    }

    public static PacSapienStamp setup(final String carrier, final String login, final String passwd) {
        log.info("Setting up pac: " + carrier);
        return new PacSapienStamp(login, passwd);
    }
}
