package com.immortalcrab.nominator.formats;

import java.util.ArrayList;
import java.util.HashMap;

public class NumberToSpanishTranslator {
    private static HashMap<Integer, String> uni;
    private static HashMap<Integer, String> decEspecial;
    private static HashMap<Integer, String> dec;
    private static HashMap<Integer, String> cent;

    static {
        uni = new HashMap<>();
        uni.put(1, "un");
        uni.put(2, "dos");
        uni.put(3, "tres");
        uni.put(4, "cuatro");
        uni.put(5, "cinco");
        uni.put(6, "seis");
        uni.put(7, "siete");
        uni.put(8, "ocho");
        uni.put(9, "nueve");

        decEspecial = new HashMap<>();
        decEspecial.put(10, "diez");
        decEspecial.put(11, "once");
        decEspecial.put(12, "doce");
        decEspecial.put(13, "trece");
        decEspecial.put(14, "catorce");
        decEspecial.put(15, "quince");
        decEspecial.put(16, "dieciséis");
        decEspecial.put(17, "diecisiete");
        decEspecial.put(18, "dieciocho");
        decEspecial.put(19, "diecinueve");
        decEspecial.put(20, "veinte");
        decEspecial.put(21, "veintiún");
        decEspecial.put(22, "veintidós");
        decEspecial.put(23, "veintitrés");
        decEspecial.put(24, "veinticuatro");
        decEspecial.put(25, "veinticinco");
        decEspecial.put(26, "veintiséis");
        decEspecial.put(27, "veintisiete");
        decEspecial.put(28, "veintiocho");
        decEspecial.put(29, "veintinueve");

        dec = new HashMap<>();
        dec.put(3, "treinta");
        dec.put(4, "cuarenta");
        dec.put(5, "cincuenta");
        dec.put(6, "sesenta");
        dec.put(7, "setenta");
        dec.put(8, "ochenta");
        dec.put(9, "noventa");

        cent = new HashMap<>();
        cent.put(1, "ciento");
        cent.put(2, "docientos");
        cent.put(3, "trecientos");
        cent.put(4, "cuatrocientos");
        cent.put(5, "quinientos");
        cent.put(6, "seiscientos");
        cent.put(7, "setecientos");
        cent.put(8, "ochocientos");
        cent.put(9, "novecientos");
    }

    private static String translateTriplet(int cifra) {
        if (cifra == 100) {
            return "cien";
        }

        var s = new ArrayList<String>();
        int centenas = cifra / 100;
        if (centenas > 0) {
            s.add(cent.get(centenas));
        }

        int sinCentenas = cifra - centenas * 100;
        int decenas = sinCentenas / 10;

        if (decenas == 1 || decenas == 2) {
            s.add(decEspecial.get(sinCentenas));

        } else {
            if (decenas > 0) {
                s.add(dec.get(decenas));
            }

            int unidades = sinCentenas - decenas * 10;
            if (unidades > 0) {
                if (decenas > 0) {
                    s.add("y");
                }
                s.add(uni.get(unidades));
            }
        }

        return String.join(" ", s);
    }

    public static String translate(long cifra) {
        if (cifra == 0) {
            return "cero";
        }

        String s = String.valueOf(cifra);
        int l = s.length();
        int cifrasRemanentes = l % 3;
        int triplets = l / 3;

        int grupos;
        if (cifrasRemanentes > 0) {
            grupos = triplets + 1;
        } else {
            grupos = triplets;
        }
        boolean millones = false;
        boolean billones = false;
        boolean trillones = false;

        var resultado = new ArrayList<String>();
        int ini = 0;
        int fin;
        if (cifrasRemanentes > 0) {
            fin = cifrasRemanentes;
        } else {
            fin = 3;
        }

        for (int i = grupos; i > 0; i--) {

            String trip = translateTriplet(Integer.parseInt(s.substring(ini, fin)));
            if (!trip.isEmpty()) {

                resultado.add(trip);
                if (i % 2 == 0) {
                    if (trip.equals("un")) {
                        resultado.remove(resultado.size() - 1);
                    }
                    resultado.add("mil");
                }
                if (i == 3 || i == 4) {
                    millones = true;
                } else if (i == 5 || i == 6) {
                    billones = true;
                } else if (i == 7 || i == 8) {
                    trillones = true;
                }
            }

            if (millones && i == 3) {
                if (trip.equals("un")) {
                    resultado.add("millón");
                } else {
                    resultado.add("millones");
                }
            } else if (billones && i == 5) {
                if (trip.equals("un")) {
                    resultado.add("billón");
                } else {
                    resultado.add("billones");
                }
            } else if (trillones && i == 7) {
                if (trip.equals("un")) {
                    resultado.add("trillón");
                } else {
                    resultado.add("trillones");
                }
            }

            ini = fin;
            fin += 3;
        }
        return String.join(" ", resultado);
    }
}