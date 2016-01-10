
package de.cme.dijkstra.test;

/**
 *
 * @author Pascal
 */
public class TestingThread extends Thread {

    private DijkstraTest dijkstraTest = new DijkstraTest();
    
    @Override
    public void run() {
        dijkstraTest.testeGraph();
    }
    
}
