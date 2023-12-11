package cz.muni.fi.xstupka.rwtester.graph;

import java.util.Collection;

/**
 * This interface describes a graph.
 * Almost all classes of this package use only this intarface for
 * processing graphs.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public interface Graph {

    /**
     * Returns information whether the graph is directed or not.
     * @return <i>true</i> if the graph is directed, <i>false</i>
     * otherwise
     */
    public boolean isDirected();

    /**
     * Returns information whether the graph is weighted or not.
     * @return <i>true</i> if the graph is weighted, <i>false</i>
     * otherwise
     */
    public boolean isWeighted();

    /**
     * Returns all vertices of the graph.
     * @return collection of all vertices building the graph
     */
    public Collection<Vertex> getVertices();

    /**
     * Returns appropriate vertex.
     * @param name name of the requested vertex
     * @return appropriate vertex
     * @throws GraphException if there is no vertex with this name
     */
    public Vertex getVertex(String name) throws GraphException;
    
    
    /**
     * Returns number of vertices.
     * @return number of vertices
     */
    public int getNumberOfVertices();

    /**
     * Returns number of edges.
     * @return number of edges
     */
    public int getNumberOfEdges();
}
