package com.immortalcrab.cfdi.helpers;

import com.immortalcrab.cfdi.processor.Processor.IPayload;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Payload implements IPayload, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("req")
    private String req = null;

    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Payload p = (Payload) o;
        return Objects.equals(this.req, p.req);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(req);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class Payload {\n");

        sb.append("    req: ").append(toIndentedString(req)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {

        if (o == null) {
            return "null";
        }

        return o.toString().replace("\n", "\n    ");
    }
}
