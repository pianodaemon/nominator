package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.DecodeError;
import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.pipeline.Pipeline.IStorage;
import com.immortalcrab.cfdi.utils.JsonToMapHelper;
import com.immortalcrab.cfdi.utils.LegoAssembler;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
class ResourceDescriptor extends JsonToMapHelper {

    Prefixes _prefixes;
    Map<String, Issuer> _issuers;
    Map<String, Pac> _pacs;

    protected ResourceDescriptor(InputStreamReader reader) throws IOException, DecodeError {
        super(JsonToMapHelper.readFromReader(reader));

        _issuers = new HashMap<>();
        _pacs = new HashMap<>();

        try {

            {
                List<Map<String, Object>> pacs = LegoAssembler.obtainObjFromKey(this.getDs(), "pac");

                pacs.stream().map(i -> {

                    Pac p = new Pac(
                            LegoAssembler.obtainObjFromKey(i, "carrier"),
                            LegoAssembler.obtainObjFromKey(i, "login"),
                            LegoAssembler.obtainObjFromKey(i, "passwd")
                    );

                    return p;

                }).forEachOrdered(wr -> {
                    _pacs.put(wr.getCarrier(), wr);
                });
            }

            {
                List<Map<String, Object>> subs = LegoAssembler.obtainObjFromKey(this.getDs(), "issuers");

                subs.stream().map(i -> {

                    Issuer s = new Issuer(
                            LegoAssembler.obtainObjFromKey(i, "rfc"),
                            LegoAssembler.obtainObjFromKey(i, "cer"),
                            LegoAssembler.obtainObjFromKey(i, "key"),
                            LegoAssembler.obtainObjFromKey(i, "passwd")
                    );

                    return s;

                }).forEachOrdered(o -> {
                    _issuers.put(o.getRfc(), o);
                });
            }

            {
                Map<String, Object> mres = LegoAssembler.obtainMapFromKey(this.getDs(), "res");

                _prefixes = new Prefixes(
                        LegoAssembler.obtainObjFromKey(mres, "prefix_ssl"),
                        LegoAssembler.obtainObjFromKey(mres, "prefix_xslt"));
            }
        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of resource descriptor is missing");
            throw new DecodeError("mandatory element in resource descriptor is missing", ex);
        }
    }

    static ResourceDescriptor fetchProfile(IStorage storage, final String profile) throws StorageError, DecodeError {

        try ( BufferedInputStream isr = storage.download(profile)) {
            return new ResourceDescriptor(new InputStreamReader(isr, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            final String msg = String.format("Profile %s can not be loaded", profile);
            throw new StorageError(msg);
        }
    }

    public Prefixes getPrefixes() {

        return _prefixes;
    }

    public Optional<Issuer> getIssuer(final String name) {

        return Optional.ofNullable(_issuers.get(name));
    }

    public Optional<Pac> getPacSettings(final String name) {

        return Optional.ofNullable(_pacs.get(name));
    }

    @AllArgsConstructor
    @Getter
    public static class Prefixes {

        private final String ssl;
        private final String xslt;

        public Map<String, String> turnIntoMap() {

            return Map.of("ssl", ssl, "xslt", xslt);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Pac {

        private final String carrier;
        private final String login;
        private final String passwd;

    }

    @AllArgsConstructor
    @Getter
    public static class Issuer {

        private final String rfc;
        private final String cer;
        private final String key;
        private final String passwd;

        public Map<String, String> turnIntoMap() {

            return Map.of("rfc", rfc, "key", key, "cert", cer, "passwd", passwd);
        }
    }
}
