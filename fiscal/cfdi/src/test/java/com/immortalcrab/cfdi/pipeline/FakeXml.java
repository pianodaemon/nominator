package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class FakeXml {

    static final String DEFAULT_BUCKET = "datalake-cfdi-subscriber";

    private final Request cfdiReq;
    private final IStorage st;

    public static String render(Request cfdiReq, IStamp<PacReqChild, PacResChild> stamper, IStorage st) throws FormatError, StorageError {

        FakeXml ic = new FakeXml(cfdiReq, st);

        StringWriter cfdi = ic.shape();
        PacReqChild pacReq = ((FakeStamp) stamper).createPacReq(cfdi.toString(), "nowhere");
        PacResChild pacRes = stamper.impress(pacReq);

        ic.save(pacRes.getContent(), (String) cfdiReq.getDs().get("serie"), (String) cfdiReq.getDs().get("folio"));

        return pacRes.getReply();
    }

    public FakeXml(Request cfdiReq, IStorage st) {
        this.cfdiReq = cfdiReq;
        this.st = st;
    }

    private void save(StringWriter sw, String serie, String folio) throws FormatError, StorageError {

        StringBuffer buf = sw.getBuffer();
        byte[] in = buf.toString().getBytes(StandardCharsets.UTF_8);

        this.st.upload("text/xml", in.length, String.format("%s/%s%s.xml", DEFAULT_BUCKET ,serie, folio), new ByteArrayInputStream(in));

    }

    private StringWriter shape() {

        StringWriter sw = new StringWriter();

        sw.write("Content to get stamped");
        return sw;
    }
}
