package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import java.io.StringWriter;

public class FakeStamp implements IStamp<PacReqChild, PacResChild> {

    static String EVERYDAY_UUID = "43ebcda4-6339-4e31-8d25-2aa28ee50e54";

    @Override
    public PacResChild impress(PacReqChild target) throws FormatError {

        StringWriter sw = new StringWriter();
        sw.write(target.getXmlStr().replace("to get", "is"));
        PacResChild res = new PacResChild(sw, EVERYDAY_UUID);
        return res;
    }

    public PacReqChild createPacReq(String xmlStr, String targetUri) {

        return new PacReqChild(xmlStr, targetUri);
    }

}
