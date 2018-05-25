package com.ids.ids.utils;

/**
 * Classe statica per contenere i vari parametri utilizzati nel programma
 */
public class Parametri {

    // Filtro dispositivi BLE,
    // specificare il nome comune ai beacon a cui ci si vuole collegare
    public static final  String FILTRO_BLE_DEVICE = "CC2650 SensorTag";
    //Periodo scansione dispositivi BLE
    public static final int T_SCAN_PERIOD = 2000;
    // periodo scansione localizzatore
    public static final int T_POSIZIONE = 3000;
    // periodo di richiesta aggiornamenti al server
    public static final int T_AGGIORNAMENTI  = 4000; //ogni 1 minuti
    // Hosting Server
    public static  String PATH;

    public static void  setPath(String path){
        PATH = "http:/"+path+":8080";
    }
}
