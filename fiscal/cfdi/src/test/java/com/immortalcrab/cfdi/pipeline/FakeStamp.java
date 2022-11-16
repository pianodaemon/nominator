package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import java.io.StringWriter;

public class FakeStamp implements IStamp<PacReqChild, PacRes> {

    static String EVERYDAY_UUID = "43ebcda4-6339-4e31-8d25-2aa28ee50e54";

    @Override
    public PacRes impress(PacReqChild target) throws FormatError {

        StringWriter sw = new StringWriter();

        // We fake the impressing step
        sw.write(target.getXmlStr().replace("to get", "is"));
        PacRes.Content content = new PacRes.Content(sw.getBuffer(), target.getTrailAlias(), EVERYDAY_UUID);
        return new PacRes(0, content);
    }

    public PacReqChild createPacReq(String xmlStr, String targetUri, String trailAlias) {

        return new PacReqChild(xmlStr, targetUri, trailAlias);
    }

}
