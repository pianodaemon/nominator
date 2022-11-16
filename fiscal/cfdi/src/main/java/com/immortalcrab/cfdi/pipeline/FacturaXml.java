package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.StringWriter;
import java.util.List;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
class FacturaXml {

    private final @NonNull
    FacturaRequestDTO _req;

    private final StringWriter _sw;

    public FacturaXml(FacturaRequestDTO req) throws FormatError {

        _req = req;
        _sw = shape();
    }

    @Override
    public String toString() {
        return _sw.toString();
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();
            cfdi.setVersion(FacturaRequestDTO.CFDI_VER);
            cfdi.setSerie(_req.getComprobanteAttributes().getSerie());
            cfdi.setFolio(_req.getComprobanteAttributes().getFolio());
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(_req.getComprobanteAttributes().getFecha()));
            cfdi.setFormaPago(_req.getComprobanteAttributes().getFormaPago());
            cfdi.setNoCertificado(_req.getComprobanteAttributes().getNoCertificado());
            cfdi.setSubTotal(_req.getComprobanteAttributes().getSubTotal());
            cfdi.setDescuento(_req.getComprobanteAttributes().getDescuento());
            cfdi.setMoneda(CMoneda.fromValue(_req.getComprobanteAttributes().getMoneda()));
            cfdi.setTipoCambio(_req.getComprobanteAttributes().getTipoCambio());
            cfdi.setTotal(_req.getComprobanteAttributes().getTotal());
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(FacturaRequestDTO.TIPO_COMPROBANTE));
            cfdi.setExportacion(_req.getComprobanteAttributes().getExportacion());
            cfdi.setMetodoPago(CMetodoPago.fromValue(_req.getComprobanteAttributes().getMetodoPago()));
            cfdi.setLugarExpedicion(_req.getComprobanteAttributes().getLugarExpedicion());

            Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();
            emisor.setRfc(_req.getEmisorAttributes().getRfc());
            emisor.setNombre(_req.getEmisorAttributes().getNombre());
            emisor.setRegimenFiscal(_req.getEmisorAttributes().getRegimenFiscal());
            cfdi.setEmisor(emisor);

            Comprobante.Receptor receptor = cfdiFactory.createComprobanteReceptor();
            receptor.setRfc(_req.getReceptorAttributes().getRfc());
            receptor.setNombre(_req.getReceptorAttributes().getNombre());
            receptor.setDomicilioFiscalReceptor(_req.getReceptorAttributes().getDomicilioFiscalReceptor());
            receptor.setResidenciaFiscal(CPais.fromValue(_req.getReceptorAttributes().getResidenciaFiscal()));
            receptor.setRegimenFiscalReceptor(_req.getReceptorAttributes().getRegimenFiscalReceptor());
            receptor.setUsoCFDI(CUsoCFDI.fromValue(_req.getReceptorAttributes().getUsoCfdi()));
            cfdi.setReceptor(receptor);

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

                concImpuestos.setTraslados(traslados);
                concImpuestos.setRetenciones(retenciones);
                concepto.setImpuestos(concImpuestos);

                conceptos.getConcepto().add(concepto);
            }
            cfdi.setConceptos(conceptos);

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
            impuestos.setRetenciones(impuestosRetenciones);

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
            impuestos.setTraslados(impuestosTraslados);
            cfdi.setImpuestos(impuestos);

            String contextPath = "mx.gob.sat.cfd._4";
            String schemaLocation = "http://www.sat.gob.mx/cfd/4 http://www.sat.gob.mx/sitio_internet/cfd/4/cfdv40.xsd";

        } catch (DatatypeConfigurationException ex) {
            throw new FormatError("", ex);
        }

        return sw;
    }
}
