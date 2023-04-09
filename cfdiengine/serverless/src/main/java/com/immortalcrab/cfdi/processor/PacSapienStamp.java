package com.immortalcrab.cfdi.processor;

import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;
import com.immortalcrab.cfdi.processor.Processor.IStamp;

import com.immortalcrab.cfdi.helpers.JsonToMapHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PacSapienStamp implements IStamp<PacReply> {

    private static final String USER_HEADER_NAME = "user";
    private static final String PASSWORD_HEADER_NAME = "password";
    private static final String CONT_TYPE_HEADER_NAME = "Content-Type";
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_URL = "https://services.test.sw.com.mx/security/authenticate";
    private static final String AUTH_STAMP_URL = "https://services.test.sw.com.mx/cfdi33/stamp/json/v4";

    private @NonNull
    final String login;

    private @NonNull
    final String passwd;

    @Override
    public PacReply impress(final String payload) throws EngineError {

        TargetConfDto targetDto = new TargetConfDto(login, passwd, AUTH_URL);
        log.debug(String.format("Asking for a token with this parameters %s", targetDto.toString()));
        SubmitionParamsDto spaDto = PacSapienStamp.ask4Token(HttpClients.createDefault(), targetDto, (final Map<String, Object> m) -> {
            if (((String) m.get("status")).equals("success")) {
                var dataMap = (Map<String, Object>) m.get("data");
                var token = (String) dataMap.get("token");
                return new SubmitionParamsDto(payload, token, AUTH_STAMP_URL);
            }

            throw new EngineError(String.format("%s (%s)", m.get("message"), m.get("messageDetail")), ErrorCodes.PAC_PARTY_ISSUES);
        });

        return submit4Stamp(HttpClients.createDefault(), spaDto, (final Map<String, Object> m) -> {
            if (((String) m.get("status")).equals("success")) {
                var dataMap = (Map<String, String>) m.get("data");
                dataMap.forEach((key, value) -> log.debug(String.format("Stamp data reply -> %s:%s", key, value)));
                StringBuilder buffer = new StringBuilder();
                var cont = new PacReply.Content(buffer.append(dataMap.get("cfdi")), "", "");
                return new PacReply(0, cont);
            }

            throw new EngineError(String.format("%s (%s)", m.get("message"), m.get("messageDetail")), ErrorCodes.PAC_PARTY_ISSUES);
        });
    }

    public static PacSapienStamp setup(final String carrier, final String login, final String passwd) {
        log.info("Setting up pac: " + carrier);
        return new PacSapienStamp(login, passwd);
    }

    public static <T> T ask4Token(CloseableHttpClient httpclient, final TargetConfDto target, ContentMapParser<T> cpa) throws EngineError {
        final String etmpl = "Sapien SSO interaction was not successful : %s";
        return interaction(httpclient, setupTokenPost(target), etmpl, cpa);
    }

    public static <T> T submit4Stamp(CloseableHttpClient httpclient, final SubmitionParamsDto target, ContentMapParser<T> cpa) throws EngineError {
        final String etmpl = "Sapien stamping was not successful : %s";
        return interaction(httpclient, setupStampPost(target), etmpl, cpa);
    }

    private static <T> T interaction(CloseableHttpClient httpclient, HttpPost stedyPost, String etmpl, ContentMapParser<T> cpa) throws EngineError {
        try ( CloseableHttpResponse response = httpclient.execute(stedyPost)) {
            if (response.getCode() == HttpStatus.SC_OK) {
                Map<String, Object> contMap = percolateContent(response);
                return cpa.parse(contMap);
            }
            final String emsg = String.format("Pac replied with an unexpected http code %s (%s)", response.getCode(), response.getReasonPhrase());
            throw new EngineError(String.format(etmpl, emsg), ErrorCodes.PAC_PARTY_ISSUES);
        } catch (IOException ex) {
            throw new EngineError(String.format(etmpl, ex.getMessage()), ErrorCodes.PAC_PARTY_ISSUES);
        }
    }

    private static HttpPost setupTokenPost(final TargetConfDto target) {
        HttpPost httpPost = new HttpPost(target.getTargetURL());
        httpPost.setHeader(USER_HEADER_NAME, target.getLogin());
        httpPost.setHeader(PASSWORD_HEADER_NAME, target.getPasswd());
        StringEntity ent = new StringEntity("", StandardCharsets.UTF_8);
        httpPost.setEntity(ent);

        return httpPost;
    }

    private static HttpPost setupStampPost(final SubmitionParamsDto target) {
        HttpPost httpPost = new HttpPost(target.getTargetURL());
        httpPost.setHeader(CONT_TYPE_HEADER_NAME, "application/json");
        httpPost.setHeader(AUTH_HEADER_NAME, "Bearer " + target.getToken());
        String json = String.format("{\"data\": \"%s\"}", target.getSignedXml());
        StringEntity ent = new StringEntity(json, StandardCharsets.UTF_8);
        httpPost.setEntity(ent);

        return httpPost;
    }

    private static Map<String, Object> percolateContent(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        InputStream reader = new ByteArrayInputStream(content.readAllBytes());
        Map<String, Object> contMap = JsonToMapHelper.readFromReader(new InputStreamReader(reader));
        EntityUtils.consume(entity);

        return contMap;
    }

    @AllArgsConstructor
    @Getter
    public static class TargetConfDto {

        private String login;
        private String passwd;
        private String targetURL;

        @Override
        public String toString() {
            return String.format("Pac { user: %s, password: %s, target_url: %s }", login, passwd, targetURL);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class SubmitionParamsDto {

        private String signedXml;
        private String token;
        private String targetURL;
    }

    @FunctionalInterface
    public interface ContentMapParser<T> {

        T parse(final Map<String, Object> m) throws EngineError;
    }
}
