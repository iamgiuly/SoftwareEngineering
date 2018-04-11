package com.ids.ids.utils;

/**
 * Classe statica per contenere i vari parametri utilizzati nel programma
 */
public class Parametri {


    // Filtro dispositivi BLE,
    // specificare il nome comune ai beacon a cui ci si vuole collegare
    public  static  String FILTRO_BLE_DEVICE = "CC2650 SensorTag";
    //Periodo scansione dispositivi BLE
    public static int T_SCAN_PERIOD = 2000;
    // periodo scansione localizzatore
    public static int T_POSIZIONE_EMERGENZA = 3000;
    // periodo scansione segnalazione
    public static int T_POSIZIONE_SEGNALAZIONE = 5000;
    // periodo di richiesta aggiornamenti al server
    public static int T_AGGIORNAMENTI  = 2000;
    // Hosting Server
    public static String PATH = "http://172.23.128.184:8080";


}
