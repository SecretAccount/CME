
package de.cme.befehle;

import java.util.List;


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
     * @param RMKNummer: Nummer des Knotens des Rückmeldeabschnittes,
     *                    der ausgelesen werden soll.
     */
    public void sendeRMK(int RMKNummer);
    
    /**
     *
     * @param RMKNummer: Nummer des Knotens des Rückmeldeabschnittes,
     *                    dessen Adresse man haben will
     * @return: gibt die Adresse des Rückmeldeabschnittes als byte zurück
     */
    public byte gibRMKAdresse(int RMKNummer);
    
    /**
     *
     * @return Gibt die belegten Rückmeldeabschnitte als Liste, die 
     *          die Nummer der Knoten enthält, zurück
     */
    public List gibBelegteRMK();
    
    /**
     *
     * @param lokAdresse: Adresse der drei Loks als int (24, 10, 11)
     * @param geschwindigkeit: Parameter für die Geschwindigkeit der Lok
     *                          (1-1000)
     */
    public void fahreLok(int lokAdresse, int geschwindigkeit);
    
    /**
     *
     * @param lokName: Name der drei Loks als String ("rote", "grüne", "blaue")
     * @param geschwindigkeit: Parameter für die Geschwindigkeit der Lok
     *                          (1-1000)
     */
    public void fahreLok(String lokName, int geschwindigkeit);
    
    /**
     * Überladene Methode: bypassTimer ist false, Timer wird nicht umgangen
     * Geschwindigkeit kann gesendet werden als Parameter
     * Es wird die Standard-Lok-Adresse verwendet (24)
     *
     * @param geschwindigkeit: (int) Geschwindigkeit der Lok von 0-100 (wird
     * intern in 0-1000 umgewandelt)
     */
    public void fahreLok(int geschwindigkeit);
    
    /**
     *
     * @param lokAdresse: Adresse der drei Loks (24, 10, 11)
     * @param lichtEin: Licht ein oder aus schalten (true/false)
     */
    public void schalteLichtVonLok(int lokAdresse, boolean lichtEin);
}
