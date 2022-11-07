package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.InputStreamReader;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.javatuples.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
@AllArgsConstructor
@Getter
class Pipeline {

    private final @NonNull
    IStamp<?, ?> stamper;

    private final @NonNull
    IStorage storage;

    private final @NonNull
    ImmutableMap<String, Pair<IDecodeStep, IXmlStep>> scenarios;

    public String issue(final String kind, InputStreamReader isr)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        Optional<Pair<IDecodeStep, IXmlStep>> stages = Optional.ofNullable(this.getScenarios().get(kind));

        if (stages.isEmpty()) {

            throw new PipelineError("cfdi " + kind + " is unsupported");
        }

        /* First stage of the pipeline
           It stands for decoding what has been read
           from the data origin (in this case the infamous as400) */
        IDecodeStep sdec = stages.get().getValue0();
        Request cfdiReq = sdec.render(isr);

        /* Second stage of the pipeline
        It stands for hand craft a valid xml at sat */
        IXmlStep sxml = stages.get().getValue1();
        String uuid = sxml.render(cfdiReq, this.getStamper(), this.getStorage());

        return uuid;
    }
}
