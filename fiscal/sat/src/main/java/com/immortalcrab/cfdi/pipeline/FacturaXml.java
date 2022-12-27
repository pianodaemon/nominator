package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import java.io.BufferedInputStream;

import com.immortalcrab.cfdi.pipeline.FacturaRequestDTO;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
class FacturaXml {

    private final @NonNull FacturaRequestDTO _req;
    private StringWriter _sw;
    private final Comprobante _cfdi;
    private final Marshaller _marshaller;

    public FacturaXml(FacturaRequestDTO req, BufferedInputStream certificate, final String certificateNo) throws FormatError {

        _req = req;
        _cfdi = new ObjectFactory().createComprobante();

        String contextPath = "mx.gob.sat.cfd._4";
        String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

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

            _cfdi.setVersion(FacturaRequestDTO.CFDI_VER);
            _cfdi.setSerie(_req.getComprobanteAttributes().getSerie());
            _cfdi.setFolio(_req.getComprobanteAttributes().getFolio());
            _cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getComprobanteAttributes().getFecha()));
            _cfdi.setFormaPago(_req.getComprobanteAttributes().getFormaPago());
            _cfdi.setNoCertificado(_req.getComprobanteAttributes().getNoCertificado());
            _cfdi.setSubTotal(_req.getComprobanteAttributes().getSubTotal());
            _cfdi.setDescuento(_req.getComprobanteAttributes().getDescuento());
            _cfdi.setMoneda(CMoneda.fromValue(_req.getComprobanteAttributes().getMoneda()));
            _cfdi.setTipoCambio(_req.getComprobanteAttributes().getTipoCambio());
            _cfdi.setTotal(_req.getComprobanteAttributes().getTotal());
            _cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(FacturaRequestDTO.TIPO_COMPROBANTE));
            _cfdi.setExportacion(_req.getComprobanteAttributes().getExportacion());
            _cfdi.setMetodoPago(CMetodoPago.fromValue(_req.getComprobanteAttributes().getMetodoPago()));
            _cfdi.setLugarExpedicion(_req.getComprobanteAttributes().getLugarExpedicion());

            byte[] contents = new byte[1024];
            int bytesRead = 0;
            StringBuilder certContents = new StringBuilder();

            while ((bytesRead = certificate.read(contents)) != -1) {
                certContents.append(new String(contents, 0, bytesRead));
            }
            _cfdi.setCertificado(certContents.toString());
            _cfdi.setNoCertificado(certificateNo);

            Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();
            emisor.setRfc(_req.getEmisorAttributes().getRfc());
            emisor.setNombre(_req.getEmisorAttributes().getNombre());
            emisor.setRegimenFiscal(_req.getEmisorAttributes().getRegimenFiscal());
            _cfdi.setEmisor(emisor);

            Comprobante.Receptor receptor = cfdiFactory.createComprobanteReceptor();
            receptor.setRfc(_req.getReceptorAttributes().getRfc());
            receptor.setNombre(_req.getReceptorAttributes().getNombre());
            receptor.setDomicilioFiscalReceptor(_req.getReceptorAttributes().getDomicilioFiscalReceptor());
            receptor.setResidenciaFiscal(CPais.fromValue(_req.getReceptorAttributes().getResidenciaFiscal()));
            receptor.setRegimenFiscalReceptor(_req.getReceptorAttributes().getRegimenFiscalReceptor());
            receptor.setUsoCFDI(CUsoCFDI.fromValue(_req.getReceptorAttributes().getUsoCfdi()));
            _cfdi.setReceptor(receptor);

            // Conceptos
            Comprobante.Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

            for (FacturaRequestDTO.PseudoConcepto psc : _req.getPseudoConceptos()) {

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
            _cfdi.setConceptos(conceptos);

            var impuestos = cfdiFactory.createComprobanteImpuestos();
            impuestos.setTotalImpuestosRetenidos(_req.getImpuestosAttributes().getTotalImpuestosRetenidos());
            impuestos.setTotalImpuestosTrasladados(_req.getImpuestosAttributes().getTotalImpuestosTrasladados());

            var impuestosRetenciones = cfdiFactory.createComprobanteImpuestosRetenciones();
            for (FacturaRequestDTO.ImpuestosRetencionAttributes impRet : _req.getImpuestosRetenciones()) {

                var impRetencion = cfdiFactory.createComprobanteImpuestosRetencionesRetencion();

                impRetencion.setImpuesto(impRet.getImpuesto());
                impRetencion.setImporte(impRet.getImporte());

                impuestosRetenciones.getRetencion().add(impRetencion);
            }
            if (!_req.getImpuestosRetenciones().isEmpty()) {
                impuestos.setRetenciones(impuestosRetenciones);
            }

            var impuestosTraslados = cfdiFactory.createComprobanteImpuestosTraslados();
            for (FacturaRequestDTO.ImpuestosTrasladoAttributes impTras : _req.getImpuestosTraslados()) {

                var impTraslado = cfdiFactory.createComprobanteImpuestosTrasladosTraslado();

                impTraslado.setBase(impTras.getBase());
                impTraslado.setImpuesto(impTras.getImpuesto());
                impTraslado.setTipoFactor(CTipoFactor.fromValue(impTras.getTipoFactor()));
                impTraslado.setTasaOCuota(impTras.getTasaOCuota());
                impTraslado.setImporte(impTras.getImporte());

                impuestosTraslados.getTraslado().add(impTraslado);
            }
            if (!_req.getImpuestosTraslados().isEmpty()) {
                impuestos.setTraslados(impuestosTraslados);
            }
            _cfdi.setImpuestos(impuestos);

            // Marshalling
            StringWriter swriter = new StringWriter();
            _marshaller.marshal(_cfdi, swriter);

            return swriter;

        } catch (JAXBException | DatatypeConfigurationException | IOException ex) {
            throw new FormatError("", ex);
        }
    }
}
