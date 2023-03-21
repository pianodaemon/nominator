package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.helpers.JsonToMapHelper;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class ClientRequest extends JsonToMapHelper {

    protected ClientRequest(InputStreamReader reader) throws IOException {
        super(JsonToMapHelper.readFromReader(reader));
    }
}
