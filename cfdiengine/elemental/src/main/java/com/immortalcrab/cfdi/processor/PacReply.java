package com.immortalcrab.cfdi.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class PacReply {

    private final int replyCode;

    private final Content content;

    public PacReply(int replyCode, Content content) {
        this.replyCode = replyCode;
        this.content = content;
    }

    public int getRc() {
        return replyCode;
    }

    public Content getContent() {
        return content;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    protected static class Content {

        private final @NonNull
        StringBuilder buffer;

        private final @NonNull
        String name;

        private final @NonNull
        String id;
    }
}
