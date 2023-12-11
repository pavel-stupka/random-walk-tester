// File: RandomGraphGenerator.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester.graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Tato trida slouzi ke generovani nahodnych grafu.
 * Trida implementuje rozhrani <code>GraphFactory</code>.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class RandomGraphGenerator implements GraphFactory {
    
    private Random random;
    private Graph graph;
    
    /** 
     * Vytvori novou instanci tridy RandomGraphGenerator.
     * Nahodny graf je vytvoren tak, ze se nejprve vytvori vsechny vrcholy
     * a pak jsou mezi nimy nahodne vytvareny hrany podle pozadovaneho poctu.
     * 
     * @param vertices z kolika vrcholu se ma graf skladat
     * @param edges z kolika hran se ma graf skladat
     * @throw IllegalArgumentException pokud je parametr <code>vertices</code> zaporny
     * @throw IllegalArgumentException pokud je parametr <code>edges</code> zaporny
     */
    public RandomGraphGenerator(int vertices, int edges) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices parameter is negative");
        }
        if (edges < 0) {
            throw new IllegalArgumentException("edges parameter is negative");
        }
        
        random = new Random();
        random.setSeed(hashCode() + System.nanoTime());
        
        GraphBuilder builder = new GraphBuilder(false, false);
        
        // pridame vsechny vrcholy
        for (int p = 0; p < vertices; p++) {
            builder.addVertex(p + "");
        }

        if (vertices > 1) {
            Set<String> setVertices = new HashSet<String>();
            int currentEdges = 0;
            int maxEdges = getMaxEdges(vertices);
            
            if (edges > maxEdges) {
                edges = maxEdges;
            }
            
            // vytvori pozadovany pocet nahodnych hran
            while (currentEdges != edges) {
                String edge = selectTwoUniqueNumbers(vertices);
                if (!setVertices.contains(edge)) {
                    setVertices.add(edge);
                    currentEdges++;
                }
            }

            // z mnoziny hran vytvori hrany grafu
            for (String line : setVertices) {
                String[] data = line.split("x");
                builder.addEdge(data[0], data[1]);
            }
        }
        
        graph = builder.getGraph();
    }
    
    /**
     * Vrati maximalni pocet hran.
     * Pocita kombinacni cislo (vertices 'nad' 2), tedy pocet hran
     * uplneho grafu na 'vertices' vrcholech.
     */
    private int getMaxEdges(int vertices) {
        if (vertices == 1) {
            return 0;
        }
        
        int foo = vertices - 2;
        int result = 1;
        
        for (int p = vertices; p > foo; p--) {
            result = result * p;
        }
        
        return result / 2;
    }
    
    /**
     * Vrati dvojici rozdilnych nahodnych cisel jako string "cislo_1xcislo_2".
     * Prvni cislo je to mensi.
     */
    private String selectTwoUniqueNumbers(int size) {
        int a = random.nextInt(size);
        int b = random.nextInt(size);
        
        while (a == b) {
            b = random.nextInt(size);
        }
        
        String result = "";
        
        if (a < b) {
            result = a + "x" + b;
        } else {
            result = b + "x" + a;
        }
        
        return result;
    }

    /**
     * Vraci vysledny nahodny graf
     *
     * @return nahodny graf
     */
    public Graph getGraph() {
        return graph;
    }
}
