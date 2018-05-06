package com.ids.ids.beacon;

/**
 * Interfaccia implementata dal Beacon scanner
 */
public interface IntBeaconScanner {

    void scansione(Boolean enable);

    String BeaconVicino();

}
