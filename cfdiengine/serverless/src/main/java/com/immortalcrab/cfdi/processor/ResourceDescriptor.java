package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.helpers.JsonToMapHelper;
import com.immortalcrab.cfdi.helpers.LegoAssembler;
import com.immortalcrab.cfdi.processor.Processor.IStorage;

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
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
class ResourceDescriptor extends JsonToMapHelper {

    Prefixes prefixes;
    Map<String, Issuer> issuers;
    Map<String, Pac> pacs;

    protected ResourceDescriptor(InputStreamReader reader) throws IOException, EngineError {
        super(JsonToMapHelper.readFromReader(reader));

        issuers = new HashMap<>();
        pacs = new HashMap<>();

        try {

            List<Map<String, Object>> alternatives;
            alternatives = LegoAssembler.obtainObjFromKey(this.getDs(), "pac");

            alternatives.stream().map(i -> new Pac(
                    LegoAssembler.obtainObjFromKey(i, Pac.kCARRIER),
                    LegoAssembler.obtainObjFromKey(i, Pac.kLOGIN),
                    LegoAssembler.obtainObjFromKey(i, Pac.kPASSWD)
            )).forEachOrdered(p -> this.pacs.put(p.getCarrier(), p));

            List<Map<String, Object>> subs = LegoAssembler.obtainObjFromKey(this.getDs(), "issuers");

            subs.stream().map(i -> new Issuer(
                    LegoAssembler.obtainObjFromKey(i, Issuer.kRFC),
                    LegoAssembler.obtainObjFromKey(i, Issuer.kCERT),
                    LegoAssembler.obtainObjFromKey(i, Issuer.kPRIVATEKEY),
                    LegoAssembler.obtainObjFromKey(i, Issuer.kPASSWD)
            )).forEachOrdered(o -> issuers.put(o.getRfc(), o));

            Map<String, Object> mres = LegoAssembler.obtainMapFromKey(this.getDs(), "res");

            prefixes = new Prefixes(
                    LegoAssembler.obtainObjFromKey(mres, "prefix_ssl"),
                    LegoAssembler.obtainObjFromKey(mres, "prefix_xslt"));

        } catch (NoSuchElementException ex) {
            log.error("One or more of the mandatory elements of resource descriptor is missing");
            throw new EngineError("mandatory element in resource descriptor is missing", ex, ErrorCodes.UNKNOWN_ISSUE);
        }
    }

    static ResourceDescriptor fetchProfile(IStorage storage, final String profile) throws EngineError {

        try ( BufferedInputStream isr = storage.download(profile)) {
            return new ResourceDescriptor(new InputStreamReader(isr, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            final String msg = String.format("Profile %s can not be loaded", profile);
            throw new EngineError(msg, ex, ErrorCodes.UNKNOWN_ISSUE);
        }
    }

    public Prefixes getPrefixes() {

        return prefixes;
    }

    public Optional<Issuer> getIssuer(final String name) {

        return Optional.ofNullable(issuers.get(name));
    }

    public Optional<Pac> getPacSettings(final String name) {

        return Optional.ofNullable(pacs.get(name));
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

        private static String kCARRIER = "carrier";
        private static String kLOGIN = "login";
        private static String kPASSWD = "passwd";

        private final @NonNull
        String carrier;
        private final @NonNull
        String login;
        private final @NonNull
        String passwd;
    }

    @AllArgsConstructor
    @Getter
    public static class Issuer {

        private static String kRFC = "rfc";
        private static String kPRIVATEKEY = "pk";
        private static String kCERT = "cert";
        private static String kPASSWD = "passwd";

        private final String rfc;
        private final String cert;
        private final String key;
        private final String passwd;

        public Map<String, String> turnIntoMap() {

            return Map.of(kRFC, rfc, kPRIVATEKEY, key, kCERT, cert, kPASSWD, passwd);
        }
    }
}
