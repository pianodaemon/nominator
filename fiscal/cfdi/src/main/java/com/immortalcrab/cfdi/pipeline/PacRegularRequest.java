package com.immortalcrab.cfdi.pipeline;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
class PacRegularRequest extends PacReq {

    public PacRegularRequest(String xmlStr) {
        super(xmlStr);
    }

}
