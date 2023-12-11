// File: BFS.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.GraphException;
import cz.muni.fi.xstupka.rwtester.graph.Vertex;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Tato trida je implementaci algoritmu prohledavani grafu do sirky.
 * Tato trida slouzi predevsim ke kontrole, jake procento z celkoveho
 * poctu vrcholu grafu je dosazitelne z nejakeho pocatecniho vrcholu.
 * Navic tato trida kazdemu vrcholu nastavi atribut <code>length</code>,
 * ktery je dostupny pres metodu <code>getLength</code> tridy <code>Vertex</code>.
 * Tato hodnota udava vzdalenost vrcholu od pocatku prohledavani grafu do sirky
 * a je vyuzivana k analyze nahodne prochazky, ktera muze byt spustena
 * az pote, kdy je tato hodnota <code>length</code> nastavena.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class BFS {

    public static final int INFINITY = -1;  // definice nekonecna
    private Graph graph;
    private int reachedVertices;
    
    /**
     * Vytvori novou instanci tridy BFS
     *
     * @param graph graf, ktery ma byt pouzit
     * @throws NullPointerException pokud je parametr <code>graph</code> null
     */
    public BFS(Graph graph) {
        if (graph == null) {
            throw new NullPointerException();
        }        
        this.graph = graph;
    }

    /**
     * Spusti algoritmus ze zadaneho vrcholu
     *
     * @param vertex pocatecni vrchol algoritmu
     * @throws GraphException pokud v grafu neexistuje vrchol daneho jmena
     */    
    public void run(String vertex) throws GraphException {
        initGraph();
        Vertex s = graph.getVertex(vertex);
        s.setLength(0);
        
        Queue<Vertex> queue = new LinkedList<Vertex>();
        queue.offer(s);
        
        reachedVertices = 1;
        
        while (!queue.isEmpty()) {
            Vertex u = queue.poll();
            List<Vertex> list = u.getNeighbours();
            if (list == null) {
                continue;
            }
            for (Vertex v : list) {
                if (v.getLength() == INFINITY) {
                    v.setLength(u.getLength() + 1);
                    v.setParent(u);
                    queue.offer(v);
                    reachedVertices++;
                }
            }
        }
    }
    
    /**
     * Vraci pocet objevenych vrcholu
     *
     * @return pocet objevenych vrcholu
     */
    public int getNumberOfReachedVertices() {
        return reachedVertices;
    }
    
    /**
     * Vraci procentualni pokryti grafu. 
     * Tzn. (pocet objevenych vrcholu / pocet vsech vrcholu) * 100
     *
     * @return procentualni pokryti grafu
     */
    public int getCoverage() {
        double foo = ((double)reachedVertices / (double)graph.getNumberOfVertices()) * 100;
        return (int) foo;
    }
    
    /**
     * Vraci informaci o tom, jestli je graf souvisly.
     * Tedy jestli je kazdy vrchol dosazitelny.
     *
     * @return <code>true</code> pokud je kazdy vrchol v grafu dosazitelny
     */
    public boolean isConnected() {
        if (reachedVertices == graph.getNumberOfVertices()) {
            return true;
        }
        return false;
    }
    
    /**
     * Vraci informaci o tom, zda-li je dany vrchol dosazitelny.
     *
     * @param vertex pozadovany vrchol, na ktery se ptame
     * @return <code>true</code> pokud je zadany vrchol dosazitelny
     * @throws GraphException pokud v grafu neexistuje vrchol daneho jmena
     */
    public boolean isReachable(String vertex) throws GraphException {
        Vertex s = graph.getVertex(vertex);
        if (s.getLength() != INFINITY) {
            return true;
        }
        return false;
    }

    /**
     * Inicializuje graf pred spustenim algoritmu
     */
    private void initGraph() {
        Collection<Vertex> vertices = graph.getVertices();
        for (Vertex v : vertices) {
            v.setParent(null);
            v.setLength(INFINITY);
        }
    }
}
