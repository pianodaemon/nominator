package com.immortalcrab.cfdi.pipeline.lola;

import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.Request;
import com.immortalcrab.cfdi.pipeline.IStorage;

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.cfd._4.Comprobante.Complemento;
import mx.gob.sat.cfd._4.Comprobante.Conceptos;
import mx.gob.sat.cfd._4.Comprobante.Emisor;
import mx.gob.sat.cfd._4.Comprobante.Receptor;
import mx.gob.sat.sitio_internet.cfd.catalogos.CEstado;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMetodoPago;
import mx.gob.sat.sitio_internet.cfd.catalogos.CMoneda;
import mx.gob.sat.sitio_internet.cfd.catalogos.CTipoDeComprobante;
import mx.gob.sat.sitio_internet.cfd.catalogos.CUsoCFDI;
import mx.gob.sat.sitio_internet.cfd.catalogos.nomina.CTipoNomina;
import mx.gob.sat.nomina12.Nomina;
import mx.gob.sat.nomina12.Nomina.Deducciones;
import mx.gob.sat.nomina12.Nomina.OtrosPagos;
import mx.gob.sat.nomina12.Nomina.Percepciones;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class NominaXml {

    private final @NonNull Request cfdiReq;
    private final @NonNull IStorage st;

    public static String render(Request cfdiReq, IStorage st) throws FormatError, StorageError {

        NominaXml ic = new NominaXml(cfdiReq, st);
        StringWriter cfdi = ic.shape();

        return "It must be slightly implemented as it was in lola";
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();
        Map<String,Object> ds = this.cfdiReq.getDs();

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();
            cfdi.setVersion("4.0");
            cfdi.setSerie((String) ds.get("serie"));
            cfdi.setFolio((String) ds.get("folio"));
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar((String) ds.get("fecha")));
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue((String) ds.get("tipo_de_comprobante")));
            cfdi.setMoneda(CMoneda.fromValue((String) ds.get("moneda")));
            cfdi.setDescuento(new BigDecimal(((Double) ds.get("descuento")).toString()));
            cfdi.setSubTotal(new BigDecimal(((Double) ds.get("subtotal")).toString()));
            cfdi.setTotal(new BigDecimal(((Double) ds.get("total")).toString()));
            cfdi.setExportacion((String) ds.get("exportacion"));
            cfdi.setMetodoPago(CMetodoPago.fromValue((String) ds.get("metodo_pago")));
            cfdi.setLugarExpedicion((String) ds.get("lugar_expedicion"));
            // var montesToolbox = new MontesToolbox();
            // cfdi.setCertificado(montesToolbox.renderCerticate(null));
            // cfdi.setNoCertificado((String) ds.get("numero_certificado"));

            // Emisor
            Emisor emisor = cfdiFactory.createComprobanteEmisor();
            var dsEmisor = (Map<String,String>) ds.get("emisor");
            emisor.setRfc(dsEmisor.get("rfc"));
            emisor.setNombre(dsEmisor.get("nombre"));
            emisor.setRegimenFiscal(dsEmisor.get("regimen_fiscal"));
            cfdi.setEmisor(emisor);

            // Receptor
            Receptor receptor = cfdiFactory.createComprobanteReceptor();
            var dsReceptor = (Map<String,String>) ds.get("receptor");
            receptor.setRfc(dsReceptor.get("rfc"));
            receptor.setNombre(dsReceptor.get("nombre"));
            receptor.setDomicilioFiscalReceptor(dsReceptor.get("domicilio_fiscal_receptor"));
            receptor.setRegimenFiscalReceptor(dsReceptor.get("regimen_fiscal_receptor"));
            receptor.setUsoCFDI(CUsoCFDI.fromValue((dsReceptor.get("uso_cfdi"))));
            cfdi.setReceptor(receptor);

            // Conceptos
            Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

            for (var c : (List<Map<String,Object>>) ds.get("conceptos")) {

                var concepto = cfdiFactory.createComprobanteConceptosConcepto();
                concepto.setClaveProdServ((String) c.get("clave_prod_serv"));
                concepto.setCantidad(new BigDecimal(((Double) c.get("cantidad")).intValue()));
                concepto.setClaveUnidad((String) c.get("clave_unidad"));
                concepto.setDescripcion((String) c.get("descripcion"));
                concepto.setValorUnitario(new BigDecimal(((Double) c.get("valor_unitario")).toString()));
                concepto.setImporte(new BigDecimal(((Double) c.get("importe")).toString()));
                concepto.setDescuento(new BigDecimal(((Double) c.get("descuento")).toString()));
                concepto.setObjetoImp((String) c.get("objeto_imp"));
                conceptos.getConcepto().add(concepto);
            }
            cfdi.setConceptos(conceptos);

            String contextPath    = "mx.gob.sat.cfd._4";
            String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

            // Complemento:Nomina ------------------------------------

            var dsNomina = (Map<String,Object>) ds.get("nomina");
            var nominaFactory = new mx.gob.sat.nomina12.ObjectFactory();
            Nomina nomina = nominaFactory.createNomina();
            nomina.setVersion((String) dsNomina.get("version"));
            nomina.setTipoNomina(CTipoNomina.fromValue((String) dsNomina.get("tipo_nomina")));
            nomina.setFechaPago(DatatypeFactory.newInstance().newXMLGregorianCalendar((String) dsNomina.get("fecha_pago")));
            nomina.setFechaInicialPago(DatatypeFactory.newInstance().newXMLGregorianCalendar((String) dsNomina.get("fecha_inicial_pago")));
            nomina.setFechaFinalPago(DatatypeFactory.newInstance().newXMLGregorianCalendar((String) dsNomina.get("fecha_final_pago")));
            nomina.setNumDiasPagados(new BigDecimal(((Double) dsNomina.get("num_dias_pagados")).intValue()));
            nomina.setTotalPercepciones(new BigDecimal(((Double) dsNomina.get("total_percepciones")).toString()));
            nomina.setTotalDeducciones(new BigDecimal(((Double) dsNomina.get("total_deducciones")).toString()));

            // Complemento:Nomina:Emisor ------------------------------------

            var dsNomEmisor = (Map<String,String>) dsNomina.get("emisor");
            Nomina.Emisor nomEmisor = nominaFactory.createNominaEmisor();
            nomEmisor.setRegistroPatronal(dsNomEmisor.get("registro_patronal"));
            nomina.setEmisor(nomEmisor);

            // Complemento:Nomina:Receptor ------------------------------------

            var dsNomReceptor = (Map<String,Object>) dsNomina.get("receptor");
            Nomina.Receptor nomReceptor = nominaFactory.createNominaReceptor();
            nomReceptor.setCurp((String) dsNomReceptor.get("curp"));
            nomReceptor.setNumSeguridadSocial((String) dsNomReceptor.get("num_seguridad_social"));
            nomReceptor.setFechaInicioRelLaboral(DatatypeFactory.newInstance().newXMLGregorianCalendar((String) dsNomReceptor.get("fecha_inicio_rel_laboral")));
            nomReceptor.setAntigüedad((String) dsNomReceptor.get("antigüedad"));
            nomReceptor.setTipoContrato((String) dsNomReceptor.get("tipo_contrato"));
            nomReceptor.setTipoRegimen((String) dsNomReceptor.get("tipo_regimen"));
            nomReceptor.setNumEmpleado((String) dsNomReceptor.get("num_empleado"));
            nomReceptor.setRiesgoPuesto((String) dsNomReceptor.get("riesgo_puesto"));
            nomReceptor.setPeriodicidadPago((String) dsNomReceptor.get("periodicidad_pago"));
            nomReceptor.setSalarioDiarioIntegrado(new BigDecimal(((Double) dsNomReceptor.get("salario_diario_integrado")).toString()));
            nomReceptor.setClaveEntFed(CEstado.fromValue((String) dsNomReceptor.get("clave_ent_fed")));
            nomina.setReceptor(nomReceptor);

            // Complemento:Nomina:Percepciones ------------------------------------

            var dsNomPercepciones = (Map<String,Object>) dsNomina.get("percepciones");
            Percepciones percepciones = nominaFactory.createNominaPercepciones();
            percepciones.setTotalSueldos(new BigDecimal(((Double) dsNomPercepciones.get("total_sueldos")).toString()));
            percepciones.setTotalGravado(new BigDecimal(((Double) dsNomPercepciones.get("total_gravado")).toString()));
            percepciones.setTotalExento(new BigDecimal(((Double) dsNomPercepciones.get("total_exento")).toString()));

            var listaPercepciones = (List<Map<String,Object>>) dsNomPercepciones.get("lista");
            for (Map<String,Object> p : listaPercepciones) {

                var percepcion = nominaFactory.createNominaPercepcionesPercepcion();
                percepcion.setTipoPercepcion((String) p.get("tipo_percepcion"));
                percepcion.setClave((String) p.get("clave"));
                percepcion.setConcepto((String) p.get("concepto"));
                percepcion.setImporteGravado(new BigDecimal(((Double) p.get("importe_gravado")).toString()));
                percepcion.setImporteExento(new BigDecimal(((Double) p.get("importe_exento")).toString()));
                percepciones.getPercepcion().add(percepcion);
            }
            nomina.setPercepciones(percepciones);

            // Complemento:Nomina:Deducciones ------------------------------------

            var dsNomDeducciones = (Map<String,Object>) dsNomina.get("deducciones");
            Deducciones deducciones = nominaFactory.createNominaDeducciones();
            deducciones.setTotalOtrasDeducciones(new BigDecimal(((Double) dsNomDeducciones.get("total_otras_deducciones")).toString()));
            deducciones.setTotalImpuestosRetenidos(new BigDecimal(((Double) dsNomDeducciones.get("total_impuestos_retenidos")).toString()));

            var listaDeducciones = (List<Map<String,Object>>) dsNomDeducciones.get("lista");
            for (Map<String,Object> d : listaDeducciones) {

                var deduccion = nominaFactory.createNominaDeduccionesDeduccion();
                deduccion.setTipoDeduccion((String) d.get("tipo_deduccion"));
                deduccion.setClave((String) d.get("clave"));
                deduccion.setConcepto((String) d.get("concepto"));
                deduccion.setImporte(new BigDecimal(((Double) d.get("importe")).toString()));
                deducciones.getDeduccion().add(deduccion);
            }
            nomina.setDeducciones(deducciones);

            // Complemento:Nomina:OtrosPagos ------------------------------------

            OtrosPagos otrosPagos = nominaFactory.createNominaOtrosPagos();
            var listaOtrosPagos = (List<Map<String,Object>>) dsNomina.get("otros_pagos");
            for (Map<String,Object> o : listaOtrosPagos) {

                var otroPago = nominaFactory.createNominaOtrosPagosOtroPago();
                otroPago.setTipoOtroPago((String) o.get("tipo_otro_pago"));
                otroPago.setClave((String) o.get("clave"));
                otroPago.setConcepto((String) o.get("concepto"));
                otroPago.setImporte(new BigDecimal(((Double) o.get("importe")).toString()));

                var subsidioAlEmpleo = nominaFactory.createNominaOtrosPagosOtroPagoSubsidioAlEmpleo();
                subsidioAlEmpleo.setSubsidioCausado(new BigDecimal(((Double) o.get("subsidio_causado")).toString()));
                otroPago.setSubsidioAlEmpleo(subsidioAlEmpleo);

                otrosPagos.getOtroPago().add(otroPago);
            }
            nomina.setOtrosPagos(otrosPagos);

            Complemento complemento = cfdiFactory.createComprobanteComplemento();
            complemento.getAny().add(nomina);
            cfdi.setComplemento(complemento);

            contextPath    += ":mx.gob.sat.nomina12";
            schemaLocation += " http://www.sat.gob.mx/nomina12 http://www.sat.gob.mx/sitio_internet/cfd/nomina/nomina12.xsd";

            JAXBContext context = JAXBContext.newInstance(contextPath);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.schemaLocation", schemaLocation);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CfdiNamespaceMapper());
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(cfdi, sw);
            System.out.println(sw.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sw;
    }
}
