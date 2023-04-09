package com.immortalcrab.cfdi.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PacReply {

    private final @NonNull
    StringBuilder buffer;

    private final @NonNull
    String name;
}
