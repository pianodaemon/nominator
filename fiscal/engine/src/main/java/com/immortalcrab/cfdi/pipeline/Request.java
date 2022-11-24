package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.RequestError;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
abstract class Request {

    protected final @NonNull Map<String, Object> ds;

    protected abstract Map<String, Object> craftImpt() throws RequestError;

    protected void captureSymbol(final String label, final Object value) {
        this.getDs().put(label, value);
    }
}
