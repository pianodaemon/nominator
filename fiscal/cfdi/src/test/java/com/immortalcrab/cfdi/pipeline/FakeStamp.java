package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.FakeStamp.PacReqChild;
import com.immortalcrab.cfdi.pipeline.FakeStamp.PacResChild;
import java.io.StringWriter;

public class FakeStamp implements IStamp<PacReqChild, PacResChild> {

    @Override
    public PacResChild impress(PacReqChild target) throws FormatError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public PacReqChild createPacReq(String xmlStr, String targetUri) {

        return new PacReqChild(xmlStr, targetUri);
    }

    public class PacReqChild extends PacReq {

        public PacReqChild(String xmlStr, String targetUri) {
            super(xmlStr, targetUri);
        }

    }

    public class PacResChild extends PacRes {

        StringWriter getContent() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        String getID() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

    }
}
