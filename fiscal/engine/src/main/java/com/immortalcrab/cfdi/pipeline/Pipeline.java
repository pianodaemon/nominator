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
    Map<String, Pair<? extends IDecodeStep, ? extends IXmlStep>> scenarios;

    public String engage(final String kind, final Map<String, String> issuerAttribs, InputStreamReader isr)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        Optional<Pair<? extends IDecodeStep, ? extends IXmlStep>> stages = Optional.ofNullable(this.getScenarios().get(kind));

        /* First stage of the pipeline
           It stands for decoding what has been read
           from the data origin  */
        Request cfdiReq = stages.orElseThrow(() -> new PipelineError("cfdi " + kind + " is unsupported"))
                .getValue0()
                .render(isr);

        /* Second stage of the pipeline
        It stands for hand craft a valid xml at sat */
        PacRes pacResult = stages.orElseThrow(() -> new PipelineError("cfdi " + kind + " is unsupported"))
                .getValue1()
                .render(cfdiReq,
                        this.getStamper(),
                        this.fetchCert(this.getResources(), issuerAttribs),
                        this.fetchKey(this.getResources(), issuerAttribs),
                        this.fetchPassword(issuerAttribs));

        saveOnPersistance(this.getStorage(), pacResult);

        return pacResult.getContent().getId();
    }

    public String doIssue(final IPayload payload)
            throws DecodeError, RequestError, PipelineError, StorageError, FormatError {

        return openPayload(payload, (var kind, var issuer, var instreamReader) -> {
            return engage(kind, issuer, instreamReader);
        });
    }

    protected abstract void saveOnPersistance(IStorage st, PacRes pacResult) throws StorageError;

    protected abstract String openPayload(final IPayload payload, Pickard pic) throws DecodeError, RequestError, PipelineError, StorageError, FormatError;

    protected abstract BufferedInputStream fetchCert(IStorage resources, final Map<String, String> issuerAttribs) throws StorageError;

    protected abstract BufferedInputStream fetchKey(IStorage resources, final Map<String, String> issuerAttribs) throws StorageError;

    protected abstract String fetchPassword(final Map<String, String> issuerAttribs) throws StorageError;

    @FunctionalInterface
    public interface Pickard {

        String route(final String kind, final Map<String, String> issuerAttribs, InputStreamReader isr) throws DecodeError, RequestError, PipelineError, StorageError, FormatError;
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

        public T render(R cfdiReq, IStamp stamper,
                BufferedInputStream certificate, BufferedInputStream signerKey, final String passwd) throws FormatError, StorageError;
    }

    @FunctionalInterface
    public interface IStamp<V extends PacReq, T extends PacRes> {

        public T impress(final V target) throws FormatError;
    }
}
