package com.immortalcrab.cfdi.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@AllArgsConstructor
public class JsonToMapHelper {

    protected final @NonNull
    Map<String, Object> ds;

    protected JsonToMapHelper(InputStreamReader reader) throws IOException {
        this(JsonToMapHelper.readFromReader(reader));
    }

    public static Map<String, Object> readFromReader(InputStreamReader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TypeReference<Map<String, Object>> tr = new TypeReference<Map<String, Object>>() {
        };

        return (mapper.readValue(reader, tr));
    }
}
