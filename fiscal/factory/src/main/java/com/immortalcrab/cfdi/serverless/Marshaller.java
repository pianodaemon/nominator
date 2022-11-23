package com.immortalcrab.cfdi.serverless;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Marshaller {

    private static final ObjectMapper mapper = createObjectMapper();

    public static <T> void marshal(OutputStream output, T value) throws IOException {
        mapper.writeValue(output, value);
    }

    public static <T> T unmarshal(InputStream input, Class<T> type) throws IOException {
        return mapper.readValue(input, type);
    }

    public static <T> AWSEvent<T> unmarshalEvent(String input, Class<T> type) throws IOException {

        final TypeFactory typeFactory = mapper.getTypeFactory();
        return mapper.readValue(input, typeFactory.constructParametricType(AWSEvent.class, type));
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
