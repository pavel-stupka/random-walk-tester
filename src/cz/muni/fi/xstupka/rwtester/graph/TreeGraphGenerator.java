// File: TreeGraphGenerator.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Tato trida slouzi ke generovani n-arnich stromu.
 * Trida implementuje rozhrani <code>GraphFactory</code>.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class TreeGraphGenerator implements GraphFactory {
    
    private List<Vertex> verticesList;

    /** 
     * Vytvori novou instanci tridy TreeGraphGenerator
     *
     * @param depth hloubka stromu (napr. 0 = pouze koren)
     * @param arity arita stromu (napr. 2 = binarni strom)
     * @throw IllegalArgumentException pokud je parametr <code>depth</code> zaporny
     * @throw IllegalArgumentException pokud je parametr <code>arity</code> mensi nez 1
     */
    public TreeGraphGenerator(int depth, int arity) {
        if (depth < 0) {
            throw new IllegalArgumentException("depth parameter is negative");
        }
        if (arity < 1) {
            throw new IllegalArgumentException("arity parameter is lower than 1");
        }
        
        verticesList = new LinkedList<Vertex>();
        
        // seznam seznamu vrcholu
        // seznam na pozici 0 obsahuje pouze korem, seznam na pozici 1
        // obsahuje vrcholy, ktere jsou primymi potomky korene, atd...
        List<List<Vertex>> vertices = new LinkedList<List<Vertex>>();
        
        // citac vrcholu
        int counter = 0;
        
        // vytvori vsechny seznamy podle pozadovane hloubky 
        for (int p = 0; p <= depth; p++) {
            List<Vertex> levelList = new LinkedList<Vertex>();
            
            // pokud p == 0, vytvarime koren
            if (p == 0) {
                Vertex vertex = new Vertex(counter + "");
                counter++;
                levelList.add(vertex);
                verticesList.add(vertex);
            } else {
                // ke kazdemu vrcholu z predchozi urovne pridame tolik novych
                // vrcholu, kolik udava arita
                List<Vertex> levelUp = vertices.get(p-1);
                
                for (Vertex v : levelUp) {
                    for (int q = 0; q < arity; q++) {
                        
                        Vertex vertex = new Vertex(counter + "");
                        counter++;
                        levelList.add(vertex);
                        verticesList.add(vertex);
                        
                        try {
                            v.addNeighbour(vertex);
                            vertex.addNeighbour(v);
                        } catch (VertexException ex) {
                            // OK, tato vyjimka nikdy nenastane
                            ex.printStackTrace();
                        }
                    }
                }
            }
            
            vertices.add(levelList);
        }
    }
    
    /**
     * Vraci vytvoreny n-arni strom s korenem "0"
     *
     * @return n-arni strom s korenem "0"
     */
    public Graph getGraph() {
        return new GraphImpl((Collection<Vertex>) verticesList, false, false);
    }
}
