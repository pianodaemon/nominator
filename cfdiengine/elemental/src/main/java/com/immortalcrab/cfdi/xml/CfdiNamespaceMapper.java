package com.immortalcrab.cfdi.xml;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

class CfdiNamespaceMapper extends NamespacePrefixMapper {

    private static final String CFDI_PREFIX       = "cfdi";
    private static final String CFDI_URI          = "http://www.sat.gob.mx/cfd/4";
    private static final String NOMINA_PREFIX = "nomina12";
    private static final String NOMINA_URI    = "http://www.sat.gob.mx/nomina12";

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {

        if (CFDI_URI.equals(namespaceUri)) {
            return CFDI_PREFIX;

        } else if (NOMINA_URI.equals(namespaceUri)) {
            return NOMINA_PREFIX;
        }

        return suggestion;
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] { CFDI_URI, NOMINA_URI };
    }
}
