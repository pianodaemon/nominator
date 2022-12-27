package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;

import java.io.*;

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

import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
class NominaXml {

    private final @NonNull NominaRequestDTO _req;
    private StringWriter _sw;
    private final Comprobante _cfdi;
    private final Marshaller _marshaller;

    public NominaXml(NominaRequestDTO req, BufferedInputStream certificate, final String certificateNo) throws FormatError {

        _req = req;
        _cfdi = new ObjectFactory().createComprobante();

        String contextPath = "mx.gob.sat.cfd._4";
        String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";
        contextPath += ":mx.gob.sat.nomina12";
        schemaLocation += " http://www.sat.gob.mx/nomina12 http://www.sat.gob.mx/sitio_internet/cfd/nomina/nomina12.xsd";

        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            _marshaller = context.createMarshaller();
            _marshaller.setProperty("jaxb.schemaLocation", schemaLocation);
            _marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CfdiNamespaceMapper());
            _marshaller.setProperty("jaxb.formatted.output", true);

        } catch (JAXBException ex) {
            throw new FormatError("", ex);
        }

        _sw = shape(certificateNo, certificate);
    }

    @Override
    public String toString() {
        return _sw.toString();
    }

    public void setSello(String sello) throws FormatError {

        _cfdi.setSello(sello);
        _sw = new StringWriter();

        try {
            _marshaller.marshal(_cfdi, _sw);

        } catch (JAXBException ex) {
            throw new FormatError("", ex);
        }
    }

    private StringWriter shape(String certificateNo, BufferedInputStream certificate) throws FormatError {

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();

            _cfdi.setVersion(NominaRequestDTO.CFDI_VER);
            _cfdi.setSerie(_req.getDocAttributes().getSerie());
            _cfdi.setFolio(_req.getDocAttributes().getFolio());
            _cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getDocAttributes().getFecha()));
            _cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(NominaRequestDTO.TIPO_COMPROBANTE));
            _cfdi.setMoneda(CMoneda.fromValue(_req.getDocAttributes().getMoneda()));
            _cfdi.setDescuento(_req.getDocAttributes().getDescuento());
            _cfdi.setSubTotal(_req.getDocAttributes().getSubtotal());
            _cfdi.setTotal(_req.getDocAttributes().getTotal());
            _cfdi.setExportacion(_req.getDocAttributes().getExportacion());
            _cfdi.setMetodoPago(CMetodoPago.fromValue(_req.getDocAttributes().getMetodoPago()));
            _cfdi.setLugarExpedicion(_req.getDocAttributes().getLugarExpedicion());

            byte[] contents = new byte[1024];
            int bytesRead = 0;
            StringBuilder certContents = new StringBuilder();

            while ((bytesRead = certificate.read(contents)) != -1) {
                certContents.append(new String(contents, 0, bytesRead));
            }
            _cfdi.setCertificado(certContents.toString());
            _cfdi.setNoCertificado(certificateNo);

            // Emisor
            {
                Emisor emisor = cfdiFactory.createComprobanteEmisor();
                emisor.setRfc(_req.getPseudoEmisor().getRfc());
                emisor.setNombre(_req.getPseudoEmisor().getNombre());
                emisor.setRegimenFiscal(_req.getPseudoEmisor().getRegimenFiscal());
                _cfdi.setEmisor(emisor);
            }

            // Receptor
            {
                Receptor receptor = cfdiFactory.createComprobanteReceptor();
                receptor.setRfc(_req.getPseudoReceptor().getRfc());
                receptor.setNombre(_req.getPseudoReceptor().getNombre());
                receptor.setDomicilioFiscalReceptor(_req.getPseudoReceptor().getDomicilioFiscal());
                receptor.setRegimenFiscalReceptor(_req.getPseudoReceptor().getRegimenFiscal());
                receptor.setUsoCFDI(CUsoCFDI.fromValue((_req.getPseudoReceptor().getProposito())));
                _cfdi.setReceptor(receptor);
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
                _cfdi.setConceptos(conceptos);
            }

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
            _cfdi.setComplemento(complemento);

            // Marshalling
            StringWriter swriter = new StringWriter();
            _marshaller.marshal(_cfdi, swriter);

            return swriter;

        } catch (JAXBException | DatatypeConfigurationException | IOException ex) {
            throw new FormatError("", ex);
        }
    }
}
