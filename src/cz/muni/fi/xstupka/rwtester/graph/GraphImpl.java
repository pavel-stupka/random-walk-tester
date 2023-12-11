package cz.muni.fi.xstupka.rwtester.graph;

import cz.muni.fi.xstupka.rwtester.RandomWalk;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Graph interface.
 * This is the main class for using graphs in this package.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphImpl implements Graph {

    private boolean directed;
    private boolean weighted;
    private Map<String, Vertex> vertices;
    
    private int numberOfVertices;
    private int numberOfEdges;

    /**
     * Copy constructor. Creates a new instance of GraphImpl by copying
     * the given graph.
     * @param graph source graph to be copied
     */
    public GraphImpl(Graph graph) {
        
        GraphBuilder builder = new GraphBuilder(graph.isDirected(), graph.isWeighted());
        
        for (Vertex vertex : graph.getVertices()) {
            builder.addVertex(vertex.getName());
            if (vertex.getNeighbours() != null) {
                for (int p = 0; p < vertex.getNeighbours().size(); p++) {
                    if (graph.isWeighted()) {
                        builder.addEdge(vertex.getName(), vertex.getNeighbours().get(p).getName(), vertex.getWeights().get(p));
                    } else {
                        builder.addEdge(vertex.getName(), vertex.getNeighbours().get(p).getName());
                    }
                }
            }
        }
        
        this.directed = builder.getGraph().isDirected();
        this.weighted = builder.getGraph().isWeighted();
        
        this.vertices = new HashMap<String, Vertex>();
        
        for (Vertex v : builder.getGraph().getVertices()) {
            v.setValueA(0);                     // pocet navstiveni
            v.setValueB(RandomWalk.INFINITY);   // cas prvniho pristupu
            v.setParent(null);                  // predek vrcholu
            this.vertices.put(v.getName(), v);
        }
        
        numberOfVertices = builder.getGraph().getVertices().size();
        numberOfEdges = computeNumberOfEdges();
    }
    
    /**
     * Creates a new instance of GraphImpl.
     * @param vertices collection of vertices to build the graph
     * @param directed whether the graph should be directed or not
     * @param weighted whether the graph should be weighted or not
     * @throws NullPointerException if the <i>vertices</i> parameter in null
     */
    public GraphImpl(Collection<Vertex> vertices, boolean directed, boolean weighted) {
        if (vertices == null) {
            throw new NullPointerException("GraphImpl constructor: vertices is null");
        }
        
        this.directed = directed;
        this.weighted = weighted;
        
        this.vertices = new HashMap<String, Vertex>();
        
        for (Vertex v : vertices) {
            this.vertices.put(v.getName(), v);
        }
        
        numberOfVertices = vertices.size();
        numberOfEdges = computeNumberOfEdges();
    }
    
    /**
     * Returns information whether the graph is directed or not.
     * @return <i>true</i> if the graph is directed, <i>false</i>
     * otherwise
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * Returns information whether the graph is weighted or not.
     * @return <i>true</i> if the graph is weighted, <i>false</i>
     * otherwise
     */
    public boolean isWeighted() {
        return weighted;
    }

    /**
     * Returns all vertices of the graph.
     * @return collection of all vertices building the graph
     */
    public Collection<Vertex> getVertices() {
        return (Collection<Vertex>) vertices.values();
    }

    /**
     * Returns appropriate vertex.
     * @param name name of the requested vertex
     * @return appropriate vertex
     * @throws GraphException if there is no vertex with this name
     */
    public Vertex getVertex(String name) throws GraphException {        
        if (!vertices.containsKey(name)) {
            throw new GraphException("No vertex '" + name + "' found");
        }
        
        return vertices.get(name);
    }
    
    /**
     * Returns number of vertices.
     * @return number of vertices
     */
    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    /**
     * Returns number of edges.
     * @return number of edges
     */
    public int getNumberOfEdges() {
        return numberOfEdges;
    }
    
    /**
     * Computes number of edges.
     * @return number of edges
     */
    private int computeNumberOfEdges() {
        int result = 0;
        Collection<Vertex> bar = (Collection<Vertex>) vertices.values();        

        for (Vertex v : bar) {
            result += v.getDegree();
        }                
        if (directed) {
            return result;
        }
        
        return result / 2;    // undirected graph
    }
}
