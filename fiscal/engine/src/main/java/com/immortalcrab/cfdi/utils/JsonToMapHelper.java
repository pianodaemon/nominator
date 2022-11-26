package com.immortalcrab.cfdi.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.RequestError;
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

    protected JsonToMapHelper(InputStreamReader reader) throws RequestError, DecodeError {
        this(JsonToMapHelper.readFromReader(reader));
    }

    public static Map<String, Object> readFromReader(InputStreamReader reader) throws DecodeError {
        ObjectMapper mapper = new ObjectMapper();

        TypeReference<Map<String, Object>> tr = new TypeReference<Map<String, Object>>() {
        };

        try {
            return (mapper.readValue(reader, tr));
        } catch (IOException ex) {
            log.warn(ex.getMessage());
            throw new DecodeError("Issue found when reading json buffer", ex);
        }
    }
}