package cz.muni.fi.xstupka.rwtester.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class saves the graph to the text file.
 * Format of the text file respects format that is described
 * by GraphLoader class.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphTextWriter implements GraphWriter {
    
    private Graph graph;
    
    /** 
     * Creates a new instance of GraphTextWriter.
     * @param graph graph that should be saved
     * @throws NullPointerException if the <i>graph</i> parametr is null
     */
    public GraphTextWriter(Graph graph) {
        if (graph == null) {
            throw new NullPointerException("GraphTextWriter constructor: graph is null");
        }
        this.graph = graph;
    }

    /**
     * Saves the graph.
     * @param file file the graph should be saved to.
     * @throws IOException on error when saving
     */
    public void write(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        
        String line;
                
        line = "# Directed: " + graph.isDirected() + "\n" +
               "# Weighted: " + graph.isWeighted() + "\n" +
               "# Vertices: " + graph.getNumberOfVertices() + "\n" +
               "# Edges: " + graph.getNumberOfEdges() + "\n\n";
        
        out.write(line);
        
        if (graph.isDirected()) {
            writeDirected(out);
        } else {
            writeUndirected(out);
        }
                       
        out.close();
    }    
    
    /**
     * Writes directed graph
     */
    private void writeDirected(FileWriter out) throws IOException {
        Collection<Vertex> vertices = graph.getVertices();
        Set<String> lines = new HashSet<String>();
        
        for (Vertex v : vertices) {
            List<Vertex> neighbours = v.getNeighbours();
            List<Integer> weights = v.getWeights();

            // if the vertex has no neighbours only this vertex is written
            if (v.getNumberOfNeighbours() == 0) {
                out.write("[" + v.getName() + "]\n");
            }
            
            // we write all vertex's neighbours
            for (int p = 0; p < v.getNumberOfNeighbours(); p++) {
                Vertex u = neighbours.get(p);
                String line = "[" + v.getName() + "] -> [" + u.getName() + "]";
                if (graph.isWeighted()) {
                    line += " " + weights.get(p) + "\n";
                } else {
                    line += "\n";
                }
                lines.add(line);
            }
        }
        
        // all lines are written to the file
        printLines(lines, out);
    }

    /**
     * Writes undirected graph
     */
    private void writeUndirected(FileWriter out) throws IOException {
        Collection<Vertex> vertices = graph.getVertices();
        Set<String> lines = new HashSet<String>();
        
        for (Vertex v : vertices) {
            List<Vertex> neighbours = v.getNeighbours();
            List<Integer> weights = v.getWeights();
            
            // if the vertex has no neighbours only this vertex is written
            if (v.getNumberOfNeighbours() == 0) {
                out.write("[" + v.getName() + "]\n");
            }

            /*
             * undirected edge A--B is the same as undirected edge B--A
             */
            for (int p = 0; p < v.getNumberOfNeighbours(); p++) {
                Vertex u = neighbours.get(p);
                String line1 = "[" + v.getName() + "] -- [" + u.getName() + "]";
                String line2 = "[" + u.getName() + "] -- [" + v.getName() + "]";
                if (graph.isWeighted()) {
                    line1 += " " + weights.get(p) + "\n";
                    line2 += " " + weights.get(p) + "\n";
                } else {
                    line1 += "\n";
                    line2 += "\n";
                }

                if (!lines.contains(line2)) {
                    lines.add(line1);
                }
            }
        }
        
        // all lines are written to the file
        printLines(lines, out);
        
    }
    
    /**
     * Prints all lines to the file
     */
    private void printLines(Set<String> lines, FileWriter out) throws IOException {
        
        List<String> list = new ArrayList<String>((Collection<String>) lines);
        Collections.sort(list);
        
        for (String line : list) {
            out.write(line);
        }
    }
}
