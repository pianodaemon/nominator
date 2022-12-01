package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.PipelineError;
import com.immortalcrab.cfdi.error.RequestError;
import com.immortalcrab.cfdi.error.StorageError;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public String engage(final String kind, InputStreamReader isr)
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

        return openPayload(payload, (String kind, InputStreamReader instreamReader) -> engage(kind, instreamReader));
    }

    abstract protected void saveOnPersistance(IStorage st, PacRes pacResult) throws StorageError;

    abstract protected String openPayload(final IPayload payload, Pickard pic) throws DecodeError, RequestError, PipelineError, StorageError, FormatError;

    public interface Pickard {

        String route(final String kind, InputStreamReader isr) throws DecodeError, RequestError, PipelineError, StorageError, FormatError;
    }

    @FunctionalInterface
    public interface IPayload {

        String getReq();
    }

    public interface IStorage {

        public void upload(final String cType,
                final long len,
                final String fileName,
                InputStream inputStream) throws StorageError;

        public BufferedInputStream download(final String fileName) throws StorageError;

        public String getTargetName() throws StorageError;

        Optional<Map<String, String>> getPathPrefixes();

        default Optional<String> getPathPrefix(final String label) {

            if (getPathPrefixes().isPresent()) {

                Map<String, String> prefixes = getPathPrefixes().get();
                return Optional.ofNullable(prefixes.get(label));
            }

            return Optional.ofNullable(null);
        }
    }

    @FunctionalInterface
    public interface IDecodeStep<R extends Request> {

        public R render(InputStreamReader isr) throws RequestError, DecodeError;
    }

    @FunctionalInterface
    public interface IXmlStep<T extends PacRes, R extends Request> {

        public T render(R cfdiReq, IStamp stamper) throws FormatError, StorageError;
    }

    @FunctionalInterface
    interface IStamp<V extends PacReq, T extends PacRes> {

        public T impress(final V target) throws FormatError;
    }
}
