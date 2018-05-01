package com.ids.ids.utils;

/**
 * Classe statica per contenere i vari parametri utilizzati nel programma
 */
public class Parametri {

    // Filtro dispositivi BLE,
    // specificare il nome comune ai beacon a cui ci si vuole collegare
    public  static  String FILTRO_BLE_DEVICE = "CC2650 SensorTag";
    //Periodo scansione dispositivi BLE
    public static int T_SCAN_PERIOD = 1000;
    // periodo scansione localizzatore
    public static int T_POSIZIONE = 2000;
    // periodo di richiesta aggiornamenti al server
    public static int T_AGGIORNAMENTI  = 60000; //ogni due minuti
    // Hosting Server
    public static String PATH = "http://192.168.137.1:8080";
}
