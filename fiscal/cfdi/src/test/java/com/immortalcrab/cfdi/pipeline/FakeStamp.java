package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

public class FakeStamp implements IStamp {

    public FakeStamp() {
    }

    @Override
    public PacRes impress(PacReq target) throws FormatError {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
