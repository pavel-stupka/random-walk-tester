package cz.muni.fi.xstupka.rwtester.graph;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a vertex of the graph.
 * Edges of the graph are implemented by adjacency list of neighbours.
 * Whether the graph will be weighted or not you chose when adding
 * first neighbour by using addNeighbour(Vertex neighbour) or
 * addNeighbour(Vertex neighbour, int weight) method. Vertex also 
 * contains two values -- value A and value B -- accessible
 * via <i>set</i> and <i>get</i> methods. These values can be used 
 * in different kinds of graph algorithms. For example value A can
 * be discover time and value B can be final time when running depth 
 * first search algorithm.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class Vertex {

    private String name;
    private long valueA;
    private long valueB;
    private long length;
    private Vertex parent;
    private int inDegree;

    private int neighboursCount;
    private List<Vertex> neighbours;
    private List<Integer> weights;
    
    /** 
     * Creates a new instance of Vertex.
     * As a parameter this constructor takes a string describing vertex's
     * name. When comparing two vertices by equals method only this name 
     * is used.
     *
     * @param name name of the vertex
     */
    public Vertex(String name) {
        this.name = name;
        length = 0;
        valueA = 0;
        valueB = 0;
        parent = null;
        neighboursCount = 0;
        neighbours = null;
        weights = null;
        inDegree = 0;
    }
    
    /**
     * Returns the name of the vertex.
     * @return vertex's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns value A of the vertex.
     * Value A and value B can be used in different kinds of graph algorithms.
     * For example value A can be discover time and value B can be final time 
     * when running depth first search algorithm.
     *
     * @return vertex's value A
     * @see #setValueA(int valueA)
     * @see #setValueB(int valueB)
     * @see #getValueB()
     */
    public long getValueA() {
        return valueA;
    }
    
    /**
     * Sets value A of the vertex.
     * Value A and value B can be used in different kinds of graph algorithms.
     * For example value A can be discover time and value B can be final time 
     * when running depth first search algorithm.
     *
     * @param valueA value to be set
     * @see #setValueB(int valueB)
     * @see #getValueA()
     * @see #getValueB()
     */
    public void setValueA(long valueA) {
        this.valueA = valueA;
    }

    /**
     * Returns value B of the vertex.
     * Value A and value B can be used in different kinds of graph algorithms.
     * For example value A can be discover time and value B can be final time 
     * when running depth first search algorithm.
     *
     * @return vertex's value B
     * @see #setValueA(int valueA)
     * @see #setValueB(int valueB)
     * @see #getValueA()
     */
    public long getValueB() {
        return valueB;
    }
    
    /**
     * Sets value B of the vertex.
     * Value A and value B can be used in different kinds of graph algorithms.
     * For example value A can be discover time and value B can be final time 
     * when running depth first search algorithm.
     *
     * @param valueB value to be set
     * @see #setValueA(int valueA)
     * @see #getValueA()
     * @see #getValueB()
     */
    public void setValueB(long valueB) {
        this.valueB = valueB;
    }
    
    /**
     * Returns parent of the vertex
     * @return vertex's parent
     */
    public Vertex getParent() {
        return parent;
    }
    
    /**
     * Sets the parent of the vertex
     * @param parent to be set
     */
    public void setParent(Vertex parent) {
        this.parent = parent;
    }
    
    /**
     * Adds a new neighbour.
     * This method adds a new neighbour to the vertex (i.e. new edge
     * to this neighbour). This method should be used for unweighted 
     * graphs only.
     * 
     * @param neighbour new neighbour of the vertex
     * @throws VertexException if there allready are some neighbours with
     * weighted egdes
     * @see #addNeighbour(Vertex neighbour, int weight)
     */
    public void addNeighbour(Vertex neighbour) throws VertexException {
        if (neighbours == null) {
            neighboursCount = 0;
            neighbours = new LinkedList<Vertex>();
            weights = null;
        }

        if (weights != null) {
            throw new VertexException("Graph is direted");
        }

        if (!neighbours.contains(neighbour)) {
            neighbours.add(neighbour);
            neighboursCount++;
        }
    }
    
    /**
     * Adds a new neighbour.
     * This method adds a new neighbour to the vertex (i.e. new edge
     * to this neighbour). This method should be used for weighted 
     * graphs only.
     * 
     * @param neighbour new neighbour of the vertex
     * @param weight weight of this new edge
     * @throws VertexException if there allready are some neighbours with
     * unweighted egdes
     * @see #addNeighbour(Vertex neighbour)
     */
    public void addNeighbour(Vertex neighbour, int weight) throws VertexException {
        if (neighbours == null) {
            neighboursCount = 0;
            neighbours = new LinkedList<Vertex>();
            weights = new LinkedList<Integer>();
        }               
        
        if (weights == null) {
            throw new VertexException("Graph is undireted");
        }

        if (!neighbours.contains(neighbour)) {
            neighbours.add(neighbour);
            weights.add(weight);
            neighboursCount++;
        }
    }

    /**
     * Returns degree of the vertex.
     * On directed graphs this method returns out-degree value.
     *
     * @return degree of the vertex
     */
    public int getDegree() {
        return neighboursCount;
    }
    
    /**
     * Returns in-degree (directed graphs only).
     * @return in-degree
     */
    public int getInDegree() {
        return inDegree;
    }
    
    /**
     * Returns number of vertex's neighbours.
     * @return number of vertex's neighbours
     */
    public int getNumberOfNeighbours() {
        return neighboursCount;
    }

    /**
     * Equals two vertices.
     * @param o vertex to be equaled
     * @return <i>true</i> if the vertices are equal, <i>false</i> otherwise
     */
    public boolean equals(Object o) {
        if (o instanceof Vertex) {
            Vertex foo = (Vertex) o;
            return name.equals(foo.getName());
        }
        return false;
    }
    
    /**
     * Returns hash code of the vertex.
     * @return hash codeof the vertex
     */
    public int hashCode() {
        return name.hashCode();
    }
    
    /**
     * Returns string description of the vertex.
     * @return string description of the vertex (vertex's name)
     */
    public String toString() {
        return name;
    }
    
    /**
     * Returns list of vertex's neighbours.
     * @return list of vertex's neighbours
     * @see #getWeights()
     */
    public List<Vertex> getNeighbours() {
        return neighbours;
    }

    /**
     * Returns list of weights of the edges.
     * @return list of weights of the edges
     * @see #getNeighbours()
     */
    public List<Integer> getWeights() {
        return weights;
    }
    
    /**
     * Sets the length for this vertex
     * @param length new length of this vertex
     */
    public void setLength(long length) {
        this.length = length;
    }
    
    /**
     * Returns the length of this vertex
     * @return the length of this vertex
     */
    public long getLength() {
        return length;
    }
    
    /**
     * Increases in-degree of the vertex.
     */
    protected void increaseInDegree() {
        inDegree++;
    }    
}
