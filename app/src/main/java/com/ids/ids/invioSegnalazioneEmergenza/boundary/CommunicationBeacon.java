package com.ids.ids.invioSegnalazioneEmergenza.boundary;

public class CommunicationBeacon {

    private static CommunicationBeacon instance = null;

    public String getPosizioneUtente(){
        return "";
    }

    public int getPianoUtente(){
        return 0;
    }

    public static CommunicationBeacon getInstance(){
        if(instance == null)
            instance = new CommunicationBeacon();
        return instance;
    }

}
