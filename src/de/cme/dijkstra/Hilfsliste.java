package de.cme.dijkstra;

public class Hilfsliste {

    private Knoten[] liste;
    private int anzahl;
    private int maxAnzahl;

    public Hilfsliste(int groesse) {
        liste = new Knoten[groesse];
        anzahl = 0;
        maxAnzahl = groesse;
    }

    public boolean add(Knoten k) {
        if (anzahl < maxAnzahl) {
            liste[anzahl] = k;
            anzahl++;
            return true;
        }
        return false;
    }

    public void sortieren() {
        int laenge = maxAnzahl;
        while (laenge > 0) {
            int groesstes = 0;
            for (int i = 0; i < laenge; i++) {
                if (liste[i].getPfadgewicht() > groesstes) {
                    groesstes = liste[i].getPfadgewicht();
                    swap(i, laenge - 1);
                }
            }
            laenge--;
        }
    }

    private void swap(int knoten1, int knoten2) {
        Knoten tmp = liste[knoten2];
        liste[knoten2] = liste[knoten1];
        liste[knoten1] = tmp;
    }

    public boolean allesBearbeitet() {
        boolean bearbeitet = true;
        for (int i = 0; i < maxAnzahl; i++) {
            if (liste[i].isBearbeitet() == false) {
                bearbeitet = false;
            }
        }
        return bearbeitet;
    }

    public Knoten ersterUnbearbeiteter() {
        for (int i = 0; i < maxAnzahl; i++) {
            if(liste[i].isBearbeitet() == false) {
                return liste[i];
            }
        }
        return liste[0];
    }
    
    public Knoten[] getListe() {
        return liste;
    }

}
