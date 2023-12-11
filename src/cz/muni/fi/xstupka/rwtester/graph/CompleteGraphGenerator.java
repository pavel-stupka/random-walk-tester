// File: CompleteGraphGenerator.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Tato trida slouzi ke generovani uplnych grafu.
 * Trida implementuje rozhrani <code>GraphFactory</code>.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class CompleteGraphGenerator implements GraphFactory {
    
    private List<Vertex> verticesList;
    
    /** 
     * Vytvori novou instanci tridy CompleteGraphGenerator
     *
     * @param vertices z kolika vrcholu ma byt uplny graf vytvoren
     * @throw IllegalArgumentException pokud je parametr <code>vertices</code> zaporny
     */
    public CompleteGraphGenerator(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices parameter is negative");
        }

        verticesList = new LinkedList<Vertex>();
        
        // postupne vytvori pozadovany pocet vrcholu a spoji kazdy s kazdym
        for (int p = 0; p < vertices; p++) {
            Vertex vertex = new Vertex(p + "");
            
            // napoji novy vrchol na vsechny ostatni
            for (Vertex v : verticesList) {
                try {
                    vertex.addNeighbour(v);
                    v.addNeighbour(vertex);
                } catch (VertexException ex) {
                    // OK, tato vyjimka zde nikdy nevznikne
                    ex.printStackTrace();
                }
            }
            
            // nakonec pridame novy vrchol mezi ostatni
            verticesList.add(vertex);
        }
    }

    /**
     * Vraci vytvoreny uplny graf
     *
     * @return uplny graf
     */
    public Graph getGraph() {
        return new GraphImpl((Collection<Vertex>) verticesList, false, false);
    }
}
