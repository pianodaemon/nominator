package com.immortalcrab.cfdi.stamper;

import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SapienStamper {

    private String token;

    public SapienStamper() {}

    public static void main(String[] args) {
        var s = new SapienStamper();
        if (s.authenticate("omontes.dev@gmail.com", "wN16^V8R@%dj")) {
            String signedXml = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\n" +
                    "<cfdi:Comprobante Version=\\\"4.0\\\" Serie=\\\"RRM\\\" Folio=\\\"5457\\\" Fecha=\\\"2022-12-09T13:00:19\\\" Sello=\\\"SgpSwG6bzUeGCbNislHk+lML/tEcZPhxK6970M+hyi7uhvSvwiEMwIbJQ9HACn0+x0EebR4S1RhtUehxHTUM/eGHicGf1AsuRkppEfcB7xcIQOuTv9bEQcWsL+KEFjML8Xk0+8S0omB/yQltLp3MljmqZxunkOyGItovbPcmfNEkhzq7M3BGHZfkEDPgXc+5E5O54LsOpLmye+VaOtOVxNDS+gnQPGoWfN/PIZmYtz5/lHpi6RbpY3k9JWh606HJ7cSSfZ0aZ9w8uChvfDfVqIJXd3ecbkmz23rDIs7dpHWnSRRbHMuFHvTCG3l8rhTwiiUwfP4SLX802nFaPHST+Q==\\\" NoCertificado=\\\"30001000000400002434\\\" Certificado=\\\"MIIFuzCCA6OgAwIBAgIUMzAwMDEwMDAwMDA0MDAwMDI0MzQwDQYJKoZIhvcNAQELBQAwggErMQ8wDQYDVQQDDAZBQyBVQVQxLjAsBgNVBAoMJVNFUlZJQ0lPIERFIEFETUlOSVNUUkFDSU9OIFRSSUJVVEFSSUExGjAYBgNVBAsMEVNBVC1JRVMgQXV0aG9yaXR5MSgwJgYJKoZIhvcNAQkBFhlvc2Nhci5tYXJ0aW5lekBzYXQuZ29iLm14MR0wGwYDVQQJDBQzcmEgY2VycmFkYSBkZSBjYWRpejEOMAwGA1UEEQwFMDYzNzAxCzAJBgNVBAYTAk1YMRkwFwYDVQQIDBBDSVVEQUQgREUgTUVYSUNPMREwDwYDVQQHDAhDT1lPQUNBTjERMA8GA1UELRMIMi41LjQuNDUxJTAjBgkqhkiG9w0BCQITFnJlc3BvbnNhYmxlOiBBQ0RNQS1TQVQwHhcNMTkwNjE3MTk0NDE0WhcNMjMwNjE3MTk0NDE0WjCB4jEnMCUGA1UEAxMeRVNDVUVMQSBLRU1QRVIgVVJHQVRFIFNBIERFIENWMScwJQYDVQQpEx5FU0NVRUxBIEtFTVBFUiBVUkdBVEUgU0EgREUgQ1YxJzAlBgNVBAoTHkVTQ1VFTEEgS0VNUEVSIFVSR0FURSBTQSBERSBDVjElMCMGA1UELRMcRUtVOTAwMzE3M0M5IC8gWElRQjg5MTExNlFFNDEeMBwGA1UEBRMVIC8gWElRQjg5MTExNk1HUk1aUjA1MR4wHAYDVQQLExVFc2N1ZWxhIEtlbXBlciBVcmdhdGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCN0peKpgfOL75iYRv1fqq+oVYsLPVUR/GibYmGKc9InHFy5lYF6OTYjnIIvmkOdRobbGlCUxORX/tLsl8Ya9gm6Yo7hHnODRBIDup3GISFzB/96R9K/MzYQOcscMIoBDARaycnLvy7FlMvO7/rlVnsSARxZRO8Kz8Zkksj2zpeYpjZIya/369+oGqQk1cTRkHo59JvJ4Tfbk/3iIyf4H/Ini9nBe9cYWo0MnKob7DDt/vsdi5tA8mMtA953LapNyCZIDCRQQlUGNgDqY9/8F5mUvVgkcczsIgGdvf9vMQPSf3jjCiKj7j6ucxl1+FwJWmbvgNmiaUR/0q4m2rm78lFAgMBAAGjHTAbMAwGA1UdEwEB/wQCMAAwCwYDVR0PBAQDAgbAMA0GCSqGSIb3DQEBCwUAA4ICAQBcpj1TjT4jiinIujIdAlFzE6kRwYJCnDG08zSp4kSnShjxADGEXH2chehKMV0FY7c4njA5eDGdA/G2OCTPvF5rpeCZP5Dw504RZkYDl2suRz+wa1sNBVpbnBJEK0fQcN3IftBwsgNFdFhUtCyw3lus1SSJbPxjLHS6FcZZ51YSeIfcNXOAuTqdimusaXq15GrSrCOkM6n2jfj2sMJYM2HXaXJ6rGTEgYmhYdwxWtil6RfZB+fGQ/H9I9WLnl4KTZUS6C9+NLHh4FPDhSk19fpS2S/56aqgFoGAkXAYt9Fy5ECaPcULIfJ1DEbsXKyRdCv3JY89+0MNkOdaDnsemS2o5Gl08zI4iYtt3L40gAZ60NPh31kVLnYNsmvfNxYyKp+AeJtDHyW9w7ftM0Hoi+BuRmcAQSKFV3pk8j51la+jrRBrAUv8blbRcQ5BiZUwJzHFEKIwTsRGoRyEx96sNnB03n6GTwjIGz92SmLdNl95r9rkvp+2m4S6q1lPuXaFg7DGBrXWC8iyqeWE2iobdwIIuXPTMVqQb12m1dAkJVRO5NdHnP/MpqOvOgLqoZBNHGyBg4Gqm4sCJHCxA1c8Elfa2RQTCk0tAzllL4vOnI1GHkGJn65xokGsaU4B4D36xh7eWrfj4/pgWHmtoDAYa8wzSwo2GVCZOs+mtEgOQB91/g==\\\" SubTotal=\\\"7185.82\\\" Descuento=\\\"1185.82\\\" Moneda=\\\"MXN\\\" Total=\\\"6000.0\\\" TipoDeComprobante=\\\"N\\\" Exportacion=\\\"01\\\" MetodoPago=\\\"PUE\\\" LugarExpedicion=\\\"67100\\\" xsi:schemaLocation=\\\"http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd http://www.sat.gob.mx/nomina12 http://www.sat.gob.mx/sitio_internet/cfd/nomina/nomina12.xsd\\\" xmlns:nomina12=\\\"http://www.sat.gob.mx/nomina12\\\" xmlns:cfdi=\\\"http://www.sat.gob.mx/cfd/4\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\">\n" +
                    "    <cfdi:Emisor Rfc=\\\"EKU9003173C9\\\" Nombre=\\\"ESCUELA KEMPER URGATE\\\" RegimenFiscal=\\\"601\\\"/>\n" +
                    "    <cfdi:Receptor Rfc=\\\"MOBO8001149UA\\\" Nombre=\\\"OMAR MONTES BRISEÑO\\\" DomicilioFiscalReceptor=\\\"55120\\\" RegimenFiscalReceptor=\\\"605\\\" UsoCFDI=\\\"CN01\\\"/>\n" +
                    "    <cfdi:Conceptos>\n" +
                    "        <cfdi:Concepto ClaveProdServ=\\\"84111505\\\" Cantidad=\\\"1\\\" ClaveUnidad=\\\"ACT\\\" Descripcion=\\\"Pago de nómina\\\" ValorUnitario=\\\"7185.82\\\" Importe=\\\"7185.82\\\" Descuento=\\\"1185.82\\\" ObjetoImp=\\\"01\\\"/>\n" +
                    "    </cfdi:Conceptos>\n" +
                    "    <cfdi:Complemento>\n" +
                    "        <nomina12:Nomina Version=\\\"1.2\\\" TipoNomina=\\\"O\\\" FechaPago=\\\"2022-10-25\\\" FechaInicialPago=\\\"2022-10-16\\\" FechaFinalPago=\\\"2022-10-31\\\" NumDiasPagados=\\\"15\\\" TotalPercepciones=\\\"7185.82\\\" TotalDeducciones=\\\"1185.82\\\" TotalOtrosPagos=\\\"0.0\\\">\n" +
                    "            <nomina12:Emisor RegistroPatronal=\\\"D4562908100\\\"/>\n" +
                    "            <nomina12:Receptor Curp=\\\"MOBO700225HDFNRM09\\\" NumSeguridadSocial=\\\"68742002174\\\" FechaInicioRelLaboral=\\\"2021-10-31\\\" Antigüedad=\\\"P52W\\\" TipoContrato=\\\"01\\\" TipoRegimen=\\\"02\\\" NumEmpleado=\\\"1520\\\" RiesgoPuesto=\\\"2\\\" PeriodicidadPago=\\\"04\\\" SalarioDiarioIntegrado=\\\"523.34\\\" ClaveEntFed=\\\"MEX\\\"/>\n" +
                    "            <nomina12:Percepciones TotalSueldos=\\\"7185.82\\\" TotalGravado=\\\"7185.82\\\" TotalExento=\\\"0.0\\\">\n" +
                    "                <nomina12:Percepcion TipoPercepcion=\\\"001\\\" Clave=\\\"001\\\" Concepto=\\\"sueldo\\\" ImporteGravado=\\\"7185.82\\\" ImporteExento=\\\"0.0\\\"/>\n" +
                    "            </nomina12:Percepciones>\n" +
                    "            <nomina12:Deducciones TotalOtrasDeducciones=\\\"198.2\\\" TotalImpuestosRetenidos=\\\"987.62\\\">\n" +
                    "                <nomina12:Deduccion TipoDeduccion=\\\"001\\\" Clave=\\\"001\\\" Concepto=\\\"IMSS\\\" Importe=\\\"198.2\\\"/>\n" +
                    "                <nomina12:Deduccion TipoDeduccion=\\\"002\\\" Clave=\\\"002\\\" Concepto=\\\"ISR\\\" Importe=\\\"987.62\\\"/>\n" +
                    "            </nomina12:Deducciones>\n" +
                    "            <nomina12:OtrosPagos>\n" +
                    "                <nomina12:OtroPago TipoOtroPago=\\\"002\\\" Clave=\\\"4455\\\" Concepto=\\\"mi otro paguito\\\" Importe=\\\"0.0\\\">\n" +
                    "                    <nomina12:SubsidioAlEmpleo SubsidioCausado=\\\"0.0\\\"/>\n" +
                    "                </nomina12:OtroPago>\n" +
                    "            </nomina12:OtrosPagos>\n" +
                    "        </nomina12:Nomina>\n" +
                    "    </cfdi:Complemento>\n" +
                    "</cfdi:Comprobante>";

            String stampedXml = s.stampXml(signedXml);
            System.out.println(stampedXml);
        }
    }

    public boolean authenticate(String login, String passwd) {

        token = "";
        boolean success = false;

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://services.test.sw.com.mx/security/authenticate");
            httpPost.setHeader("user", login);
            httpPost.setHeader("password", passwd);
            StringEntity ent = new StringEntity("", StandardCharsets.UTF_8);
            httpPost.setEntity(ent);

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                var respBodyStr = new String(content.readAllBytes(), StandardCharsets.UTF_8);

                var gson = new Gson();
                Map<String, Object> respBodyMap = gson.fromJson(respBodyStr, Map.class);

                if (response.getCode() == 200) {
                    var status = (String) respBodyMap.get("status");

                    if (status.equals("success")) {
                        var dataMap = (Map<String, Object>) respBodyMap.get("data");
                        token = (String) dataMap.get("token");
                        success = true;

                    } else {
                        System.out.println((String) respBodyMap.get("message"));
                        System.out.println((String) respBodyMap.get("messageDetail"));
                    }

                } else {
                    // Replace println by some logging here
                    System.out.println("authenticate request returned error: " + response.getCode() + " " + response.getReasonPhrase());
                    System.out.println((String) respBodyMap.get("message"));
                    System.out.println((String) respBodyMap.get("messageDetail"));
                }

                EntityUtils.consume(entity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    public String stampXml(String signedXml) {

        String stampedXml = "";

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpPost httpPost = new HttpPost("https://services.test.sw.com.mx/cfdi33/stamp/json/v4");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + token);
            String json = String.format("{\"data\": \"%s\"}", signedXml);
            StringEntity ent = new StringEntity(json, StandardCharsets.UTF_8);
            httpPost.setEntity(ent);

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {

                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                var respBodyStr = new String(content.readAllBytes(), StandardCharsets.UTF_8);

                var gson = new Gson();
                Map<String, Object> respBodyMap = gson.fromJson(respBodyStr, Map.class);

                if (response.getCode() == 200) {
                    var status = (String) respBodyMap.get("status");

                    if (status.equals("success")) {
                        var dataMap = (Map<String, String>) respBodyMap.get("data");
                        stampedXml = dataMap.get("cfdi");
                        System.out.println("STAMP SUCCEDED: " + stampedXml);

                    } else {
                        System.out.println((String) respBodyMap.get("message"));
                        System.out.println((String) respBodyMap.get("messageDetail"));
                    }

                } else {
                    // Replace println by some logging here
                    System.out.println("stampXml request returned error: " + response.getCode() + " " + response.getReasonPhrase());
                    System.out.println((String) respBodyMap.get("message"));
                    System.out.println((String) respBodyMap.get("messageDetail"));
                }

                EntityUtils.consume(entity);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stampedXml;
    }
}
