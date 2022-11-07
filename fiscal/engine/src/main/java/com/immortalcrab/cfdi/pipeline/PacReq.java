package com.immortalcrab.cfdi.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
class PacReq {

    protected final @NonNull
    String xmlStr;

}
