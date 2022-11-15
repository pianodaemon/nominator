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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
class NominaXml {

    private final @NonNull
    NominaRequestDTO _req;

    private final StringWriter _sw;

    public NominaXml(NominaRequestDTO req) throws FormatError {

        _req = req;
        _sw = shape();
    }

    public static String render(Request req, IStamp< PacRegularRequest, PacRegularResponse> stamper, IStorage st) throws FormatError, StorageError {

        NominaXml ic = new NominaXml((NominaRequestDTO) req);

        PacRegularRequest pacReq = new PacRegularRequest(ic.toString());
        PacRegularResponse pacRes = stamper.impress(pacReq);

        return "It must be slightly implemented as it was in lola";
    }

    @Override
    public String toString() {
        return _sw.toString();
    }

    private StringWriter shape() throws FormatError {

        try {

            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();

            cfdi.setVersion(NominaRequestDTO.CFDI_VER);
            cfdi.setSerie(_req.getDocAttributes().getSerie());
            cfdi.setFolio(_req.getDocAttributes().getFolio());
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getDocAttributes().getFecha()));
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(NominaRequestDTO.TIPO_COMPROBANTE));
            cfdi.setMoneda(CMoneda.fromValue(_req.getDocAttributes().getMoneda()));
            cfdi.setDescuento(_req.getDocAttributes().getDescuento());
            cfdi.setSubTotal(_req.getDocAttributes().getSubtotal());
            cfdi.setTotal(_req.getDocAttributes().getTotal());
            cfdi.setExportacion(_req.getDocAttributes().getExportacion());
            cfdi.setMetodoPago(CMetodoPago.fromValue(_req.getDocAttributes().getMetodoPago()));
            cfdi.setLugarExpedicion(_req.getDocAttributes().getLugarExpedicion());
            // var montesToolbox = new MontesToolbox();
            // cfdi.setCertificado(montesToolbox.renderCerticate(null));
            // cfdi.setNoCertificado((String) ds.get("numero_certificado"));

            // Emisor
            {
                Emisor emisor = cfdiFactory.createComprobanteEmisor();
                emisor.setRfc(_req.getPseudoEmisor().getRfc());
                emisor.setNombre(_req.getPseudoEmisor().getNombre());
                emisor.setRegimenFiscal(_req.getPseudoEmisor().getRegimenFiscal());
                cfdi.setEmisor(emisor);
            }

            // Receptor
            {
                Receptor receptor = cfdiFactory.createComprobanteReceptor();
                receptor.setRfc(_req.getPseudoReceptor().getRfc());
                receptor.setNombre(_req.getPseudoReceptor().getNombre());
                receptor.setDomicilioFiscalReceptor(_req.getPseudoReceptor().getDomicilioFiscal());
                receptor.setRegimenFiscalReceptor(_req.getPseudoReceptor().getRegimenFiscal());
                receptor.setUsoCFDI(CUsoCFDI.fromValue((_req.getPseudoReceptor().getProposito())));
                cfdi.setReceptor(receptor);
            }

            // Conceptos
            {
                Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

                _req.getPseudoConceptos().stream().map(psc -> {

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

            // Complemento:Nomina
            var nominaFactory = new mx.gob.sat.nomina12.ObjectFactory();
            Nomina nomina = nominaFactory.createNomina();
            nomina.setVersion(NominaRequestDTO.NOMINA_VER);
            nomina.setTipoNomina(CTipoNomina.fromValue(_req.getNomAttributes().getTipoNomina()));
            nomina.setFechaPago(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getNomAttributes().getFechaPago()));
            nomina.setFechaInicialPago(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getNomAttributes().getFechaInicialPago()));
            nomina.setFechaFinalPago(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getNomAttributes().getFechaFinalPago()));
            nomina.setNumDiasPagados(_req.getNomAttributes().getDiasPagados());
            nomina.setTotalPercepciones(_req.getNomAttributes().getTotalPercepciones());
            nomina.setTotalDeducciones(_req.getNomAttributes().getTotalDeducciones());

            // Complemento:Nomina:Emisor 
            {
                Nomina.Emisor nomEmisor = nominaFactory.createNominaEmisor();
                nomEmisor.setRegistroPatronal(_req.getNomEmisorAttribs().getRegistroPatronal());
                nomina.setEmisor(nomEmisor);
            }

            // Complemento:Nomina:Receptor
            {
                Nomina.Receptor nomReceptor = nominaFactory.createNominaReceptor();
                nomReceptor.setCurp(_req.getNomReceptorAttribs().getCurp());
                nomReceptor.setNumSeguridadSocial(_req.getNomReceptorAttribs().getNumSeguridadSocial());
                nomReceptor.setFechaInicioRelLaboral(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getNomReceptorAttribs().getFechaInicioRelLaboral()));
                nomReceptor.setAntigüedad(_req.getNomReceptorAttribs().getAntiguedad());
                nomReceptor.setTipoContrato(_req.getNomReceptorAttribs().getTipoContrato());
                nomReceptor.setTipoRegimen(_req.getNomReceptorAttribs().getTipoRegimen());
                nomReceptor.setNumEmpleado(_req.getNomReceptorAttribs().getNumEmpleado());
                nomReceptor.setRiesgoPuesto(_req.getNomReceptorAttribs().getRiesgoPuesto());
                nomReceptor.setPeriodicidadPago(_req.getNomReceptorAttribs().getPeriodicidadPago());
                nomReceptor.setSalarioDiarioIntegrado(_req.getNomReceptorAttribs().getSalarioDiarioIntegrado());
                nomReceptor.setClaveEntFed(CEstado.fromValue(_req.getNomReceptorAttribs().getClaveEntFed()));
                nomina.setReceptor(nomReceptor);
            }

            // Complemento:Nomina:Percepciones
            {
                Percepciones percepciones = nominaFactory.createNominaPercepciones();

                percepciones.setTotalSueldos(_req.getNomPercepcionesAttribs().getTotalSueldos());
                percepciones.setTotalGravado(_req.getNomPercepcionesAttribs().getTotalGravado());
                percepciones.setTotalExento(_req.getNomPercepcionesAttribs().getTotalExento());

                _req.getNomPercepcionesAttribs().getItems().stream().map(p -> {

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
                deducciones.setTotalOtrasDeducciones(_req.getNomDeduccionesAttribs().getTotalOtrasDeducciones());
                deducciones.setTotalImpuestosRetenidos(_req.getNomDeduccionesAttribs().getTotalImpuestosRetenidos());
                _req.getNomDeduccionesAttribs().getItems().stream().map(d -> {

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
                _req.getNomOtrosPagosAttribs().getItems().stream().map(o -> {

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

            StringWriter swriter = new StringWriter();

            // Marshalling
            {
                JAXBContext context = JAXBContext.newInstance(contextPath);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty("jaxb.schemaLocation", schemaLocation);
                marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CfdiNamespaceMapper());
                marshaller.setProperty("jaxb.formatted.output", true);
                marshaller.marshal(cfdi, swriter);
            }

            return swriter;

        } catch (JAXBException | DatatypeConfigurationException ex) {
            throw new FormatError("", ex);
        }
    }
}
