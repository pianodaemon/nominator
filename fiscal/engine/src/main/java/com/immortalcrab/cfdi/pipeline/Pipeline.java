package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import org.javatuples.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Getter
public abstract class Pipeline {

    private final @NonNull
    IStamp stamper;

    private final @NonNull
    IStorage storage;

    private final @NonNull
    IStorage resources;

    private final @NonNull
    Map<String, Pair<IDecodeStep, IXmlStep>> scenarios;

    private String engage(final String kind, InputStreamReader isr)
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
        PacRes pacResult = sxml.render(cfdiReq, this.getStamper());
        saveOnPersistance(this.getStorage(), pacResult);

        return pacResult.getContent().getId();
    }

    public String doIssue(final IPayload payload)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        BufferedInputStream bf = this.getStorage().download(payload.getReq());
        InputStreamReader isr = new InputStreamReader(bf, StandardCharsets.UTF_8);

        return this.engage(payload.getKind(), isr);
    }

    protected String doIssue(final String kind, InputStreamReader isr)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        return this.engage(kind, isr);
    }

    abstract protected void saveOnPersistance(IStorage st, PacRes pacResult) throws StorageError;
}
