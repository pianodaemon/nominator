package com.immortalcrab.cfdi.xml;

import com.immortalcrab.cfdi.dtos.FacturaRequestDTO;
import com.immortalcrab.cfdi.errors.EngineError;
import com.immortalcrab.cfdi.errors.ErrorCodes;

import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.StringWriter;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FacturaXml {

    private final @NonNull
    FacturaRequestDTO req;

    private final StringWriter sw;

    public FacturaXml(FacturaRequestDTO req,
            BufferedInputStream certificate, BufferedInputStream signerKey, final String certificateNo) throws EngineError {

        this.req = req;
        this.sw = shape();
    }

    @Override
    public String toString() {
        return sw.toString();
    }

    private StringWriter shape() throws EngineError {

        StringWriter writer = new StringWriter();

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();
            cfdi.setVersion(FacturaRequestDTO.CFDI_VER);
            cfdi.setSerie(req.getComprobanteAttributes().getSerie());
            cfdi.setFolio(req.getComprobanteAttributes().getFolio());
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(req.getComprobanteAttributes().getFecha()));
            cfdi.setFormaPago(req.getComprobanteAttributes().getFormaPago());
            cfdi.setNoCertificado(req.getComprobanteAttributes().getNoCertificado());
            cfdi.setSubTotal(req.getComprobanteAttributes().getSubTotal());
            cfdi.setDescuento(req.getComprobanteAttributes().getDescuento());
            cfdi.setMoneda(CMoneda.fromValue(req.getComprobanteAttributes().getMoneda()));
            cfdi.setTipoCambio(req.getComprobanteAttributes().getTipoCambio());
            cfdi.setTotal(req.getComprobanteAttributes().getTotal());
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(FacturaRequestDTO.TIPO_COMPROBANTE));
            cfdi.setExportacion(req.getComprobanteAttributes().getExportacion());
            cfdi.setMetodoPago(CMetodoPago.fromValue(req.getComprobanteAttributes().getMetodoPago()));
            cfdi.setLugarExpedicion(req.getComprobanteAttributes().getLugarExpedicion());

            Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();
            emisor.setRfc(req.getEmisorAttributes().getRfc());
            emisor.setNombre(req.getEmisorAttributes().getNombre());
            emisor.setRegimenFiscal(req.getEmisorAttributes().getRegimenFiscal());
            cfdi.setEmisor(emisor);

            Comprobante.Receptor receptor = cfdiFactory.createComprobanteReceptor();
            receptor.setRfc(req.getReceptorAttributes().getRfc());
            receptor.setNombre(req.getReceptorAttributes().getNombre());
            receptor.setDomicilioFiscalReceptor(req.getReceptorAttributes().getDomicilioFiscalReceptor());
            receptor.setResidenciaFiscal(CPais.fromValue(req.getReceptorAttributes().getResidenciaFiscal()));
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
                    traslado.setTasaOCuota(t.getTasaOCuota());
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
                    retencion.setTasaOCuota(r.getTasaOCuota());
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
            impuestos.setTotalImpuestosRetenidos(req.getImpuestosAttributes().getTotalImpuestosRetenidos());
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
                impTraslado.setTasaOCuota(impTras.getTasaOCuota());
                impTraslado.setImporte(impTras.getImporte());

                impuestosTraslados.getTraslado().add(impTraslado);
            }
            if (!req.getImpuestosTraslados().isEmpty()) {
                impuestos.setTraslados(impuestosTraslados);
            }
            cfdi.setImpuestos(impuestos);

            String contextPath = "mx.gob.sat.cfd._4";
            String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

            // Hacer el marshalling del cfdi object
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.schemaLocation", schemaLocation);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new CfdiNamespaceMapper());
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(cfdi, writer);

        } catch (JAXBException | DatatypeConfigurationException ex) {
            throw new EngineError("Impossible to turn the request into the xml",
                    ex, ErrorCodes.FORMAT_BUILDER_ISSUE);
        }

        return writer;
    }
}
