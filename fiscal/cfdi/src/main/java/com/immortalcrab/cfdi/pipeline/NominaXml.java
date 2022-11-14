package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.pipeline.NominaRequestDTO.PseudoConcepto;

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
class NominaXml {

    private final @NonNull NominaRequestDTO cfdiReq;

    private final @NonNull IStorage st;

    public static String render(Request cfdiReq, IStamp<PacRegularRequest, PacRegularResponse> stamper, IStorage st) throws FormatError, StorageError {

        NominaXml ic = new NominaXml((NominaRequestDTO) cfdiReq, st);

        StringWriter cfdi = ic.shape();
        PacRegularRequest pacReq = new PacRegularRequest(cfdi.toString());
        PacRegularResponse pacRes = stamper.impress(pacReq);

        return "It must be slightly implemented as it was in lola";
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();
        Map<String, Object> ds = cfdiReq.getDs();

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();
            cfdi.setVersion(NominaRequestDTO.CFDI_VER);
            cfdi.setSerie(cfdiReq.getDocAttributes().getSerie());
            cfdi.setFolio(cfdiReq.getDocAttributes().getFolio());
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(cfdiReq.getDocAttributes().getFecha()));
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(NominaRequestDTO.TIPO_COMPROBANTE));
            cfdi.setMoneda(CMoneda.fromValue(cfdiReq.getDocAttributes().getMoneda()));
            cfdi.setDescuento(cfdiReq.getDocAttributes().getDescuento());
            cfdi.setSubTotal(cfdiReq.getDocAttributes().getSubtotal());
            cfdi.setTotal(cfdiReq.getDocAttributes().getTotal());
            cfdi.setExportacion(cfdiReq.getDocAttributes().getExportacion());
            cfdi.setMetodoPago(CMetodoPago.fromValue(cfdiReq.getDocAttributes().getMetodoPago()));
            cfdi.setLugarExpedicion(cfdiReq.getDocAttributes().getLugarExpedicion());
            // var montesToolbox = new MontesToolbox();
            // cfdi.setCertificado(montesToolbox.renderCerticate(null));
            // cfdi.setNoCertificado((String) ds.get("numero_certificado"));

            // Emisor
            Emisor emisor = cfdiFactory.createComprobanteEmisor();
            emisor.setRfc(cfdiReq.getPseudoEmisor().getRfc());
            emisor.setNombre(cfdiReq.getPseudoEmisor().getNombre());
            emisor.setRegimenFiscal(cfdiReq.getPseudoEmisor().getRegimenFiscal());
            cfdi.setEmisor(emisor);

            // Receptor
            Receptor receptor = cfdiFactory.createComprobanteReceptor();
            receptor.setRfc(cfdiReq.getPseudoReceptor().getRfc());
            receptor.setNombre(cfdiReq.getPseudoReceptor().getNombre());
            receptor.setDomicilioFiscalReceptor(cfdiReq.getPseudoReceptor().getDomicilioFiscal());
            receptor.setRegimenFiscalReceptor(cfdiReq.getPseudoReceptor().getRegimenFiscal());
            receptor.setUsoCFDI(CUsoCFDI.fromValue((cfdiReq.getPseudoReceptor().getProposito())));
            cfdi.setReceptor(receptor);

            // Conceptos
            Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

            for (PseudoConcepto psc : cfdiReq.getPseudoConceptos()) {

                var concepto = cfdiFactory.createComprobanteConceptosConcepto();

                concepto.setClaveProdServ(psc.getClaveProdServ());
                concepto.setCantidad(psc.getCantidad());
                concepto.setClaveUnidad(psc.getClaveUnidad());
                concepto.setDescripcion(psc.getDescripcion());
                concepto.setValorUnitario(psc.getValorUnitario());
                concepto.setImporte(psc.getImporte());
                concepto.setDescuento(psc.getDescuento());
                concepto.setObjetoImp(psc.getObjImp());
                conceptos.getConcepto().add(concepto);
            }
            cfdi.setConceptos(conceptos);

            String contextPath = "mx.gob.sat.cfd._4";
            String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

            // Complemento:Nomina ------------------------------------
            var nominaFactory = new mx.gob.sat.nomina12.ObjectFactory();
            Nomina nomina = nominaFactory.createNomina();
            nomina.setVersion(NominaRequestDTO.NOMINA_VER);
            nomina.setTipoNomina(CTipoNomina.fromValue(cfdiReq.getNomAttributes().getTipoNomina()));
            nomina.setFechaPago(DatatypeFactory.newInstance().newXMLGregorianCalendar(cfdiReq.getNomAttributes().getFechaPago()));
            nomina.setFechaInicialPago(DatatypeFactory.newInstance().newXMLGregorianCalendar(cfdiReq.getNomAttributes().getFechaInicialPago()));
            nomina.setFechaFinalPago(DatatypeFactory.newInstance().newXMLGregorianCalendar(cfdiReq.getNomAttributes().getFechaFinalPago()));
            nomina.setNumDiasPagados(cfdiReq.getNomAttributes().getDiasPagados());
            nomina.setTotalPercepciones(cfdiReq.getNomAttributes().getTotalPercepciones());
            nomina.setTotalDeducciones(cfdiReq.getNomAttributes().getTotalDeducciones());
            // Complemento:Nomina:Emisor ------------------------------------

            Nomina.Emisor nomEmisor = nominaFactory.createNominaEmisor();
            nomEmisor.setRegistroPatronal(cfdiReq.getNomEmisorAttribs().getRegistroPatronal());
            nomina.setEmisor(nomEmisor);

            // Complemento:Nomina:Receptor ------------------------------------
            var dsNomina = (Map<String, Object>) ds.get("nomina");
            var dsNomReceptor = (Map<String, Object>) dsNomina.get("receptor");
            Nomina.Receptor nomReceptor = nominaFactory.createNominaReceptor();
            nomReceptor.setCurp(cfdiReq.getNomReceptorAttribs().getCurp());
            nomReceptor.setNumSeguridadSocial(cfdiReq.getNomReceptorAttribs().getNumSeguridadSocial());
            nomReceptor.setFechaInicioRelLaboral(DatatypeFactory.newInstance().newXMLGregorianCalendar(cfdiReq.getNomReceptorAttribs().getFechaInicioRelLaboral()));
            nomReceptor.setAntigüedad(cfdiReq.getNomReceptorAttribs().getAntiguedad());
            nomReceptor.setTipoContrato(cfdiReq.getNomReceptorAttribs().getTipoContrato());
            nomReceptor.setTipoRegimen(cfdiReq.getNomReceptorAttribs().getTipoRegimen());
            nomReceptor.setNumEmpleado(cfdiReq.getNomReceptorAttribs().getNumEmpleado());
            nomReceptor.setRiesgoPuesto(cfdiReq.getNomReceptorAttribs().getRiesgoPuesto());
            nomReceptor.setPeriodicidadPago(cfdiReq.getNomReceptorAttribs().getPeriodicidadPago());
            nomReceptor.setSalarioDiarioIntegrado(cfdiReq.getNomReceptorAttribs().getSalarioDiarioIntegrado());
            nomReceptor.setClaveEntFed(CEstado.fromValue(cfdiReq.getNomReceptorAttribs().getClaveEntFed()));
            nomina.setReceptor(nomReceptor);

            // Complemento:Nomina:Percepciones ------------------------------------
            var dsNomPercepciones = (Map<String, Object>) dsNomina.get("percepciones");
            Percepciones percepciones = nominaFactory.createNominaPercepciones();
            percepciones.setTotalSueldos(new BigDecimal(((Double) dsNomPercepciones.get("total_sueldos")).toString()));
            percepciones.setTotalGravado(new BigDecimal(((Double) dsNomPercepciones.get("total_gravado")).toString()));
            percepciones.setTotalExento(new BigDecimal(((Double) dsNomPercepciones.get("total_exento")).toString()));

            var listaPercepciones = (List<Map<String, Object>>) dsNomPercepciones.get("lista");
            for (Map<String, Object> p : listaPercepciones) {

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
            var dsNomDeducciones = (Map<String, Object>) dsNomina.get("deducciones");
            Deducciones deducciones = nominaFactory.createNominaDeducciones();
            deducciones.setTotalOtrasDeducciones(new BigDecimal(((Double) dsNomDeducciones.get("total_otras_deducciones")).toString()));
            deducciones.setTotalImpuestosRetenidos(new BigDecimal(((Double) dsNomDeducciones.get("total_impuestos_retenidos")).toString()));

            var listaDeducciones = (List<Map<String, Object>>) dsNomDeducciones.get("lista");
            for (Map<String, Object> d : listaDeducciones) {

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
            var listaOtrosPagos = (List<Map<String, Object>>) dsNomina.get("otros_pagos");
            for (Map<String, Object> o : listaOtrosPagos) {

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

            contextPath += ":mx.gob.sat.nomina12";
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
