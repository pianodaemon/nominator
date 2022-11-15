package com.immortalcrab.cfdi.pipeline;

import com.immortalcrab.cfdi.error.FormatError;
import com.immortalcrab.cfdi.error.StorageError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import mx.gob.sat.cfd._4.Comprobante;
import mx.gob.sat.cfd._4.ObjectFactory;
import mx.gob.sat.sitio_internet.cfd.catalogos.*;

import javax.xml.datatype.DatatypeFactory;
import java.io.StringWriter;
import java.util.List;

@AllArgsConstructor
@Log4j
@Getter
class FacturaXml {

    private final @NonNull FacturaRequestDTO cfdiReq;
    private final @NonNull IStorage st;

    public static String render(Request cfdiReq, IStamp<PacRegularRequest, PacRegularResponse> stamper, IStorage st) throws FormatError, StorageError {

        FacturaXml ic = new FacturaXml((FacturaRequestDTO) cfdiReq, st);

        StringWriter cfdi = ic.shape();
        PacRegularRequest pacReq = new PacRegularRequest(cfdi.toString());
        PacRegularResponse pacRes = stamper.impress(pacReq);
 
        return "It must be slightly implemented as it was in lola";
    }

    private StringWriter shape() throws FormatError {

        StringWriter sw = new StringWriter();

        try {
            ObjectFactory cfdiFactory = new ObjectFactory();
            Comprobante cfdi = cfdiFactory.createComprobante();
            cfdi.setVersion(FacturaRequestDTO.CFDI_VER);
            cfdi.setSerie(cfdiReq.getComprobanteAttributes().getSerie());
            cfdi.setFolio(cfdiReq.getComprobanteAttributes().getFolio());
            cfdi.setFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(cfdiReq.getComprobanteAttributes().getFecha()));
            cfdi.setFormaPago(cfdiReq.getComprobanteAttributes().getFormaPago());
            cfdi.setNoCertificado(cfdiReq.getComprobanteAttributes().getNoCertificado());
            cfdi.setSubTotal(cfdiReq.getComprobanteAttributes().getSubTotal());
            cfdi.setDescuento(cfdiReq.getComprobanteAttributes().getDescuento());
            cfdi.setMoneda(CMoneda.fromValue(cfdiReq.getComprobanteAttributes().getMoneda()));
            cfdi.setTipoCambio(cfdiReq.getComprobanteAttributes().getTipoCambio());
            cfdi.setTotal(cfdiReq.getComprobanteAttributes().getTotal());
            cfdi.setTipoDeComprobante(CTipoDeComprobante.fromValue(FacturaRequestDTO.TIPO_COMPROBANTE));
            cfdi.setExportacion(cfdiReq.getComprobanteAttributes().getExportacion());
            cfdi.setMetodoPago(CMetodoPago.fromValue(cfdiReq.getComprobanteAttributes().getMetodoPago()));
            cfdi.setLugarExpedicion(cfdiReq.getComprobanteAttributes().getLugarExpedicion());

            Comprobante.Emisor emisor = cfdiFactory.createComprobanteEmisor();
            emisor.setRfc(cfdiReq.getEmisorAttributes().getRfc());
            emisor.setNombre(cfdiReq.getEmisorAttributes().getNombre());
            emisor.setRegimenFiscal(cfdiReq.getEmisorAttributes().getRegimenFiscal());
            cfdi.setEmisor(emisor);

            Comprobante.Receptor receptor = cfdiFactory.createComprobanteReceptor();
            receptor.setRfc(cfdiReq.getReceptorAttributes().getRfc());
            receptor.setNombre(cfdiReq.getReceptorAttributes().getNombre());
            receptor.setDomicilioFiscalReceptor(cfdiReq.getReceptorAttributes().getDomicilioFiscalReceptor());
            receptor.setResidenciaFiscal(CPais.fromValue(cfdiReq.getReceptorAttributes().getResidenciaFiscal()));
            receptor.setRegimenFiscalReceptor(cfdiReq.getReceptorAttributes().getRegimenFiscalReceptor());
            receptor.setUsoCFDI(CUsoCFDI.fromValue(cfdiReq.getReceptorAttributes().getUsoCfdi()));
            cfdi.setReceptor(receptor);

            // Conceptos
            Comprobante.Conceptos conceptos = cfdiFactory.createComprobanteConceptos();

            for (FacturaRequestDTO.PseudoConcepto psc : cfdiReq.getPseudoConceptos()) {

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
                List<FacturaRequestDTO.ConceptoTrasladoAttributes> psTraslados = psc.getTraslados();
                for (FacturaRequestDTO.ConceptoTrasladoAttributes t: psTraslados) {

                    var traslado = cfdiFactory.createComprobanteConceptosConceptoImpuestosTrasladosTraslado();
                    traslado.setBase(t.getBase());
                    traslado.setImpuesto(t.getImpuesto());
                    traslado.setTipoFactor(CTipoFactor.fromValue(t.getTipoFactor()));
                    traslado.setTasaOCuota(t.getTasaOCuota());
                    traslado.setImporte(t.getImporte());
                    traslados.getTraslado().add(traslado);
                }

                var retenciones = cfdiFactory.createComprobanteConceptosConceptoImpuestosRetenciones();
                List<FacturaRequestDTO.ConceptoRetencionAttributes> psRetenciones = psc.getRetenciones();
                for (FacturaRequestDTO.ConceptoRetencionAttributes r: psRetenciones) {

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
            impuestos.setTotalImpuestosRetenidos(cfdiReq.getImpuestosAttributes().getTotalImpuestosRetenidos());
            impuestos.setTotalImpuestosTrasladados(cfdiReq.getImpuestosAttributes().getTotalImpuestosTrasladados());

            var impuestosRetenciones = cfdiFactory.createComprobanteImpuestosRetenciones();
            for (FacturaRequestDTO.ImpuestosRetencionAttributes impRet : cfdiReq.getImpuestosRetenciones()) {
                var impRetencion = cfdiFactory.createComprobanteImpuestosRetencionesRetencion();
                impRetencion.setImpuesto(impRet.getImpuesto());
                impRetencion.setImporte(impRet.getImporte());
                impuestosRetenciones.getRetencion().add(impRetencion);
            }
            impuestos.setRetenciones(impuestosRetenciones);

            var impuestosTraslados = cfdiFactory.createComprobanteImpuestosTraslados();
            for (FacturaRequestDTO.ImpuestosTrasladoAttributes impTras : cfdiReq.getImpuestosTraslados()) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sw;
    }
}
