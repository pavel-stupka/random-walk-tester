package cz.muni.fi.xstupka.rwtester.graph;

/**
 * This class holds information about decoded line data when loading a graph.
 * This class is used by GraphLoader class when parsing an input file.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class LinePacket {
    
    // type of parsed line
    public static final int ERROR = 0;
    public static final int VERTEX = 1;
    public static final int EDGE = 2;
    public static final int EMPTY = 3; // empty or comment (#...)

    private int type;
    private String direction;
    private String weight;
    private String vertexA;
    private String vertexB;    
    private boolean weighted;

    /** 
     * Creates a new instance of LinePacket.
     */
    public LinePacket(int type) {
        this.type = type;        
    }

    /** 
     * Creates a new instance of LinePacket.
     */
    public LinePacket(String vertexA) {
        type = VERTEX;
        this.vertexA = vertexA;
    }

    /** 
     * Creates a new instance of LinePacket.
     */
    public LinePacket(String vertexA, String vertexB, String direction) {
        type = EDGE;
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.direction = direction;
        weight = "N/A";
        weighted = false;
    }
    
    /** 
     * Creates a new instance of LinePacket.
     */
    public LinePacket(String vertexA, String vertexB, String direction, String weight) {
        type = EDGE;
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.direction = direction;
        this.weight = weight;
        weighted = true;
    }
    
    /**
     * Returns the type of the parsed line.
     */
    public int getType() {
        return type;
    }
    
    /**
     * Returns vertex A if the line contains an edge
     */
    public String getVertexA() {
        return vertexA;
    }
    
    /**
     * Returns vertex B if the line contains an edge
     */
    public String getVertexB() {
        return vertexB;
    }
    
    /**
     * Returns orientation of the edge.
     * @return orientation of the edge (A, B): <code>--</code>, <code>-></code>
     * or <code>&lt;-</code>
     */
    public String getDirection() {
        return direction;
    }
    
    /**
     * Returns weight of the edge.
     */
    public String getWeight() {
        return weight;
    }
    
    /**
     * Returns information whether the edge is directed or not.
     */
    public boolean isWeighted() {
        return weighted;
    }
    
    /**
     * Checks line's validity.
     */
    public boolean isValid() {        
        if (type == ERROR || type == EMPTY) {
            return true;
        } else if (type == VERTEX) {
            if (vertexA.length() == 0) {
                return false;
            }
            return true;
        }        
        if (vertexA.length() == 0 || vertexB.length() == 0 || direction.length() == 0 || weight.length() == 0) {
            return false;
        }        
        if (direction.equals("<>")) {
            return false;
        }        
        if (weighted) {
            try {
                int foo = Integer.parseInt(weight);
            } catch (NumberFormatException e) {
                return false;
            }
        }        
        return true;
    }
}
