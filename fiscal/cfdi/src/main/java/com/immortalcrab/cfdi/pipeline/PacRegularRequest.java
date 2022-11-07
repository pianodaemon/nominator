package com.immortalcrab.cfdi.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
@AllArgsConstructor
@Getter
class PacRegularRequest {

    private final @NonNull
    String xmlStr;

}
