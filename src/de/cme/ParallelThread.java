
package de.cme;

import de.cme.dijkstra.Knoten;
import java.util.List;

public class ParallelThread extends Thread {

    private Steuerung eineSteuerung;
    private List<Knoten> weg;
    
    public ParallelThread(Steuerung steuerung) {
        eineSteuerung = steuerung;
    }

    ParallelThread(Steuerung steuerung, List<Knoten> weg) {
        this(steuerung);
        this.weg = weg;
    }

    
    @Override
    public void run() {
        eineSteuerung.sendeRMK(weg);
    }

    
    
}
