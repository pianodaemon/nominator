package com.immortalcrab.nominator.formats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class NominaPdf {

//    public static void main(String[] args) {
//        try {
//            String fileContent = Files.readString(Paths.get("/home/userd/dev/lola4/DOS/cfdi/service/formats/sample5.json"));
//            render(fileContent, "");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static BufferedInputStream render(String json, InputStream isXml) throws Exception {

        final NominaParser nomParser = new NominaParser(isXml);

        Gson gson = new Gson();
        Map<String, Object> ds = gson.fromJson(json, HashMap.class);
        ds.put("no_certificado", "00001000000413586324");
        ds.put("UUID", nomParser.getUUID());
        ds.put("CDIGITAL_SAT", "00001000000413073350");
        ds.put("FECHSTAMP", "2021-11-22T10:04:05");
        ds.put("SELLO_CFD", nomParser.getSelloCfd());
        ds.put("SELLO_SAT", nomParser.getSelloSat());
        ds.put("CADENA_ORIGINAL_TFD", "||1.1|63D91EE9-FAFC-4F9F-B69F-A3D65F8C0261|2021-08-25T22:54:52|SVT110323827|PfKJXRxWxMMsClxmr5ROzrFLFlaXFmcq/3B5gzUZegxBXG8bs/1MkrKkxJXsmEVwEVfhM9vx17Fm5YnqrgbI7rv1gh+5lH+WlPa3lNzKupLBwAWTi493hRrUVKYvth97fc0+mPv6hzDmBSziSLiQQ2WCppKb2jANIqQplmE70BKTe5q3C0fUhjSRghWmgRAdyKZ9AMjGNrxZ3pkYE14yx7SxP6Xc2YNEnsTfBbbKkZGcGzMwLBzuG3uJtXR2JFFPdkEmZymEZCL3UejX6iIi6oDmjChmrryjWC8jSWGCHnp678sM456yoNz5uRfNb64iVvYWHP+h3VpKTFrlVZnDuA==|00001000000413073350||");

        byte[] pdfBytes = null;
        try {
            var dsEmisor = (Map<String, String>) ds.get("emisor");
            var dsReceptor = (Map<String, String>) ds.get("receptor");
            var dsConceptos = (List<Map<String, Object>>) ds.get("conceptos");
            var dsNomina = (Map<String, Object>) ds.get("nomina");
            var dsNominaReceptor = (Map<String, Object>) dsNomina.get("receptor");
            var dsNominaPercepciones = (Map<String, Object>) dsNomina.get("percepciones");
            var dsNominaDeducciones = (Map<String, Object>) dsNomina.get("deducciones");
            var dsNominaOtrosPagos = (Map<String, Object>) dsNomina.get("otros_pagos");
            var percepList = (List<Map<String, Object>>) dsNominaPercepciones.get("lista");
            var deducList = (List<Map<String, Object>>) dsNominaDeducciones.get("lista");
            var otrosPagosList = (List<Map<String, Object>>) dsNominaOtrosPagos.get("lista");

            // Obtener catalogos del SAT -----------------------------------------------------------------------
            Map<String, String> regimenFiscalCat = SatCatalog.getCatalog("regimen_fiscal");
            Map<String, String> claveUnidadCat = SatCatalog.getCatalog("clave_unidad");
            Map<String, String> claveProdServCat = SatCatalog.getCatalog("clave_prod_serv");
            Map<String, String> tipoNominaCat = SatCatalog.getCatalog("tipo_nomina");
            Map<String, String> tipoJornadaCat = SatCatalog.getCatalog("tipo_jornada");
            Map<String, String> riesgoPuestoCat = SatCatalog.getCatalog("riesgo_puesto");
            Map<String, String> tipoContratoCat = SatCatalog.getCatalog("tipo_contrato");
            Map<String, String> tipoRegimenCat = SatCatalog.getCatalog("tipo_regimen");
            Map<String, String> claveEntFedCat = SatCatalog.getCatalog("clave_ent_fed");
            Map<String, String> periodicidadPagoCat = SatCatalog.getCatalog("periodicidad_pago");
            Map<String, String> tipoPercepcionCat = SatCatalog.getCatalog("tipo_percepcion");
            Map<String, String> tipoDeduccionCat = SatCatalog.getCatalog("tipo_deduccion");
            Map<String, String> tipoOtroPagoCat = SatCatalog.getCatalog("tipo_otro_pago");

            // Formatear importes y agregar nombres/descripciones a campos -------------------------------------
            var df = new DecimalFormat("###,##0.00");

            ds.put("descuento", df.format((Double) ds.get("descuento")));
            ds.put("subtotal", df.format((Double) ds.get("subtotal")));
            Double totalDouble = (Double) ds.get("total");



            // Continuar dando formato -------------------------------------
            dsEmisor.put("regimen_fiscal_descr", regimenFiscalCat.get(dsEmisor.get("regimen_fiscal")));

            for (Map<String, Object> c : dsConceptos) {
                // formatting numbers
                c.put("valor_unitario", df.format((Double) c.get("valor_unitario")));
                c.put("importe", df.format((Double) c.get("importe")));
                // adding names/descriptions from catalogs
                c.put("clave_unidad_descr", claveUnidadCat.get((String) c.get("clave_unidad")));
                c.put("clave_prod_serv_descr", claveProdServCat.get((String) c.get("clave_prod_serv")));
            }

            dsNomina.put("num_dias_pagados", String.valueOf(((Double) dsNomina.get("num_dias_pagados")).intValue()));
            dsNomina.put("tipo_nomina_descr", tipoNominaCat.get(dsNomina.get("tipo_nomina")));
            dsNominaReceptor.put("tipo_jornada_descr", tipoJornadaCat.get(dsNominaReceptor.get("tipo_jornada")));
            dsNominaReceptor.put("riesgo_puesto_descr", riesgoPuestoCat.get(dsNominaReceptor.get("riesgo_puesto")));
            dsNominaReceptor.put("tipo_contrato_descr", tipoContratoCat.get(dsNominaReceptor.get("tipo_contrato")));
            dsNominaReceptor.put("tipo_regimen_descr", tipoRegimenCat.get(dsNominaReceptor.get("tipo_regimen")));
            dsNominaReceptor.put("clave_ent_fed_descr", claveEntFedCat.get(dsNominaReceptor.get("clave_ent_fed")));
            dsNominaReceptor.put("periodicidad_pago_descr", periodicidadPagoCat.get(dsNominaReceptor.get("periodicidad_pago")));

            dsNominaPercepciones.put("total_gravado", df.format((Double) dsNominaPercepciones.get("total_gravado")));
            dsNominaPercepciones.put("total_exento", df.format((Double) dsNominaPercepciones.get("total_exento")));
            for (Map<String, Object> p : percepList) {
                // formatting numbers
                p.put("importe_gravado", df.format((Double) p.get("importe_gravado")));
                p.put("importe_exento", df.format((Double) p.get("importe_exento")));
                // adding names/descriptions from catalogs
                p.put("tipo_percepcion_descr", tipoPercepcionCat.get((String) p.get("tipo_percepcion")));
            }

            dsNominaDeducciones.put("total_otras_deducciones", df.format((Double) dsNominaDeducciones.get("total_otras_deducciones")));
            dsNominaDeducciones.put("total_impuestos_retenidos", df.format((Double) dsNominaDeducciones.get("total_impuestos_retenidos")));
            for (Map<String, Object> d : deducList) {
                // formatting numbers
                d.put("importe", df.format((Double) d.get("importe")));
                // adding names/descriptions from catalogs
                d.put("tipo_deduccion_descr", tipoDeduccionCat.get((String) d.get("tipo_deduccion")));
            }

            dsNominaOtrosPagos.put("total_otros_pagos", df.format((Double) dsNominaOtrosPagos.get("total_otros_pagos")));
            for (Map<String, Object> o : otrosPagosList) {
                // formatting numbers
                o.put("importe", df.format((Double) o.get("importe")));
                o.put("subsidio_causado", df.format((Double) o.get("subsidio_causado")));
                o.put("saldo_a_favor", df.format((Double) o.get("saldo_a_favor")));
                o.put("remanente_sal_fav", df.format((Double) o.get("remanente_sal_fav")));
                o.put("año", String.valueOf(((Double) o.get("año")).intValue()));
                // adding names/descriptions from catalogs
                o.put("tipo_otro_pago_descr", tipoOtroPagoCat.get((String) o.get("tipo_otro_pago")));
            }

            // Translating importe total to Spanish
            String totalStr = df.format(totalDouble);
            String[] totalArr = totalStr.split("\\.");
            String centavoStr = " PESOS 00/100 MXN";
            if (totalArr.length > 1) {
                centavoStr = String.format(" PESOS %s/100 MXN", totalArr[1]);
            }
            ds.put("total", totalStr);
            ds.put("total_letra", NumberToSpanishTranslator.translate(totalDouble.longValue()).toUpperCase() + centavoStr);

            // QR Code generation ------------------------------------------------------------------------------
            String verificaCfdiUrl = String.format("https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?id=%s&re=%s&rr=%s",
                    ds.get("UUID"),
                    dsEmisor.get("rfc"),
                    dsReceptor.get("rfc")
            );
            var qrCodeBais = QRCode.generateByteStream(verificaCfdiUrl, 400, 400);
            ds.put("QRCODE", qrCodeBais);

            // logo selection ---------------------------------------------------------------
            var empresa = "TIR";

            if (empresa.equals("TQ")) {
                ds.put("LOGO_FILENAME", "/logo.jpg");

            } else if (empresa.equals("TIR")) {
                ds.put("LOGO_FILENAME", "/tir_logo.jpg");

            } else {
                throw new Exception("EMPRESA desconocida: " + empresa);
            }

            // Get resources dir ---------------------------------------------------------------
            String resourcesDirVarName = "RESOURCES_DIR";
            String resourcesDir = System.getenv(resourcesDirVarName);
            if (resourcesDir == null) {
                resourcesDir = "/resources";
            }

            // PDF generation (CFDI) ---------------------------------------------------------------
            ds.put(resourcesDirVarName, resourcesDir);
            var template = new File(resourcesDir + "/nomina.jrxml");
            byte[] bytes = Files.readAllBytes(template.toPath());
            var bais = new ByteArrayInputStream(bytes);
            JasperReport masterJR = JasperCompileManager.compileReport(bais);
            JRDataSource conceptosDS = new JRBeanCollectionDataSource(dsConceptos);

            template = new File(resourcesDir + "/nomina_percepciones.jrxml");
            bytes = Files.readAllBytes(template.toPath());
            bais = new ByteArrayInputStream(bytes);
            JasperReport percepSubrepJR = JasperCompileManager.compileReport(bais);
            JRDataSource percepDS = new JRBeanCollectionDataSource(percepList);
            ds.put("PercepCompiledSubreport", percepSubrepJR);
            ds.put("PercepSubreportDataSource", percepDS);

            template = new File(resourcesDir + "/nomina_deducciones.jrxml");
            bytes = Files.readAllBytes(template.toPath());
            bais = new ByteArrayInputStream(bytes);
            JasperReport deducSubrepJR = JasperCompileManager.compileReport(bais);
            JRDataSource deducDS = new JRBeanCollectionDataSource(deducList);
            ds.put("DeducCompiledSubreport", deducSubrepJR);
            ds.put("DeducSubreportDataSource", deducDS);

            template = new File(resourcesDir + "/nomina_otros_pagos.jrxml");
            bytes = Files.readAllBytes(template.toPath());
            bais = new ByteArrayInputStream(bytes);
            JasperReport otrosPagosSubrepJR = JasperCompileManager.compileReport(bais);
            JRDataSource otrosPagosDS = new JRBeanCollectionDataSource(otrosPagosList);
            ds.put("OtrosPagosCompiledSubreport", otrosPagosSubrepJR);
            ds.put("OtrosPagosSubreportDataSource", otrosPagosDS);

            JasperPrint jasperPrint = JasperFillManager.fillReport(masterJR, ds, conceptosDS);
            pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
//            JasperExportManager.exportReportToPdfFile(jasperPrint, "/home/userd/dev/lola4/DOS/cfdi/service/formats/sample5.pdf");

        } catch (JRException ex) {
            ex.printStackTrace();
            throw new Exception("An error occurred when building factura pdf (jasper report). ", ex);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("An error occurred when building factura pdf. ", ex);
        }

        return new BufferedInputStream(new ByteArrayInputStream(pdfBytes));
    }
}