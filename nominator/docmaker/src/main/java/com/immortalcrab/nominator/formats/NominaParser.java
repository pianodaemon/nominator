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

    private String UUID;
    private String selloSat;
    private String selloCfd;
    private String noCertificadoSAT;
    private String fechaTimbre;

    private void read(InputStream url) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(url, new FormatHandler());
        } catch (ParserConfigurationException e) {
            System.err.println("error de  parseo " + e);
        } catch (SAXException e2) {
            System.err.println(e2);
            System.err.println("error de  sax: " + e2.getStackTrace());
        } catch (IOException e3) {
            System.err.println("error de  io: " + e3.getMessage());
        }
    }

    private class FormatHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

            if (qName.equals("tfd:TimbreFiscalDigital")) {

                for (int i = 0; i < atts.getLength(); i++) {

                    String vn = atts.getQName(i);
                    if (vn.equals("UUID")) {

                        setUUID(atts.getValue(i));
                    }

                    if (vn.equals("selloSAT")) {

                        setSelloSat(atts.getValue(i));
                    }

                    if (vn.equals("selloCFD")) {

                        setSelloCfd(atts.getValue(i));
                    }

                    if (vn.equals("noCertificadoSAT")) {

                        setNoCertificadoSAT(atts.getValue(i));
                    }

                    if (vn.equals("FechaTimbrado")) {
                        setFechaTimbre(atts.getValue(i));
                    }
                }
            }
        }
    }
}