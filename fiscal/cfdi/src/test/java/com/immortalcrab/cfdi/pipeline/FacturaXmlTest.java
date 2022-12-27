package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.toolbox.MontesToolbox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class FacturaXmlTest {

    private ClassLoader _cloader;

    @BeforeAll
    public void setUpClass() {

        _cloader = getClass().getClassLoader();
    }

//    @Test
//    public void loadReqFromReader() {
//
//        try {
//            InputStream isTesting = _cloader.getResourceAsStream("xmlsamples/factura.xml");
//            String facturaTesting = new String(isTesting.readAllBytes(), StandardCharsets.UTF_8);
//
//            InputStream is = _cloader.getResourceAsStream("jsonreqs/facturareq.json");
//            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
//            FacturaXml facturaGenerated = new FacturaXml(new FacturaRequestDTO(reader), null, null, null);
//
//            char[] csc0 = facturaGenerated.toString().replace("\n", "").replace("\r", "").toCharArray();
//            char[] csc1 = facturaTesting.toString().replace("\n", "").replace("\r", "").toCharArray();
//            for (int i = 0; i < csc0.length; i++) {
//                assertTrue(csc0[i] == csc1[i]);
//            }
//
//        } catch (Exception ex) {
//            assertNull(ex);
//        }
//    }

    public static void main(String[] args) {

        try {
            byte[] profile = Files.readAllBytes(Paths.get("/home/userd/dev/nominator/fiscal/cfdi/src/test/resources/jsonprofiles/default.json"));
            var profileBais = new ByteArrayInputStream(profile);
            InputStreamReader profileReader = new InputStreamReader(profileBais, StandardCharsets.UTF_8);
            ResourceDescriptor resDescriptor = new ResourceDescriptor(profileReader);
            var issuer = resDescriptor.getIssuer("RRM031001QE7").orElseThrow();

            byte[] j = Files.readAllBytes(Paths.get("/home/userd/dev/nominator/fiscal/cfdi/src/test/resources/jsonreqs/facturareq.json"));
            var j0 = new ByteArrayInputStream(j);
            var reqReader = new InputStreamReader(j0, StandardCharsets.UTF_8);

            var mToolbox = new MontesToolbox();

            String localPath = "/home/userd/immortalcrab/resources/ssl/RRM031001QE7/";
            byte[] c = Files.readAllBytes(Paths.get(localPath + issuer.getCer()));
            String c0 = mToolbox.renderCertificate(c);
            var c1 = new ByteArrayInputStream(c0.getBytes(StandardCharsets.UTF_8));
            var certificate = new BufferedInputStream(c1);

            byte[] p = Files.readAllBytes(Paths.get(localPath + issuer.getPem()));
            var p0 = new ByteArrayInputStream(p);

            String[] certNoArr = issuer.getCer().split("\\.cer");
            FacturaXml facturaGenerated = new FacturaXml(new FacturaRequestDTO(reqReader), certificate, certNoArr[0]);
            System.out.println(facturaGenerated);

            String facturaXml = facturaGenerated.toString();
            var n = new ByteArrayInputStream(facturaXml.getBytes(StandardCharsets.UTF_8));
            var n0 = new InputStreamReader(n);
            var n1 = new BufferedReader(n0);
            Source xsltSource = new StreamSource(new File("/home/userd/dev/lola4/DOS/resources/b/cadenaoriginal_4_0.xslt"));
            String originalStr = mToolbox.renderOriginalStr(n1, xsltSource);
            System.out.println(originalStr);

            var pemBr = new BufferedReader(new InputStreamReader(p0));
            String sello = mToolbox.signOriginalStr(pemBr, originalStr);
            System.out.println(sello);
            facturaGenerated.setSello(sello);
            System.out.println(facturaGenerated);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
