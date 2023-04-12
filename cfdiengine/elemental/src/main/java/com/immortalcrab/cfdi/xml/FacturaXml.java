package com.immortalcrab.cfdi.xml;

import com.immortalcrab.cfdi.dtos.FacturaRequestDTO;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.toolbox.IToolbox;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.math.BigDecimal;

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.stream.StreamSource;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FacturaXml {

    private static final String RFC_FOREIGNER = "XEXX010101000";

    private final @NonNull
    FacturaRequestDTO req;

    private final StringWriter sw;
    private final ClassLoader cloader = getClass().getClassLoader();
    private final IToolbox toolbox = new IToolbox() {
    };

    public FacturaXml(FacturaRequestDTO req,
            BufferedInputStream certificate, BufferedInputStream signerKey, final String certificateNo) throws EngineError {

        this.req = req;
        this.sw = shape(certificateNo, certificate, signerKey);
    }

    @Override
    public String toString() {
        return sw.toString();
    }

    private StringWriter shape(String certificateNo,
            BufferedInputStream certificate, BufferedInputStream signerKey) throws EngineError {

        StringWriter swriter = new StringWriter();

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();
            cfdi.setVersion(FacturaRequestDTO.CFDI_VER);
            cfdi.setSerie(req.getComprobanteAttributes().getSerie());
            cfdi.setFolio(req.getComprobanteAttributes().getFolio());
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(req.getComprobanteAttributes().getFecha()));
            cfdi.setFormaPago(req.getComprobanteAttributes().getFormaPago());
            cfdi.setSubTotal(req.getComprobanteAttributes().getSubTotal());
            cfdi.setDescuento(req.getComprobanteAttributes().getDescuento());

            final CMoneda currency = CMoneda.fromValue(req.getComprobanteAttributes().getMoneda());
            cfdi.setMoneda(currency);
            if (currency != CMoneda.MXN) {
                cfdi.setTipoCambio(req.getComprobanteAttributes().getTipoCambio());
            }

            cfdi.setTotal(req.getComprobanteAttributes().getTotal());
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(FacturaRequestDTO.TIPO_COMPROBANTE));
            cfdi.setExportacion(req.getComprobanteAttributes().getExportacion());
            cfdi.setMetodoPago(CMetodoPago.fromValue(req.getComprobanteAttributes().getMetodoPago()));
            cfdi.setLugarExpedicion(req.getComprobanteAttributes().getLugarExpedicion());

            cfdi.setCertificado(toolbox.renderCerticate(certificate.readAllBytes()));
            cfdi.setNoCertificado(certificateNo);

            Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();
            emisor.setRfc(req.getEmisorAttributes().getRfc());
            emisor.setNombre(req.getEmisorAttributes().getNombre());
            emisor.setRegimenFiscal(req.getEmisorAttributes().getRegimenFiscal());
            cfdi.setEmisor(emisor);

            final String rfcReceptor = req.getReceptorAttributes().getRfc();
            Comprobante.Receptor receptor = cfdiFactory.createComprobanteReceptor();
            receptor.setRfc(rfcReceptor);
            receptor.setNombre(req.getReceptorAttributes().getNombre());
            receptor.setDomicilioFiscalReceptor(req.getReceptorAttributes().getDomicilioFiscalReceptor());
            if (RFC_FOREIGNER.equals(rfcReceptor)) {
                receptor.setResidenciaFiscal(CPais.fromValue(req.getReceptorAttributes().getResidenciaFiscal()));
            }
            receptor.setRegimenFiscalReceptor(req.getReceptorAttributes().getRegimenFiscalReceptor());
            receptor.setUsoCFDI(CUsoCFDI.fromValue(req.getReceptorAttributes().getUsoCfdi()));
            cfdi.setReceptor(receptor);

            // Conceptos
            Comprobante.Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

            for (FacturaRequestDTO.PseudoConcepto psc : req.getPseudoConceptos()) {

                var concepto = cfdiFactory.createComprobanteConceptosConcepto();

                concepto.setClaveProdServ(psc.getClaveProdServ());
                concepto.setNoIdentificacion(psc.getNoIdentificacion());
                concepto.setCantidad(psc.getCantidad());
                concepto.setClaveUnidad(psc.getClaveUnidad());
                concepto.setUnidad(psc.getUnidad());
                concepto.setDescripcion(psc.getDescripcion());
                concepto.setValorUnitario(psc.getValorUnitario());
                concepto.setImporte(psc.getImporte());
                concepto.setDescuento(psc.getDescuento());
                concepto.setObjetoImp(psc.getObjetoImp());

                var traslados = cfdiFactory.createComprobanteConceptosConceptoImpuestosTraslados();
                List< FacturaRequestDTO.ConceptoTrasladoAttributes> psTraslados = psc.getTraslados();
                for (FacturaRequestDTO.ConceptoTrasladoAttributes t : psTraslados) {

                    var traslado = cfdiFactory.createComprobanteConceptosConceptoImpuestosTrasladosTraslado();

                    traslado.setBase(t.getBase());
                    traslado.setImpuesto(t.getImpuesto());
                    traslado.setTipoFactor(CTipoFactor.fromValue(t.getTipoFactor()));
                    traslado.setTasaOCuota(t.getTasaOCuota().setScale(6));
                    traslado.setImporte(t.getImporte());

                    traslados.getTraslado().add(traslado);
                }

                var retenciones = cfdiFactory.createComprobanteConceptosConceptoImpuestosRetenciones();
                List< FacturaRequestDTO.ConceptoRetencionAttributes> psRetenciones = psc.getRetenciones();
                for (FacturaRequestDTO.ConceptoRetencionAttributes r : psRetenciones) {

                    var retencion = cfdiFactory.createComprobanteConceptosConceptoImpuestosRetencionesRetencion();

                    retencion.setBase(r.getBase());
                    retencion.setImpuesto(r.getImpuesto());
                    retencion.setTipoFactor(CTipoFactor.fromValue(r.getTipoFactor()));
                    retencion.setTasaOCuota(r.getTasaOCuota().setScale(6));
                    retencion.setImporte(r.getImporte());

                    retenciones.getRetencion().add(retencion);
                }

                var concImpuestos = cfdiFactory.createComprobanteConceptosConceptoImpuestos();

                if (!psTraslados.isEmpty()) {
                    concImpuestos.setTraslados(traslados);
                }
                if (!psRetenciones.isEmpty()) {
                    concImpuestos.setRetenciones(retenciones);
                }
                concepto.setImpuestos(concImpuestos);

                conceptos.getConcepto().add(concepto);
            }
            cfdi.setConceptos(conceptos);

            var impuestos = cfdiFactory.createComprobanteImpuestos();
            BigDecimal totalImpuestosRetenidos = req.getImpuestosAttributes().getTotalImpuestosRetenidos();
            if (totalImpuestosRetenidos.compareTo(new BigDecimal(0)) != 0) {
                impuestos.setTotalImpuestosRetenidos(req.getImpuestosAttributes().getTotalImpuestosRetenidos());
            }
            impuestos.setTotalImpuestosTrasladados(req.getImpuestosAttributes().getTotalImpuestosTrasladados());

            var impuestosRetenciones = cfdiFactory.createComprobanteImpuestosRetenciones();
            for (FacturaRequestDTO.ImpuestosRetencionAttributes impRet : req.getImpuestosRetenciones()) {

                var impRetencion = cfdiFactory.createComprobanteImpuestosRetencionesRetencion();

                impRetencion.setImpuesto(impRet.getImpuesto());
                impRetencion.setImporte(impRet.getImporte());

                impuestosRetenciones.getRetencion().add(impRetencion);
            }
            if (!req.getImpuestosRetenciones().isEmpty()) {
                impuestos.setRetenciones(impuestosRetenciones);
            }

            var impuestosTraslados = cfdiFactory.createComprobanteImpuestosTraslados();
            for (FacturaRequestDTO.ImpuestosTrasladoAttributes impTras : req.getImpuestosTraslados()) {

                var impTraslado = cfdiFactory.createComprobanteImpuestosTrasladosTraslado();

                impTraslado.setBase(impTras.getBase());
                impTraslado.setImpuesto(impTras.getImpuesto());
                impTraslado.setTipoFactor(CTipoFactor.fromValue(impTras.getTipoFactor()));
                impTraslado.setTasaOCuota(impTras.getTasaOCuota().setScale(6));
                impTraslado.setImporte(impTras.getImporte());

                impuestosTraslados.getTraslado().add(impTraslado);
            }
            if (!req.getImpuestosTraslados().isEmpty()) {
                impuestos.setTraslados(impuestosTraslados);
            }
            cfdi.setImpuestos(impuestos);

            // Marshalling (without issuer signature)
            Marshaller marshaller;
            String contextPath = "mx.gob.sat.cfd._4";
            String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

            JAXBContext context = JAXBContext.newInstance(contextPath);
            marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.schemaLocation", schemaLocation);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CfdiNamespaceMapper());
            marshaller.setProperty("jaxb.formatted.output", true);

            marshaller.marshal(cfdi, swriter);

            // Marshalling (including issuer signature)
            final String xmlPriorToSignature = swriter.toString();
            log.debug(String.format("This how the xml looks prior to signature -- {{ %s }}", xmlPriorToSignature));

            var pemKeyBr = new BufferedReader(new InputStreamReader(signerKey));
            var cfdiBr = new BufferedReader(new StringReader(xmlPriorToSignature));
            var xsltSource = new StreamSource(cloader.getResourceAsStream("cfdv40/cadenaoriginal_4_0.xslt"));

            String originalStr = toolbox.renderOriginal(cfdiBr, xsltSource);
            String sello = toolbox.signOriginal(pemKeyBr, originalStr);
            cfdi.setSello(sello);

            swriter = new StringWriter();
            marshaller.marshal(cfdi, swriter);

        } catch (JAXBException | DatatypeConfigurationException | IOException ex) {
            throw new EngineError("An error occurred when creating cfdi xml.", ex);
        }

        return swriter;
    }
}
