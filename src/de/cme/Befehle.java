
package de.cme;

import java.util.ArrayList;


public interface Befehle {
    
    /**
     * @param weichenNummer: Nummer des Knotens
     * @param stellung: Stellung rund 'r' (links) oder gerade 'g' (rechts)
     */
    public void stelleWeiche(int weichenNummer, char stellung);
    
    /**
     * @param RMKNummer: Nummer des Knotens des Rückmeldeabschnittes
     * @return Gibt die Belegung des Rückmeldeabschnittes zurück
     *          true für belegt, false für frei
     */
    public boolean leseRMK(int RMKNummer);
    
    /**
     *
     * @return Gibt die belegten Rückmeldeabschnitte als Liste, die 
     *          die Nummer der Knoten enthält, zurück
     */
    public ArrayList gibBelegteRMK();
    
    /**
     *
     * @param LokName: Adresse der drei Loks (24, 10, 11)
     * @param geschwindigkeit: Parameter für die Geschwindigkeit der Lok
     *                          (1-1000)
     */
    public void fahreLok(int LokName, int geschwindigkeit);
    
    /**
     *
     * @param LokName: Adresse der drei Loks (24, 10, 11)
     * @param lichtEin: Licht ein oder aus schalten (true/false)
     */
    public void schalteLichtVonLok(int LokName, boolean lichtEin);
}
