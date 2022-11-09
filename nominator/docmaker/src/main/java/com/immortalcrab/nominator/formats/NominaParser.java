package com.immortalcrab.nominator.formats;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class NominaParser {

    private void read(InputStream url){
        try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(url, new ParseaArchivoXmlOfCFDI() );
        }
        catch(ParserConfigurationException e){
            System.err.println("error de  parseo " + e);
        }
        catch(SAXException e2){
            System.err.println(e2);
            System.err.println("error de  sax: " + e2.getStackTrace());
        }
        catch (IOException e3) {
            System.err.println("error de  io: " + e3.getMessage() );
        }
    }

}
