package de.cme.dijkstra;

import java.util.*;

public class Knoten {
    private int name;
    private int pfadgewicht;
    private Knoten vorgaenger;
    private boolean bearbeitet;
    // als Startknoten gesetzt?
    private boolean startknoten;
    
    public Vector<Knoten> nachbarliste = new Vector<Knoten>();

    public Knoten(int name) {
        this.name = name;
        pfadgewicht = Integer.MAX_VALUE;
        vorgaenger = null;
        bearbeitet = false;
    }
    
    public void setStart() {
        this.pfadgewicht = 0;
        //Knoten ist Startknoten
        startknoten = true;
    }

    public int getPfadgewicht() {
        return pfadgewicht;
    }

    public void setPfadgewicht(int pfadgewicht) {
        this.pfadgewicht = pfadgewicht;
    }
    
    public void setVorgaenger(Knoten vorgaenger) {
        this.vorgaenger = vorgaenger;
    }

    public Knoten getVorgaenger() {
        return vorgaenger;
    }

    public boolean isBearbeitet() {
        return bearbeitet;
    }

    public void setBearbeitet(boolean bearbeitet) {
        this.bearbeitet = bearbeitet;
    }
    
    public int getName() {
        return name;
    }
    
}
