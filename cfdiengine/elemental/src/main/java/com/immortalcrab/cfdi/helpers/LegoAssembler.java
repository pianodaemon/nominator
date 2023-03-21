package com.immortalcrab.cfdi.helpers;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class LegoAssembler {

    public static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
        return LegoAssembler.obtainObjFromKey(m, k);
    }

    public static <T> T obtainObjFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
        return (T) Optional.ofNullable(m.get(k)).orElseThrow();
    }
}