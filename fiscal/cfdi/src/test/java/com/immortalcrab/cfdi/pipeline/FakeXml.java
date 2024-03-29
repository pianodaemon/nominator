package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.pipeline.Pipeline.IStamp;
import java.io.BufferedInputStream;
import java.io.StringWriter;

public class FakeXml {

    private final NominaRequestDTO _req;

    private final StringWriter _sw;

    public FakeXml(NominaRequestDTO req) throws FormatError {

        _req = req;
        _sw = shape();
    }

    public static <R extends Request> PacRes render(R req, IStamp<PacReqChild, PacRes> stamper,
            BufferedInputStream certificate, BufferedInputStream signerKey) throws FormatError, StorageError {

        NominaRequestDTO dto = (NominaRequestDTO) req;
        FakeXml ic = new FakeXml(dto);

        String expectedName = dto.getDocAttributes().getSerie() + dto.getDocAttributes().getFolio();
        PacReqChild pacReq = ((FakeStamp) stamper).createPacReq(ic.toString(), "nowhere", expectedName);

        return stamper.impress(pacReq);
    }

    @Override
    public String toString() {
        return _sw.toString();
    }

    private StringWriter shape() {

        StringWriter sw = new StringWriter();

        sw.write("Content to get stamped");
        return sw;
    }
}
