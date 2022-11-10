package com.immortalcrab.nominator.formats;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableMap;

class SatCatalog {

    public static Map < String, String > getCatalog(String field) throws Exception {

        Map < String, String > m = null;

        switch (field) {
            case "clave_ent_fed":
                {
                    m = Stream.of(new String[][] {
                        {
                            "AGU",
                            "Aguascalientes"
                        }, {
                            "BCN",
                            "Baja California"
                        }, {
                            "BCS",
                            "Baja California Sur"
                        }, {
                            "CAM",
                            "Campeche"
                        }, {
                            "CHP",
                            "Chiapas"
                        }, {
                            "CHH",
                            "Chihuahua"
                        }, {
                            "COA",
                            "Coahuila"
                        }, {
                            "COL",
                            "Colima"
                        }, {
                            "CMX",
                            "Ciudad de México"
                        }, {
                            "DUR",
                            "Durango"
                        }, {
                            "GUA",
                            "Guanajuato"
                        }, {
                            "GRO",
                            "Guerrero"
                        }, {
                            "HID",
                            "Hidalgo"
                        }, {
                            "JAL",
                            "Jalisco"
                        }, {
                            "MEX",
                            "Estado de México"
                        }, {
                            "MIC",
                            "Michoacán"
                        }, {
                            "MOR",
                            "Morelos"
                        }, {
                            "NAY",
                            "Nayarit"
                        }, {
                            "NLE",
                            "Nuevo León"
                        }, {
                            "OAX",
                            "Oaxaca"
                        }, {
                            "PUE",
                            "Puebla"
                        }, {
                            "QUE",
                            "Querétaro"
                        }, {
                            "ROO",
                            "Quintana Roo"
                        }, {
                            "SLP",
                            "San Luis Potosí"
                        }, {
                            "SIN",
                            "Sinaloa"
                        }, {
                            "SON",
                            "Sonora"
                        }, {
                            "TAB",
                            "Tabasco"
                        }, {
                            "TAM",
                            "Tamaulipas"
                        }, {
                            "TLA",
                            "Tlaxcala"
                        }, {
                            "VER",
                            "Veracruz"
                        }, {
                            "YUC",
                            "Yucatán"
                        }, {
                            "ZAC",
                            "Zacatecas"
                        }
                    }).collect(Collectors.toMap(data - > data[0], data - > data[1]));
                }
                break;

            case "regimen_fiscal":
                {
                    m = Stream.of(new String[][] {
                        {
                            "601",
                            "General de Ley Personas Morales"
                        }, {
                            "603",
                            "Personas Morales con Fines no Lucrativos"
                        }, {
                            "605",
                            "Sueldos y Salarios e Ingresos Asimilados a Salarios"
                        }, {
                            "606",
                            "Arrendamiento"
                        }, {
                            "607",
                            "Régimen de Enajenación o Adquisición de Bienes"
                        }, {
                            "608",
                            "Demás ingresos"
                        }, {
                            "610",
                            "Residentes en el Extranjero sin Establecimiento Permanente en México"
                        }, {
                            "611",
                            "Ingresos por Dividendos (socios y accionistas)"
                        }, {
                            "612",
                            "Personas Físicas con Actividades Empresariales y Profesionales"
                        }, {
                            "614",
                            "Ingresos por intereses"
                        }, {
                            "615",
                            "Régimen de los ingresos por obtención de premios"
                        }, {
                            "616",
                            "Sin obligaciones fiscales"
                        }, {
                            "620",
                            "Sociedades Cooperativas de Producción que optan por diferir sus ingresos"
                        }, {
                            "621",
                            "Incorporación Fiscal"
                        }, {
                            "622",
                            "Actividades Agrícolas, Ganaderas, Silvícolas y Pesqueras"
                        }, {
                            "623",
                            "Opcional para Grupos de Sociedades"
                        }, {
                            "624",
                            "Coordinados"
                        }, {
                            "625",
                            "Régimen de las Actividades Empresariales con ingresos a través de Plataformas Tecnológicas"
                        }, {
                            "626",
                            "Régimen Simplificado de Confianza"
                        },
                    }).collect(Collectors.toMap(data - > data[0], data - > data[1]));
                }
                break;

            case "tipo_jornada":
                {
                    m = Stream.of(new String[][] {
                        {
                            "01",
                            "Diurna"
                        }, {
                            "02",
                            "Nocturna"
                        }, {
                            "03",
                            "Mixta"
                        }, {
                            "04",
                            "Por hora"
                        }, {
                            "05",
                            "Reducida"
                        }, {
                            "06",
                            "Continuada"
                        }, {
                            "07",
                            "Partida"
                        }, {
                            "08",
                            "Por turnos"
                        }, {
                            "99",
                            "Otra Jornada"
                        },
                    }).collect(Collectors.toMap(data - > data[0], data - > data[1]));
                }
                break;

            case "riesgo_puesto":
                m = Map.of(
                    "1", "Clase I",
                    "2", "Clase II",
                    "3", "Clase III",
                    "4", "Clase IV",
                    "5", "Clase V",
                    "99", "No aplica");
                break;

            case "tipo_contrato":
                {
                    m = Stream.of(new String[][] {
                        {
                            "01",
                            "Contrato de trabajo por tiempo indeterminado"
                        }, {
                            "02",
                            "Contrato de trabajo para obra determinada"
                        }, {
                            "03",
                            "Contrato de trabajo por tiempo determinado"
                        }, {
                            "04",
                            "Contrato de trabajo por temporada"
                        }, {
                            "05",
                            "Contrato de trabajo sujeto a prueba"
                        }, {
                            "06",
                            "Contrato de trabajo con capacitación inicial"
                        }, {
                            "07",
                            "Modalidad de contratación por pago de hora laborada"
                        }, {
                            "08",
                            "Modalidad de trabajo por comisión laboral"
                        }, {
                            "09",
                            "Modalidades de contratación donde no existe relación de trabajo"
                        }, {
                            "10",
                            "Jubilación, pensión, retiro."
                        }, {
                            "99",
                            "Otro contrato"
                        },
                    }).collect(Collectors.toMap(data - > data[0], data - > data[1]));
                }
                break;

            case "tipo_regimen":
                m = ImmutableMap. < String, String > builder()
                    .put("02", "Sueldos (Incluye ingresos señalados en la fracción I del artículo 94 de LISR)")
                    .put("03", "Jubilados")
                    .put("04", "Pensionados")
                    .put("05", "Asimilados Miembros Sociedades Cooperativas Produccion")
                    .put("06", "Asimilados Integrantes Sociedades Asociaciones Civiles")
                    .put("07", "Asimilados Miembros consejos")
                    .put("08", "Asimilados comisionistas")
                    .put("09", "Asimilados Honorarios")
                    .put("10", "Asimilados acciones")
                    .put("11", "Asimilados otros")
                    .put("12", "Jubilados o Pensionados")
                    .put("13", "Indemnización o Separación")
                    .put("99", "Otro Regimen")
                    .build();
                break;

            case "tipo_nomina":
                {
                    m = Map.of(
                        "O", "Nómina ordinaria",
                        "E", "Nómina extraordinaria");
                }
                break;

            case "periodicidad_pago":
                m = ImmutableMap. < String, String > builder()
                    .put("01", "Diario")
                    .put("02", "Semanal")
                    .put("03", "Catorcenal")
                    .put("04", "Quincenal")
                    .put("05", "Mensual")
                    .put("06", "Bimestral")
                    .put("07", "Unidad obra")
                    .put("08", "Comisión")
                    .put("09", "Precio alzado")
                    .put("10", "Decenal")
                    .put("99", "Otra Periodicidad")
                    .build();
                break;

            case "clave_unidad":
                {
                    m = Map.of(
                        "ACT", "Actividad",
                        "E48", "Unidad de servicio");
                }
                break;

            case "clave_prod_serv":
                {
                    m = Map.of(
                        "84111505", "Servicios de contabilidad de sueldos y salarios");
                }
                break;

            case "tipo_percepcion":
                m = ImmutableMap. < String, String > builder()
                    .put("001", "Sueldos, Salarios  Rayas y Jornales")
                    .put("002", "Gratificación Anual (Aguinaldo)")
                    .put("003", "Participación de los Trabajadores en las Utilidades PTU")
                    .put("004", "Reembolso de Gastos Médicos Dentales y Hospitalarios")
                    .put("005", "Fondo de Ahorro")
                    .put("006", "Caja de ahorro")
                    .put("009", "Contribuciones a Cargo del Trabajador Pagadas por el Patrón")
                    .put("010", "Premios por puntualidad")
                    .put("011", "Prima de Seguro de vida")
                    .put("012", "Seguro de Gastos Médicos Mayores")
                    .put("013", "Cuotas Sindicales Pagadas por el Patrón")
                    .put("014", "Subsidios por incapacidad")
                    .put("015", "Becas para trabajadores y/o hijos")
                    .put("019", "Horas extra")
                    .put("020", "Prima dominical")
                    .put("021", "Prima vacacional")
                    .put("022", "Prima por antigüedad")
                    .put("023", "Pagos por separación")
                    .put("024", "Seguro de retiro")
                    .put("025", "Indemnizaciones")
                    .put("026", "Reembolso por funeral")
                    .put("027", "Cuotas de seguridad social pagadas por el patrón")
                    .put("028", "Comisiones")
                    .put("029", "Vales de despensa")
                    .put("030", "Vales de restaurante")
                    .put("031", "Vales de gasolina")
                    .put("032", "Vales de ropa")
                    .put("033", "Ayuda para renta")
                    .put("034", "Ayuda para artículos escolares")
                    .put("035", "Ayuda para anteojos")
                    .put("036", "Ayuda para transporte")
                    .put("037", "Ayuda para gastos de funeral")
                    .put("038", "Otros ingresos por salarios")
                    .put("039", "Jubilaciones, pensiones o haberes de retiro")
                    .put("044", "Jubilaciones, pensiones o haberes de retiro en parcialidades")
                    .put("045", "Ingresos en acciones o títulos valor que representan bienes")
                    .put("046", "Ingresos asimilados a salarios")
                    .put("047", "Alimentación diferentes a los establecidos en el Art 94 último párrafo LISR")
                    .put("048", "Habitación")
                    .put("049", "Premios por asistencia")
                    .put("050", "Viáticos")
                    .put("051",
                        "Pagos por gratificaciones, primas, compensaciones, recompensas u otros a extrabajadores derivados de jubilación en parcialidades")
                    .put("052",
                        "Pagos que se realicen a extrabajadores que obtengan una jubilación en parcialidades derivados de la ejecución de resoluciones judicial o de un laudo")
                    .put("053",
                        "Pagos que se realicen a extrabajadores que obtengan una jubilación en una sola exhibición derivados de la ejecución de resoluciones judicial o de un laudo")
                    .build();
                break;

            case "tipo_deduccion":
                m = ImmutableMap. < String, String > builder()
                    .put("001", "Seguridad social")
                    .put("002", "ISR")
                    .put("003", "Aportaciones a retiro, cesantía en edad avanzada y vejez.")
                    .put("004", "Otros")
                    .put("005", "Aportaciones a Fondo de vivienda")
                    .put("006", "Descuento por incapacidad")
                    .put("007", "Pensión alimenticia")
                    .put("008", "Renta")
                    .put("009", "Préstamos provenientes del Fondo Nacional de la Vivienda para los Trabajadores")
                    .put("010", "Pago por crédito de vivienda")
                    .put("011", "Pago de abonos INFONACOT")
                    .put("012", "Anticipo de salarios")
                    .put("013", "Pagos hechos con exceso al trabajador")
                    .put("014", "Errores")
                    .put("015", "Pérdidas")
                    .put("016", "Averías")
                    .put("017", "Adquisición de artículos producidos por la empresa o establecimiento")
                    .put("018",
                        "Cuotas para la constitución y fomento de sociedades cooperativas y de cajas de ahorro")
                    .put("019", "Cuotas sindicales")
                    .put("020", "Ausencia (Ausentismo)")
                    .put("021", "Cuotas obrero patronales")
                    .put("022", "Impuestos Locales")
                    .put("023", "Aportaciones voluntarias")
                    .put("024", "Ajuste en Gratificación Anual (Aguinaldo) Exento")
                    .put("025", "Ajuste en Gratificación Anual (Aguinaldo) Gravado")
                    .put("026", "Ajuste en Participación de los Trabajadores en las Utilidades PTU Exento")
                    .put("027", "Ajuste en Participación de los Trabajadores en las Utilidades PTU Gravado")
                    .put("028", "Ajuste en Reembolso de Gastos Médicos Dentales y Hospitalarios Exento")
                    .put("029", "Ajuste en Fondo de ahorro Exento")
                    .put("030", "Ajuste en Caja de ahorro Exento")
                    .put("031", "Ajuste en Contribuciones a Cargo del Trabajador Pagadas por el Patrón Exento")
                    .put("032", "Ajuste en Premios por puntualidad Gravado")
                    .put("033", "Ajuste en Prima de Seguro de vida Exento")
                    .put("034", "Ajuste en Seguro de Gastos Médicos Mayores Exento")
                    .put("035", "Ajuste en Cuotas Sindicales Pagadas por el Patrón Exento")
                    .put("036", "Ajuste en Subsidios por incapacidad Exento")
                    .put("037", "Ajuste en Becas para trabajadores y/o hijos Exento")
                    .put("038", "Ajuste en Horas extra Exento")
                    .put("039", "Ajuste en Horas extra Gravado")
                    .put("040", "Ajuste en Prima dominical Exento")
                    .put("041", "Ajuste en Prima dominical Gravado")
                    .put("042", "Ajuste en Prima vacacional Exento")
                    .put("043", "Ajuste en Prima vacacional Gravado")
                    .put("044", "Ajuste en Prima por antigüedad Exento")
                    .put("045", "Ajuste en Prima por antigüedad Gravado")
                    .put("046", "Ajuste en Pagos por separación Exento")
                    .put("047", "Ajuste en Pagos por separación Gravado")
                    .put("048", "Ajuste en Seguro de retiro Exento")
                    .put("049", "Ajuste en Indemnizaciones Exento")
                    .put("050", "Ajuste en Indemnizaciones Gravado")
                    .put("051", "Ajuste en Reembolso por funeral Exento")
                    .put("052", "Ajuste en Cuotas de seguridad social pagadas por el patrón Exento")
                    .put("053", "Ajuste en Comisiones Gravado")
                    .put("054", "Ajuste en Vales de despensa Exento")
                    .put("055", "Ajuste en Vales de restaurante Exento")
                    .put("056", "Ajuste en Vales de gasolina Exento")
                    .put("057", "Ajuste en Vales de ropa Exento")
                    .put("058", "Ajuste en Ayuda para renta Exento")
                    .put("059", "Ajuste en Ayuda para artículos escolares Exento")
                    .put("060", "Ajuste en Ayuda para anteojos Exento")
                    .put("061", "Ajuste en Ayuda para transporte Exento")
                    .put("062", "Ajuste en Ayuda para gastos de funeral Exento")
                    .put("063", "Ajuste en Otros ingresos por salarios Exento")
                    .put("064", "Ajuste en Otros ingresos por salarios Gravado")
                    .put("065",
                        "Ajuste en Jubilaciones, pensiones o haberes de retiro en una sola exhibición Exento ")
                    .put("066",
                        "Ajuste en Jubilaciones, pensiones o haberes de retiro en una sola exhibición Gravado")
                    .put("067", "Ajuste en Pagos por separación Acumulable")
                    .put("068", "Ajuste en Pagos por separación No acumulable")
                    .put("069", "Ajuste en Jubilaciones, pensiones o haberes de retiro en parcialidades Exento")
                    .put("070", "Ajuste en Jubilaciones, pensiones o haberes de retiro en parcialidades Gravado")
                    .put("071", "Ajuste en Subsidio para el empleo (efectivamente entregado al trabajador)")
                    .put("072", "Ajuste en Ingresos en acciones o títulos valor que representan bienes Exento")
                    .put("073", "Ajuste en Ingresos en acciones o títulos valor que representan bienes Gravado")
                    .put("074", "Ajuste en Alimentación Exento")
                    .put("075", "Ajuste en Alimentación Gravado")
                    .put("076", "Ajuste en Habitación Exento")
                    .put("077", "Ajuste en Habitación Gravado")
                    .put("078", "Ajuste en Premios por asistencia")
                    .put("079",
                        "Ajuste en Pagos distintos a los listados y que no deben considerarse como ingreso por sueldos, salarios o ingresos asimilados.")
                    .put("080", "Ajuste en Viáticos gravados")
                    .put("081", "Ajuste en Viáticos (entregados al trabajador)")
                    .put("082", "Ajuste en Fondo de ahorro Gravado")
                    .put("083", "Ajuste en Caja de ahorro Gravado")
                    .put("084", "Ajuste en Prima de Seguro de vida Gravado")
                    .put("085", "Ajuste en Seguro de Gastos Médicos Mayores Gravado")
                    .put("086", "Ajuste en Subsidios por incapacidad Gravado")
                    .put("087", "Ajuste en Becas para trabajadores y/o hijos Gravado")
                    .put("088", "Ajuste en Seguro de retiro Gravado")
                    .put("089", "Ajuste en Vales de despensa Gravado")
                    .put("090", "Ajuste en Vales de restaurante Gravado")
                    .put("091", "Ajuste en Vales de gasolina Gravado")
                    .put("092", "Ajuste en Vales de ropa Gravado")
                    .put("093", "Ajuste en Ayuda para renta Gravado")
                    .put("094", "Ajuste en Ayuda para artículos escolares Gravado")
                    .put("095", "Ajuste en Ayuda para anteojos Gravado")
                    .put("096", "Ajuste en Ayuda para transporte Gravado")
                    .put("097", "Ajuste en Ayuda para gastos de funeral Gravado")
                    .put("098", "Ajuste a ingresos asimilados a salarios gravados")
                    .put("099", "Ajuste a ingresos por sueldos y salarios gravados")
                    .put("100", "Ajuste en Viáticos exentos")
                    .put("101", "ISR Retenido de ejercicio anterior")
                    .put("102",
                        "Ajuste a pagos por gratificaciones, primas, compensaciones, recompensas u otros a extrabajadores derivados de jubilación en parcialidades, gravados")
                    .put("103",
                        "Ajuste a pagos que se realicen a extrabajadores que obtengan una jubilación en parcialidades derivados de la ejecución de una resolución judicial o de un laudo gravados")
                    .put("104",
                        "Ajuste a pagos que se realicen a extrabajadores que obtengan una jubilación en parcialidades derivados de la ejecución de una resolución judicial o de un laudo exentos")
                    .put("105",
                        "Ajuste a pagos que se realicen a extrabajadores que obtengan una jubilación en una sola exhibición derivados de la ejecución de una resolución judicial o de un laudo gravados")
                    .put("106",
                        "Ajuste a pagos que se realicen a extrabajadores que obtengan una jubilación en una sola exhibición derivados de la ejecución de una resolución judicial o de un laudo exentos")
                    .put("107", "Ajuste al Subsidio Causado")
                    .build();
                break;

            case "tipo_otro_pago":
                m = ImmutableMap. < String, String > builder()
                    .put("001", "Reintegro de ISR pagado en exceso (siempre que no haya sido enterado al SAT).")
                    .put("002", "Subsidio para el empleo (efectivamente entregado al trabajador).")
                    .put("003", "Viáticos (entregados al trabajador).")
                    .put("004", "Aplicación de saldo a favor por compensación anual.")
                    .put("005",
                        "Reintegro de ISR retenido en exceso de ejercicio anterior (siempre que no haya sido enterado al SAT).")
                    .put("006", "Alimentos en bienes (Servicios de comedor y comida) Art 94 último párrafo LISR.")
                    .put("007", "ISR ajustado por subsidio.")
                    .put("008",
                        "Subsidio efectivamente entregado que no correspondía (Aplica sólo cuando haya ajuste al cierre de mes en relación con el Apéndice 7 de la guía de llenado de nómina).")
                    .put("009", "Reembolso de descuentos efectuados para el crédito de vivienda.")
                    .put("999",
                        "Pagos distintos a los listados y que no deben considerarse como ingreso por sueldos, salarios o ingresos asimilados.")
                    .build();
                break;

            default:
                throw new Exception(String.format("Field %s is not found in SAT catalog.", field));
        }

        return m;
    }
}
