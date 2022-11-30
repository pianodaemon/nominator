package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.utils.JsonToMapHelper;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class Request extends JsonToMapHelper{

    protected Request(InputStreamReader reader) throws IOException {
        super(JsonToMapHelper.readFromReader(reader));
    }
}
