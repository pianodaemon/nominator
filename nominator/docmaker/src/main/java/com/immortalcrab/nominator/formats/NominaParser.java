package com.immortalcrab.nominator.formats;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class NominaParser {

    private static final String CFDI_COMPROBANTE = "cfdi:Comprobante";
    private static final String TFD_TIMBREFISCALDIGITAL = "tfd:TimbreFiscalDigital";

    private String UUID;
    private String selloSAT;
    private String selloCFD;
    private String noCertificadoSAT;
    private String fechaTimbrado;
    private String rfcProvCertif;
    private String version;
    private String noCertificado;

    public NominaParser(InputStream is) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        sp.parse(is, new FormatHandler());
    }

    private class FormatHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

            if (qName.equals(TFD_TIMBREFISCALDIGITAL)) {

                for (int i = 0; i < atts.getLength(); i++) {

                    String vn = atts.getQName(i);
                    if (vn.equals("UUID")) {

                        setUUID(atts.getValue(i));
                    }

                    if (vn.equals("SelloSAT")) {

                        setSelloSAT(atts.getValue(i));
                    }

                    if (vn.equals("SelloCFD")) {

                        setSelloCFD(atts.getValue(i));
                    }

                    if (vn.equals("NoCertificadoSAT")) {

                        setNoCertificadoSAT(atts.getValue(i));
                    }

                    if (vn.equals("FechaTimbrado")) {

                        setFechaTimbrado(atts.getValue(i));
                    }

                    if (vn.equals("Version")) {

                        setVersion(atts.getValue(i));
                    }

                    if (vn.equals("RfcProvCertif")) {

                        setRfcProvCertif(atts.getValue(i));
                    }
                }
            }

            if (qName.equals(CFDI_COMPROBANTE)) {

                for (int i = 0; i < atts.getLength(); i++) {

                    String vn = atts.getQName(i);
                    if (vn.equals("NoCertificado")) {

                        setNoCertificado(atts.getValue(i));
                    }
                }
            }
        }
    }
}
