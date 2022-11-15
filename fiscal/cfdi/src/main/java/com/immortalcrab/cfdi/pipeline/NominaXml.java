package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.StorageError;
import com.immortalcrab.cfdi.error.FormatError;

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

    public static String render(Request cfdiReq, IStamp < PacRegularRequest, PacRegularResponse > stamper, IStorage st) throws FormatError, StorageError {

        NominaXml ic = new NominaXml((NominaRequestDTO) cfdiReq, st);

        StringWriter cfdi = ic.shape();
        PacRegularRequest pacReq = new PacRegularRequest(cfdi.toString());
        PacRegularResponse pacRes = stamper.impress(pacReq);

        return "It must be slightly implemented as it was in lola";
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();
        Map < String, Object > ds = cfdiReq.getDs();

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
            {
                Emisor emisor = cfdiFactory.createComprobanteEmisor();
                emisor.setRfc(cfdiReq.getPseudoEmisor().getRfc());
                emisor.setNombre(cfdiReq.getPseudoEmisor().getNombre());
                emisor.setRegimenFiscal(cfdiReq.getPseudoEmisor().getRegimenFiscal());
                cfdi.setEmisor(emisor);
            }

            // Receptor
            {
                Receptor receptor = cfdiFactory.createComprobanteReceptor();
                receptor.setRfc(cfdiReq.getPseudoReceptor().getRfc());
                receptor.setNombre(cfdiReq.getPseudoReceptor().getNombre());
                receptor.setDomicilioFiscalReceptor(cfdiReq.getPseudoReceptor().getDomicilioFiscal());
                receptor.setRegimenFiscalReceptor(cfdiReq.getPseudoReceptor().getRegimenFiscal());
                receptor.setUsoCFDI(CUsoCFDI.fromValue((cfdiReq.getPseudoReceptor().getProposito())));
                cfdi.setReceptor(receptor);
            }

            // Conceptos
            {
                Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

                cfdiReq.getPseudoConceptos().stream().map(psc -> {

                    var concepto = cfdiFactory.createComprobanteConceptosConcepto();

                    concepto.setClaveProdServ(psc.getClaveProdServ());
                    concepto.setCantidad(psc.getCantidad());
                    concepto.setClaveUnidad(psc.getClaveUnidad());
                    concepto.setDescripcion(psc.getDescripcion());
                    concepto.setValorUnitario(psc.getValorUnitario());
                    concepto.setImporte(psc.getImporte());
                    concepto.setDescuento(psc.getDescuento());
                    concepto.setObjetoImp(psc.getObjImp());

                    return concepto;
                }).forEachOrdered(concepto -> {

                    conceptos.getConcepto().add(concepto);
                });
                cfdi.setConceptos(conceptos);
            }

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

            // Complemento:Nomina:Emisor 
            {
                Nomina.Emisor nomEmisor = nominaFactory.createNominaEmisor();
                nomEmisor.setRegistroPatronal(cfdiReq.getNomEmisorAttribs().getRegistroPatronal());
                nomina.setEmisor(nomEmisor);
            }

            // Complemento:Nomina:Receptor
            {
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
            }

            // Complemento:Nomina:Percepciones
            {
                Percepciones percepciones = nominaFactory.createNominaPercepciones();

                percepciones.setTotalSueldos(cfdiReq.getNomPercepcionesAttribs().getTotalSueldos());
                percepciones.setTotalGravado(cfdiReq.getNomPercepcionesAttribs().getTotalGravado());
                percepciones.setTotalExento(cfdiReq.getNomPercepcionesAttribs().getTotalExento());

                cfdiReq.getNomPercepcionesAttribs().getItems().stream().map(p -> {

                    var percepcion = nominaFactory.createNominaPercepcionesPercepcion();

                    percepcion.setTipoPercepcion(p.getTipoPercepcion());
                    percepcion.setClave(p.getClave());
                    percepcion.setConcepto(p.getConcepto());
                    percepcion.setImporteGravado(p.getImporteGravado());
                    percepcion.setImporteExento(p.getImporteExento());

                    return percepcion;
                }).forEachOrdered(i -> {

                    percepciones.getPercepcion().add(i);
                });
                nomina.setPercepciones(percepciones);
            }

            // Complemento:Nomina:Deducciones
            {
                Deducciones deducciones = nominaFactory.createNominaDeducciones();
                deducciones.setTotalOtrasDeducciones(cfdiReq.getNomDeduccionesAttribs().getTotalOtrasDeducciones());
                deducciones.setTotalImpuestosRetenidos(cfdiReq.getNomDeduccionesAttribs().getTotalImpuestosRetenidos());
                cfdiReq.getNomDeduccionesAttribs().getItems().stream().map(d -> {

                    var deduccion = nominaFactory.createNominaDeduccionesDeduccion();
                    deduccion.setTipoDeduccion(d.getTipoDeduccion());
                    deduccion.setClave(d.getClave());
                    deduccion.setConcepto(d.getConcepto());
                    deduccion.setImporte(d.getImporte());

                    return deduccion;
                }).forEachOrdered(i -> {

                    deducciones.getDeduccion().add(i);
                });
                nomina.setDeducciones(deducciones);
            }

            // Complemento:Nomina:OtrosPagos
            {
                OtrosPagos otrosPagos = nominaFactory.createNominaOtrosPagos();
                cfdiReq.getNomOtrosPagosAttribs().getItems().stream().map(o -> {

                    var otroPago = nominaFactory.createNominaOtrosPagosOtroPago();
                    otroPago.setTipoOtroPago(o.getTipoOtroPago());
                    otroPago.setClave(o.getClave());
                    otroPago.setConcepto(o.getConcepto());
                    otroPago.setImporte(o.getImporte());

                    var subsidioAlEmpleo = nominaFactory.createNominaOtrosPagosOtroPagoSubsidioAlEmpleo();
                    subsidioAlEmpleo.setSubsidioCausado(o.getSubsidioCausado());
                    otroPago.setSubsidioAlEmpleo(subsidioAlEmpleo);

                    return otroPago;
                }).forEachOrdered(i -> {

                    otrosPagos.getOtroPago().add(i);
                });
                nomina.setOtrosPagos(otrosPagos);
            }

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
