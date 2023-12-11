// File: RWManager.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.GraphException;
import cz.muni.fi.xstupka.rwtester.graph.GraphImpl;
import cz.muni.fi.xstupka.rwtester.graph.Vertex;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tato trida je zapouzdrenim pro testovani nahodne prochazky.
 * Jejim prostrednictvim se spousti jednotlive testy, ktere lze s timto
 * programem provadet. Jedna se predevsim o analyzu grafu a testovani nahodne
 * prochazky v rezimech pokryti grafu a nalezeni cesty v grafu.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class RWManager {
    
    private Graph graph;
    private String graphName;
    private int randomWalkMode;
    private boolean discoverMode;
    private Graph averageGraph;
            
    /** 
     * Vytvori novou instanci tridy RWManager 
     *
     * @throws NullPointerException pokud je parametr <code>graph</code> null
     */
    public RWManager(Graph graph) {
        if (graph == null) {
            throw new NullPointerException();
        }
        this.graph = graph;
        graphName = graph.toString();
        setRandomWalkMode(RandomWalk.CLASSIC_MODE);
        setDiscoverMode(false);
    }

    /**
     * Spusti testovani nahodne prochazky v rezimu pokryti grafu.
     * 
     * @param runs kolikrat ma byt nahodna prochazka spustena (t.j. z kolika testu ma byt
     *     vytvoren vysledny prumer)
     * @param startVertex pocatecni vrchol, ze ktereho ma byt nahodna prochazka spustena
     * @param coverage procentualni pokryti grafu, ktereho ma nahodna prochazka dosahnout
     * @throw GraphException pokud vrchol zadaneho jmena v grafu neexistuje
     */
    public RWResult testCover(int runs, String startVertex, int coverage) throws GraphException {
        
        // nejprve zkontrolujeme, jestli je pozadovane procentualni
        // pokryti grafu vubec dosazitelne (pomoci BFS)
        System.out.println("\nChecking reachable vertices - running BFS");
        BFS bfs = new BFS(graph);
        bfs.run(startVertex);
        System.out.println("Reachable/Total vertices: " + bfs.getNumberOfReachedVertices() + "/" + graph.getNumberOfVertices());
        System.out.println("Required/Possible coverage: " + coverage + "/" + bfs.getCoverage() + " (percentual)");
        
        if (coverage > bfs.getCoverage()) {
            System.out.println("FAILED (required coverage is unreachable)");
            return null;
        } else {
            System.out.println("PASSED\n");
        }

        // seznam vysledku jednotlivych nahodnych prochazek
        RWResult[] results = new RWResult[runs];
        
        // zvolime prislusnou nahodnou prochazku podle toho, jedna-li se
        // o orientovany nebo neorientovany graf
        RandomWalk randomWalk;
        if (graph.isDirected()) {
            randomWalk = new DirectedRandomWalk(graph);
            System.out.println("Directed graph: true");
            System.out.println("Selecting DIRECTED random walk algorithm");
        } else {
            randomWalk = new UndirectedRandomWalk(graph);
            System.out.println("Directed graph: false");
            System.out.println("Selecting UNDIRECTED random walk algorithm");
        }
        
        randomWalk.setMode(randomWalkMode);
        randomWalk.setDiscoverMode(discoverMode);
        if (discoverMode) {
            System.out.println("Discover mode ON");
        } else {
            System.out.println("Discover mode OFF");
        }
        System.out.print("Mode: ");
        switch(randomWalkMode) {
            case RandomWalk.CLASSIC_MODE: System.out.println("classic\n"); break;
            case RandomWalk.OUT_DEGREE_MODE: System.out.println("outdegree\n"); break;
            case RandomWalk.REVERSE_OUT_DEGREE_MODE: System.out.println("routdegree\n"); break;
            case RandomWalk.IN_DEGREE_MODE: System.out.println("indegree\n"); break;
            case RandomWalk.REVERSE_IN_DEGREE_MODE: System.out.println("rindegree\n"); break;
            default: System.out.println("UNSPECIFIED\n"); break;
        }
        
        
        System.out.println("start vertex: " + startVertex);
        System.out.println("loops: " + runs);
        System.out.println("coverage: " + coverage + "%");
        System.out.println("");
        
        // vytvorime novy prumerny graf
        averageGraph = new GraphImpl(graph);
        
        // spustime vsechny nahodne prochazky a prubezne alayzujeme ziskana data
        for (int p = 0; p < runs; p++) {
            System.out.print("Running test " + (p+1) + "\t\t");
            randomWalk.runCover(startVertex, coverage);
            System.out.print("Analyzing");
            results[p] = RWAnalyzer.analyze(randomWalk);
            updateAverageGraph();
            System.out.println("\tDONE");
        }
        
        computeAverageGraph(runs);

        // vratime celkovy vysledek jako prumer vsech dosazenych vysledku
        return new RWResult(results);
    }
    
    /**
     * Spusti testovani nahodne prochazky v rezimu hledani cesty k danemu vrcholu
     *
     * @param runs kolikrat ma byt nahodna prochazka spustena (t.j. z kolika testu ma byt
     *     vytvoren vysledny prumer)
     * @param startVertex pocatecni vrchol, ze ktereho ma byt nahodna prochazka spustena
     * @param endVertex koncovy vrchol, ktereho ma nahodna prochazka dosahnout
     * @throw GraphException pokud aspon jeden z vrcholu zadaneho jmena v grafu neexistuje
     */
    public RWResult testFindPath(int runs, String startVertex, String endVertex) throws GraphException {        
        
        // nejprve pomoci BFS zkontrolujeme, zda-li je cilovy vrchol vubec dosazitelny
        System.out.println("\nChecking reachable vertex \"" + endVertex + "\" - running BFS");
        BFS bfs = new BFS(graph);
        bfs.run(startVertex);
        
        if (!bfs.isReachable(endVertex)) {
            System.out.println("FAILED (\"" + endVertex + "\" in unreachable)");
            return null;
        } else {
            System.out.println("PASSED\n");
        }
        
        // seznam vysledku jednotlivych nahodnych prochazek
        RWResult[] results = new RWResult[runs];
        
        // zvolime prislusnou nahodnou prochazku podle toho, jedna-li se
        // o orientovany nebo neorientovany graf
        RandomWalk randomWalk;
        if (graph.isDirected()) {
            randomWalk = new DirectedRandomWalk(graph);
            System.out.println("Directed graph: true");
            System.out.println("Selecting DIRECTED random walk algorithm");
        } else {
            randomWalk = new UndirectedRandomWalk(graph);
            System.out.println("Directed graph: false");
            System.out.println("Selecting UNDIRECTED random walk algorithm");
        }
        
        randomWalk.setMode(randomWalkMode);
        randomWalk.setDiscoverMode(discoverMode);
        if (discoverMode) {
            System.out.println("Discover mode ON");
        } else {
            System.out.println("Discover mode OFF");
        }
        System.out.print("Mode: ");
        switch(randomWalkMode) {
            case RandomWalk.CLASSIC_MODE: System.out.println("classic\n"); break;
            case RandomWalk.OUT_DEGREE_MODE: System.out.println("outdegree\n"); break;
            case RandomWalk.REVERSE_OUT_DEGREE_MODE: System.out.println("routdegree\n"); break;
            case RandomWalk.IN_DEGREE_MODE: System.out.println("indegree\n"); break;
            case RandomWalk.REVERSE_IN_DEGREE_MODE: System.out.println("rindegree\n"); break;
            default: System.out.println("UNSPECIFIED\n"); break;
        }
        
        System.out.println("start vertex: " + startVertex);
        System.out.println("target vertex: " + endVertex);
        System.out.println("loops: " + runs);
        System.out.println("");      
        
        // vytvorime novy prumerny graf
        averageGraph = new GraphImpl(graph);
                
        // spustime vsechny nahodne prochazky a prubezne alayzujeme ziskana data
        for (int p = 0; p < runs; p++) {
            System.out.print("Running test " + (p+1) + "\t\t");
            randomWalk.runFindPath(startVertex, endVertex);
            System.out.print("Analyzing");
            results[p] = RWAnalyzer.analyze(randomWalk);
            updateAverageGraph();
            System.out.println("\tDONE");
        }
        
        computeAverageGraph(runs);
        
        // vratime celkovy vysledek jako prumer vsech dosazenych vysledku
        return new RWResult(results);
    }
    
    /**
     * Upravi prumerny graf
     */
    private void updateAverageGraph() {
        for (Vertex vertex : graph.getVertices()) {
            try {
                Vertex second = averageGraph.getVertex(vertex.getName());
                second.setParent(vertex.getParent());
                second.setValueA(second.getValueA() + vertex.getValueA());
                second.setValueB(second.getValueB() + vertex.getValueB());
            } catch (GraphException ex) {
                System.out.println("Error: " + ex.getMessage());
                System.exit(1);
            }
        }
    }
    
    /**
     * Vypocita vysledek prumeru
     * @param runs pocet testu
     */
    private void computeAverageGraph(int runs) {
        for (Vertex second : averageGraph.getVertices()) {
            second.setValueA(second.getValueA() / runs);
            second.setValueB(second.getValueB() / runs);
        } 
    }
    
    /**
     * Spusti analyzu grafu a vysledky ulozi do textovych souboru
     * definovanych sablonou.
     *
     * @param template sablona textovych souboru pro zapis vysledku analyzy
     */
    public void analyzeGraph(String template) {
        GraphAnalyzer analyzer = new GraphAnalyzer(graph);
        System.out.print("Analyzing graph");
        analyzer.analyze();
        System.out.println("\tDONE");
        System.out.print("Saving results");
        try {
            analyzer.saveResults(template);
        } catch (IOException ex) {
            System.out.print("\tFAILED ");
            System.out.println(ex.getMessage());
            return;
        }
        System.out.println("\tDONE");
    }
    
    // ---------------- GENEROVANO REFACTORINGEM ----------------
    
    public Graph getGraph() {
        return graph;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public int getRandomWalkMode() {
        return randomWalkMode;
    }

    public void setRandomWalkMode(int randomWalkMode) {
        this.randomWalkMode = randomWalkMode;
    }

    public boolean isDiscoverMode() {
        return discoverMode;
    }

    public void setDiscoverMode(boolean discoverMode) {
        this.discoverMode = discoverMode;
    }

    public Graph getAverageGraph() {
        return averageGraph;
    }
}
