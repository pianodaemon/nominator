package com.immortalcrab.cfdi.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class PacRes {

    private final int _replyCode;

    private final Content _content;

    public PacRes(int replyCode, Content content) {
        _replyCode = replyCode;
        _content = content;
    }

    public int getRc() {
        return _replyCode;
    }

    public Content getContent() {
        return _content;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    protected static class Content {

        private final @NonNull
        StringBuffer buffer;

        private final @NonNull
        String name;

        private final @NonNull
        String id;
    }
}
