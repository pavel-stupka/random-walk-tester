// File: UnirectedRandomWalk.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.GraphException;
import cz.muni.fi.xstupka.rwtester.graph.Vertex;
import java.util.List;

/**
 * Trida realizujici nahodnou prochazku na neorientovanych grafech
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class UndirectedRandomWalk extends RandomWalk {

    private boolean stop;
    private int coverage;
    
    /**
     * Vytvori novou instanci tridy RandomWalk
     *
     * @param graph graf, ktery ma byt pro nahodnou prochazku pouzit
     * @throws NullPointerException pokud je parametr <code>graph</code> null
     */
    public UndirectedRandomWalk(Graph graph) {
        super(graph);
    }
    
    /**
     * Spousti nahodnou prochazku pro pokryti grafu.
     * Mod pokryti grafu znamena, ze nahodna prochazka bude aktivni do te doby,
     * nez bude aspon jednou navstiven urcity pocet vrcholu. Procentualni pokryti
     * pak urcuje tuto hranici zastaveni nahodne prochazky.
     *
     * @param startVertex pocatecni vrchol, ze ktereho ma byt nahodna prochazka spustena
     * @param coverage procentualni pokryti, ktere ma byt dosazeno (v procentech)
     * @throws GraphException pokud vrchol zadaneho jmena v grafu neexistuje
     */
    public void runCover(String startVertex, int coverage) throws GraphException {
        Vertex v = getGraph().getVertex(startVertex);
        initGraph();
        type = "U-CO";
        
        this.coverage = coverage;
        stop = false;

        while (visitedVertices != vertices) {
            processVertex(v);
            if (visitedVertices == vertices || stop) {
                break;
            }
            Vertex next = getRandomNeighbour(v);
            next.setParent(v);
            v = next;
        }
        
        updatePercentageCover();
    }

    /**
     * Spousti nahodnou prochazku k nalezeni cesty k zadanemu vrcholu.
     * Princip tohoto modu nahodne prochazky spociva v tom, ze nahodna prochazka je
     * aktivni do te doby, nez je objeven zadany cilovy vrchol.
     *
     * @param startVertex pocatecni vrchol, ze ktereho ma byt nahodna prochazka spustena
     * @param endVertex koncovy vrchol, ktereho ma nahodna prochazka dosahnout
     * @throws GraphException pokud aspon jeden z vrcholu zadaneho jmena v grafu neexistuje
     */
    public void runFindPath(String startVertex, String endVertex) throws GraphException {
        Vertex v = getGraph().getVertex(startVertex);
        Vertex u = getGraph().getVertex(endVertex);
        initGraph();        
        type = "U-FP";
        
        if (v.equals(u)) {
            processVertex(v);
            updatePercentageCover();
            return;
        }
        
        while (!v.equals(u)) {
            processVertex(v);
                        
            // ziska seznam sousedu
            if (isDiscoverMode()) {
                List<Vertex> neighbours = v.getNeighbours();
                if (neighbours != null) {
                    for (Vertex vertex : neighbours) {
                        if (vertex.equals(u)) {
                            break;
                        }
                    }
                }
            }
            
            Vertex next = getRandomNeighbour(v);
            next.setParent(v);
            v = next;
        }
        
        updatePercentageCover();
    }    
    
    /**
     * Zpracuje dany vrchol
     * 
     * @param v vrchol, ktery ma byt v danem momenta zpracovan
     */
    private void processVertex(Vertex v) {
        // nalezen novy doposud neobjeveny vrchol
        if (v.getValueB() == INFINITY) {
            v.setValueB(time);
            visitedVertices++;
        }
        
        long visits = v.getValueA();
        v.setValueA(visits + 1);  
        
        // zpracuje sousedy pokud je zapnut discover mod
        if (isDiscoverMode()) {
            processVertexNeighbours(v);
        }

        time++;
        
        if (isVerbose()) {
            if (time % getVerboseTime() == 0) {    
                int coverage = (int) (((double) visitedVertices / (double) vertices) * 100);
                System.out.println(type + " time: " + time + " vertices: " + visitedVertices + " / " + vertices + " coverage: " + coverage + "%");
            }
        }
        
        int foo = (int) (((double) visitedVertices / (double) vertices) * 100.0);
        if (percentageCover[foo] == 0) {
            percentageCover[foo] = time;
        }
        
        if (foo >= coverage) {
            stop = true;
        }
    }
    
    /**
     * Zpracuje sousedy daneho vrcholu
     * @param vertex vrchol jehoz sousedi maji byt zpracovany
     */
    private void processVertexNeighbours(Vertex vertex) {
        // ziska seznam sousedu
        List<Vertex> neighbours = vertex.getNeighbours();
        if (neighbours == null) {
            return;
        }        
        
        // projde a zkontroluje sousedy
        for (Vertex neighbour : neighbours) {
            if (neighbour.getValueB() == INFINITY) {
                neighbour.setValueB(time);
                visitedVertices++;
            }
            
            long visits = neighbour.getValueA();
            neighbour.setValueA(visits + 1);  
        }
        
        // test na pokryti
        int foo = (int) (((double) visitedVertices / (double) vertices) * 100.0);
        if (percentageCover[foo] == 0) {
            percentageCover[foo] = time;
        }
        
        if (foo >= coverage) {
            stop = true;
        }
    }
}
