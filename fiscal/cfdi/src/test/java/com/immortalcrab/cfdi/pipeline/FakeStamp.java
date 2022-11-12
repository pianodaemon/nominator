package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

public class FakeStamp implements IStamp<PacReqChild, PacResChild> {

    @Override
    public PacResChild impress(PacReqChild target) throws FormatError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public PacReqChild createPacReq(String xmlStr, String targetUri) {

        return new PacReqChild(xmlStr, targetUri);
    }

}
