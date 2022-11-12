package com.immortalcrab.cfdi.pipeline;

import java.io.StringWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PacRes {

    StringWriter content;
    String reply;
}
