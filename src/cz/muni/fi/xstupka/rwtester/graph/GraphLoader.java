package cz.muni.fi.xstupka.rwtester.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class loads a graph from the text file.
 * The text file containing the graph should be this format:
 * <br/>
 * On each line is one of these:
 * <ul>
 *   <li>empty line</li>
 *   <li>comment (beginning with #)</li>
 *   <li>vertex</li>
 *   <li>edge</li>
 * </ul>
 * Example:
 * <br/>
 * <pre>
 * # directed edge from A to B
 * [A] -> [B]
 *
 * # the same
 * [B] <- [A]
 *
 * # undirected edge between A and B with weight 5
 * [A] -- [B] 5
 *
 * # vertex A
 * [A]
 * </pre>
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphLoader implements GraphFactory {
    
    // if we allready know whether the graph is directed / weighted
    private boolean knownStructure;     

    // structure of the graph
    private boolean directed;
    private boolean weighted;
    
    // parser error description
    private String errorDescription;
    
    private GraphBuilder builder;

    /**
     * Creates a new instance of GraphLoader.
     * This constructor loads a graph from the file.
     *
     * @param file file the graph should be loaded from
     * @throws IOException on error when openning the file
     * @throws GraphLoaderException on parse error
     */
    public GraphLoader(File file) throws IOException, GraphLoaderException {
        knownStructure = false;
        errorDescription = "";
        BufferedReader in = new BufferedReader(new FileReader(file));
        Set<String> vertices = new HashSet<String>();
        
        String line = "";
        int lineNo = 1;
        while ((line = in.readLine()) != null) {
            LinePacket packet = parseLine(line);
            
            // if the line is not valid we report error
            if (!packet.isValid()) {
                errorDescription = "Line " + lineNo + ": Syntax missmatch";
                throw new GraphLoaderException("Parse Error (" + errorDescription +")");
            }
            
            // if the parser returned error we report it
            if (packet.getType() == LinePacket.ERROR) {
                errorDescription = "Line " + lineNo + ": Syntax missmatch";
                throw new GraphLoaderException("Parse Error (" + errorDescription +")");             
            }
            
            // if the result is a vertex we add it to the set of vertices
            if (packet.getType() == LinePacket.VERTEX) {
                vertices.add(packet.getVertexA());
            }
            
            // if the result is an edge let's process it:
            if (packet.getType() == LinePacket.EDGE) {                
                if (!knownStructure) {
                    knownStructure = true;
                    if (packet.getDirection().equals("--")) {
                        directed = false;
                    } else {
                        directed = true;
                    }
                    weighted = packet.isWeighted();
                    builder = new GraphBuilder(directed, weighted);
                }
                
                String vertexA = "";
                String vertexB = "";
                
                // edge is weighted graph is not (or vice-versa)
                if (weighted != packet.isWeighted()) {
                    errorDescription = "Line " + lineNo + ": Graph weight";
                    throw new GraphLoaderException("Parse Error (" + errorDescription +")");
                }
                
                if (packet.getDirection().equals("--")) {
                    if (directed) { // edge is not directed but the graph is
                        errorDescription = "Line " + lineNo + ": Graph is directed";
                        throw new GraphLoaderException("Parse Error (" + errorDescription +")");
                    }
                    vertexA = packet.getVertexA();
                    vertexB = packet.getVertexB();
                } else if(packet.getDirection().equals("->")) {
                    if (!directed) {
                        // edge is directed but the graph is not
                        errorDescription = "Line " + lineNo + ": Graph is undirected";
                        throw new GraphLoaderException("Parse Error (" + errorDescription +")");
                    }
                    vertexA = packet.getVertexA();
                    vertexB = packet.getVertexB();                    
                } else if(packet.getDirection().equals("<-")) {
                    if (!directed) {
                        // edge is directed but the graph is not
                        errorDescription = "Line " + lineNo + ": Graph is undirected";
                        throw new GraphLoaderException("Parse Error (" + errorDescription +")");
                    }
                    vertexB = packet.getVertexA();
                    vertexA = packet.getVertexB();
                }

                // finally we add the edge
                if (weighted) {
                    builder.addEdge(vertexA, vertexB, Integer.parseInt(packet.getWeight()));
                } else {
                    builder.addEdge(vertexA, vertexB);
                }
            }
            lineNo++;
        }
        // if there are no edges in the graph we may still add some vertices
        if (builder == null) {
            builder = new GraphBuilder(false, false);
        }
        // let's add loaded vertices
        for (String v : vertices) {
            builder.addVertex(v);
        }                
        in.close();
    }

    /**
     * Returns decription of the last error
     * @return decription of the last error
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Returns loaded graph.
     * @return loaded graph
     */
    public Graph getGraph() {
        return builder.getGraph();
    }
    
    /*
     *  . . . = (white space)*
     *  a a a = (character)+
     *  x x x = (digit)+
     *      E = END OF LINE
     *
     *          E               E
     *          #               < >                       E
     *    . . . [ a a a ] . . . - - . . . [ a a a ] . . . x x x . . . E
     *  1       2       3       4 5       6       7       8     9     
     *
     */
    
    /**
     * Parse line of the source text file and returns decoded data.
     */
    private LinePacket parseLine(String line) {
        
        String vertexA = "";
        String vertexB = "";
        String direction = "";
        String weight = "";

        int state = 1;
        
        for (int p = 0; p < line.length(); p++) {
            char c = line.charAt(p);
            
            //-----------------------------------------------------------------
            //  - - - PARSER START - - -
            //-----------------------------------------------------------------
            switch(state) {
                case 1:
                    if (Character.isWhitespace(c)) {
                        // OK
                    } else if (c == '#') {
                        return new LinePacket(LinePacket.EMPTY);
                    } else if (c == '[') {
                        state = 2;
                    } else {
                        return new LinePacket(LinePacket.ERROR);
                    }
                    break;
                   
                case 2:
                    if (c == ']') {
                        state = 3;
                    } else {
                        vertexA += c;
                    }
                    break;
                    
                case 3:
                    if (Character.isWhitespace(c)) {
                        // OK
                    } else if (c == '-' || c == '<') {
                        direction += c;
                        state = 4;
                    } else {
                        return new LinePacket(LinePacket.ERROR);
                    }                  
                    break;
                    
                case 4:
                    if (c == '-' || c == '>') {
                        direction += c;
                        state = 5;
                    } else {
                        return new LinePacket(LinePacket.ERROR);
                    }
                    break;
                    
                case 5:
                    if (Character.isWhitespace(c)) {
                        // OK
                    } else if (c == '[') {
                        state = 6;
                    } else {
                        return new LinePacket(LinePacket.ERROR);
                    }
                    break;
                    
                case 6:
                    if (c == ']') {
                        state = 7;
                    } else {
                        vertexB += c;
                    }
                    break;
                    
                case 7:
                    if (Character.isWhitespace(c)) {
                        // OK                    
                    } else {
                        weight += c;
                        state = 8;
                    }
                    break;
                    
                case 8:
                    if (Character.isWhitespace(c)) {
                        state = 9;
                    } else {
                        weight += c;
                    }
                    break;
                    
                case 9:
                    if (Character.isWhitespace(c)) {
                        // OK                    
                    } else {
                        return new LinePacket(LinePacket.ERROR);
                    }
            }
            //-----------------------------------------------------------------
            // - - -  PARSER END - - - 
            //-----------------------------------------------------------------
        }
        
        // possible final states
        if (state == 1) {
            return new LinePacket(LinePacket.EMPTY);
        } else if (state == 3) {
            return new LinePacket(vertexA);
        } else if (state == 7) {
            return new LinePacket(vertexA, vertexB, direction);            
        } else if (state == 9 || state == 8) {
            return new LinePacket(vertexA, vertexB, direction, weight);
        }
                
        return new LinePacket(LinePacket.ERROR);
    }
}
