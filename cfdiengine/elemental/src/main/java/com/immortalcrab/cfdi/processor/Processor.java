package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Getter
public abstract class Processor {

    private final @NonNull
    IStamp<? extends PacReply> stamper;

    private final @NonNull
    IStorage storage;

    private final @NonNull
    IStorage resources;

    private final @NonNull
    Map<String, Stages<? extends IDecodeStep, ? extends IXmlStep>> scenarios;

    public PacReply engage(final String kind, final String label, final String issuer,
            final Map<String, String> issuerAttribs, InputStreamReader isr)
            throws EngineError {

        Optional<Stages<? extends IDecodeStep, ? extends IXmlStep>> stages = Optional.ofNullable(this.getScenarios().get(kind));

        /* First stage of the pipeline
           It stands for decoding what has been read
           from the data origin  */
        ClientRequest cfdiReq = stages.orElseThrow(
                () -> new EngineError("cfdi " + kind + " is unsupported", ErrorCodes.REQUEST_INVALID))
                .getDecodeStep()
                .render(isr);

        /* Second stage of the pipeline
        It stands for hand craft a valid xml at sat */
        PacReply reply = stages.orElseThrow(
                () -> new EngineError("cfdi " + kind + " is unsupported", ErrorCodes.REQUEST_INVALID))
                .getXmlStep()
                .render(cfdiReq,
                        label,
                        this.getStamper(),
                        this.fetchCert(this.getResources(), issuerAttribs),
                        this.fetchKey(this.getResources(), issuerAttribs),
                        this.fetchCertNumber(issuerAttribs));

        saveOnPersistance(this.getStorage(),
                String.format("%s/%s", issuer, kind), reply);

        return reply;
    }

    public PacReply doIssue(final IPayload payload)
            throws EngineError {

        return openPayload(payload, this::engage);
    }

    protected abstract void saveOnPersistance(IStorage st, final String directory, PacReply pacResult) throws EngineError;

    protected abstract PacReply openPayload(final IPayload payload, Pickard pic) throws EngineError;

    protected abstract BufferedInputStream fetchCert(IStorage resources, final Map<String, String> issuerAttribs) throws EngineError;

    protected abstract BufferedInputStream fetchKey(IStorage resources, final Map<String, String> issuerAttribs) throws EngineError;

    protected abstract String fetchCertNumber(final Map<String, String> issuerAttribs) throws EngineError;

    public interface IStorage {

        public void upload(final String cType,
                final long len,
                final String fileName,
                InputStream inputStream) throws EngineError;

        public BufferedInputStream download(final String fileName) throws EngineError;

        public String getTargetName() throws EngineError;

        Optional<Map<String, String>> getPathPrefixes();

        default Optional<String> getPathPrefix(final String label) {

            try {
                return Optional.ofNullable(getPathPrefixes()
                        .orElseThrow(() -> new Exception("no path prefix")).get(label));
            } catch (Exception e) {
                return Optional.ofNullable(null);
            }
        }
    }

    @FunctionalInterface
    public interface Pickard {

        PacReply route(final String kind, final String label,
                final String issuer, final Map<String, String> issuerAttribs,
                InputStreamReader isr) throws EngineError;
    }

    @FunctionalInterface
    public interface IPayload {

        String getReq();
    }

    @FunctionalInterface
    public interface IStamp<T extends PacReply> {

        public T impress(final String label, final String payload) throws EngineError;
    }

    @FunctionalInterface
    public interface IDecodeStep<R extends ClientRequest> {

        public R render(InputStreamReader isr) throws EngineError;
    }

    @FunctionalInterface
    public interface IXmlStep<T extends PacReply, R extends ClientRequest> {

        public T render(R cfdiReq, final String label, IStamp<? extends PacReply> stamper,
                BufferedInputStream certificate, BufferedInputStream signerKey, final String certificateNo) throws EngineError;
    }
}
