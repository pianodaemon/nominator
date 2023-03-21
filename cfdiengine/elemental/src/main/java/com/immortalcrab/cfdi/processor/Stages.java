package com.immortalcrab.cfdi.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class Stages<T extends Processor.IDecodeStep, W extends Processor.IXmlStep> {

    final @NonNull
    T decodeStep;

    final @NonNull
    W xmlStep;
}
