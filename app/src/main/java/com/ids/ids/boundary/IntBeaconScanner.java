package com.ids.ids.boundary;

/**
 * Interfaccia implementata dal Beacon scanner
 */
public interface IntBeaconScanner {

    void scansione(Boolean enable);

    String BeaconVicino();

}
