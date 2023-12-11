package cz.muni.fi.xstupka.rwtester.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class builds the graph.
 * You can use this class to create a graph manually.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphBuilder implements GraphFactory {
    
    private boolean directed;
    private boolean weighted;
    private Map<String, Vertex> vertices;
    
    /** 
     * Creates a new instance of GraphBuilder.
     * @param directed <i>true</i> whether the graph should be directed
     * @param weighted <i>true</i> whether the graph should be weighted
     */
    public GraphBuilder(boolean directed, boolean weighted) {
        this.directed = directed;
        this.weighted = weighted;
        vertices = new HashMap<String, Vertex>();
    }
    
    /**
     * Adds a new unweighted edge to the graph.
     * If the graph is directed orientation of this new edge is from
     * vertex A to vertex B. If the graph is weighted default
     * weight of the edge is 1.
     *
     * @param vertexA name of the vertex A
     * @param vertexB name of the vertex B
     * @see #addEdge(String vertexA, String vertexB, int weight)
     */
    public void addEdge(String vertexA, String vertexB) {
        if (weighted) {
            addEdge(vertexA, vertexB, 1);
        }

        Vertex a = getVertex(vertexA);
        Vertex b = getVertex(vertexB);

        try {
            if (directed) {
                a.addNeighbour(b);
                b.increaseInDegree();
            } else {
                a.addNeighbour(b);
                b.addNeighbour(a);
            }
        } catch (VertexException e) {
            // this will never happen
        }
    }
    
    /**
     * Adds a new weighted edge to the graph.
     * If the graph is directed orientation of this new edge is from
     * vertex A to vertex B. If the graph is unweighted <i>weight</i>
     * parameter is not used.
     *
     * @param vertexA name of the vertex A
     * @param vertexB name of the vertex B
     * @param weight weight of the edge
     * @see #addEdge(String vertexA, String vertexB)
     */
    public void addEdge(String vertexA, String vertexB, int weight) {
        if (!weighted) {
            addEdge(vertexA, vertexB);
        }

        Vertex a = getVertex(vertexA);
        Vertex b = getVertex(vertexB);

        try {
            if (directed) {
                a.addNeighbour(b, weight);
                b.increaseInDegree();
            } else {
                a.addNeighbour(b, weight);
                b.addNeighbour(a, weight);
            }
        } catch (VertexException e) {
            // this will never happen
        }   
    }
    
    /**
     * Adds a new vertex to the graph.
     * @param name name of the new vertex
     */
    public void addVertex(String name) {
        getVertex(name); // !@#$ hack :)
    }
    
    /**
     * Returns vertex having the given name.
     * If there is no such vertex new vertex is created and then
     * returned.
     *
     * @return vertex with the given name
     */
    private Vertex getVertex(String name) {
        if (!vertices.containsKey(name)) {
            Vertex foo = new Vertex(name);
            vertices.put(name, foo);
            return foo;
        }
        return vertices.get(name);
    }
    
    /**
     * Returns created graph.
     * @return created graph
     */
    public Graph getGraph() {
        Collection<Vertex> bar = (Collection<Vertex>) vertices.values();
        return new GraphImpl(bar, directed, weighted);
    }
}
