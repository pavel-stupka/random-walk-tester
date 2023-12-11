// File: GraphAnalyzer.java
// Doc language: Czech & English

package cz.muni.fi.xstupka.rwtester;

import cz.muni.fi.xstupka.rwtester.graph.Graph;
import cz.muni.fi.xstupka.rwtester.graph.Vertex;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Tato trida slouzi k analyze grafu
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphAnalyzer {

    public final static int DEGREE = 0;
    public final static int IN_DEGREE = 1;
    public final static int OUT_DEGREE = 2;

    private Graph graph;

    private List<String> vertices;
    private List<Integer> degrees;
    private List<Integer> inDegrees;
    private List<Integer> outDegrees;
    
    // vysledna zavislost poctu vrcholu na stupni vrcholu
    // (jestli se graf ridi mocninnym zakonem resp. je-li graf bezskalovy)
    private List<int[]> degreeResults;
    private List<int[]> inDegreeResults;
    private List<int[]> outDegreeResults;

    /** 
     * Vytvori novou instanci tridy GraphAnalyzer.
     *
     * @param graph graf, ktery ma byt analyzovan
     * @throws NullPointerException pokud je parametr <code>graph</code> null
     */
    public GraphAnalyzer(Graph graph) {
        if (graph == null) {
            throw new NullPointerException();
        }
        this.graph = graph;

        vertices = new LinkedList<String>();
        degrees = new LinkedList<Integer>();
        inDegrees = new LinkedList<Integer>();
        outDegrees = new LinkedList<Integer>();
    }
    
    /**
     * Returns the list of vertices.
     * @return list of vertices
     */
    public List<String> getVertices() {
        return vertices;
    }

    /**
     * Returns the list of degrees.
     * On directed graphs this list is the sum of in-degrees and out-degrees values
     * @return list of degrees
     */
    public List<Integer> getDegrees() {
        return degrees;
    }
    
    /**
     * Returns the list of in-degrees of directed graph.
     * @return list of in-degrees
     */
    public List<Integer> getInDegrees() {
        return inDegrees;
    }    
    
    /**
     * Returns the list of out-degrees of directed graph.
     * @return list of out-degrees
     */
    public List<Integer> getOutDegrees() {
        return outDegrees;
    }
    
    /**
     * Returns statistics about degrees.
     * 
     * @param type of required statistics DEGREE, IN_DEGREE, OUT_DEGREE
     * (the last two are for directed graphs only)
     * @return list of integer fields with two numbers, the first one is the degree
     * and the second one is the number of vertices with this degree
     * @throws IllegalArgumentException if the type is not one of: 
     * DEGREE, IN_DEGREE, OUT_DEGREE
     */    
    public List<int[]> getResults(int type) {        
        if (type != DEGREE && type != IN_DEGREE && type != OUT_DEGREE) {
            throw new IllegalArgumentException();
        }
        
        switch (type) {
            case DEGREE:
                return degreeResults;
            case IN_DEGREE:
                return inDegreeResults;
            case OUT_DEGREE:
                return outDegreeResults;
        }
        
        return null;
    }
    
    /**
     * Saves results to files.
     * This method generates text files containing results of statistics.
     * For undirected graphs two files are created: template_deg.txt and
     * template_info.txt. For directed graphs four files are created: 
     * template_[deg/in/out].txt and template_info.txt.
     * 
     * @param template common name for all generated files
     * @throws IOException on error when saving
     */    
    public void saveResults(String template) throws IOException {
        if (graph.isDirected()) {
            saveDeg(new File(template + "_deg.txt"));
            saveIn(new File(template + "_in.txt"));
            saveOut(new File(template + "_out.txt"));
        } else {
            saveDeg(new File(template + "_deg.txt"));
        }
        
        saveInfo(new File(template + "_info.txt"));
    }
    
    /**
     * Generates info about the structure.
     */
    public void analyze() {
        Collection<Vertex> foo = graph.getVertices();
        for (Vertex v : foo) {
            vertices.add(v.getName());
            if (graph.isDirected()) {
                inDegrees.add(v.getInDegree());
                outDegrees.add(v.getDegree());
                degrees.add(v.getDegree() + v.getInDegree());
            } else {
                degrees.add(v.getDegree());
            }
        }
        
        if (graph.isDirected()) {
            degreeResults = generateStatistics(degrees);
            inDegreeResults = generateStatistics(inDegrees);
            outDegreeResults = generateStatistics(outDegrees);
        } else {
            degreeResults = generateStatistics(degrees);
        }        
    }

    /**
     * Generates required statistics.
     */
    private List<int[]> generateStatistics(List<Integer> list) {        
        int min = 0;
        int max = 0;
        boolean started = false;
        // find min and max values
        for (int i : list) {
            if (!started) {
                started = true;
                min = i;
                max = i;
            }
            if (i < min) {
                min = i;
            }
            if (i > max) {
                max = i;
            }
        }
        // computes statistics
        List<int[]> result = new LinkedList<int []>();
        for (int p = min; p <= max; p++) {
            int count = 0;
            for (int i : list) {
                if (i == p) {
                    count++;
                }
            }
            if (count > 0) {
                int []bar = new int[2];
                bar[0] = p;
                bar[1] = count;
                result.add(bar);
            }
        }
        return result;
    }
    
    private void saveInfo(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        
        out.write("GRAPH INFO FILE\n\n");
        out.write("Directed: " + graph.isDirected() + "\n");
        out.write("Weighted: " + graph.isWeighted() + "\n\n");
        out.write("Vertices: " + graph.getNumberOfVertices() + "\n");
        out.write("Edges: " + graph.getNumberOfEdges() + "\n\n");
        
        double ratio = (double) graph.getNumberOfEdges() / graph.getNumberOfVertices();
        
        out.write("Edges / Vertices ratio = " + ratio);
        out.write(" (i.e. average 1 vertex = " + ratio + " edge[s])\n\n");
        
        if (graph.isDirected()) {
            out.write("Degree (min/max): " + getMinDegree(graph, DEGREE) + "/" + getMaxDegree(graph, DEGREE) + "\n");
            out.write("In degree (min/max): " + getMinDegree(graph, IN_DEGREE) + "/" + getMaxDegree(graph, IN_DEGREE) + "\n");
            out.write("Out degree (min/max): " + getMinDegree(graph, OUT_DEGREE) + "/" + getMaxDegree(graph, OUT_DEGREE) + "\n");
        } else {
            out.write("Degree (min/max): " + getMinDegree(graph, DEGREE) + "/" + getMaxDegree(graph, DEGREE) + "\n");
        }
        
        out.close();        
    }

    private void saveDeg(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        List<int[]> st = getResults(DEGREE);
        for (int []p : st) {
            out.write(p[0] + " " + p[1] + "\n");
        }
        out.close();
    }

    private void saveIn(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        List<int[]> st = getResults(IN_DEGREE);
        for (int []p : st) {
            out.write(p[0] + " " + p[1] + "\n");
        }
        out.close();
    }

    private void saveOut(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        List<int[]> st = getResults(OUT_DEGREE);
        for (int []p : st) {
            out.write(p[0] + " " + p[1] + "\n");
        }
        out.close();
    }
    
    /**
     * Vraci vzdalenost vrcholu ktery je nejdale od pocatku
     * startu BFS algoritmu, tedy vzdalenost nejvyssi.
     */
    public static long getMaxLength(Graph graph) {
        Collection<Vertex> vertices = graph.getVertices();
        long max = 0;
        for (Vertex v : vertices) {
            long current = v.getLength();
            if (current > max) {
                max = current;
            }
        }
        return max;
    }
    
    /**
     * Returns min degree of the graph
     * @param graph we ask for
     * @param type DEGREE, IN_DEGREE, OUT_DEGREE
     */
    public static int getMinDegree(Graph graph, int type) {
        Collection<Vertex> vertices = graph.getVertices();
        
        int min = 0;
        
        if (graph.isDirected()) {
            boolean running = false;
            for (Vertex v : vertices) {
                int value;
                if (type == DEGREE) {
                    value = v.getDegree() + v.getInDegree();
                } else if (type == IN_DEGREE) {
                    value = v.getInDegree();
                } else {
                    value = v.getDegree();
                }
                
                if (!running) {
                    min = value;
                    running = true;
                }
                if (value < min) {
                    min = value;
                }
            }
            
        } else {
            boolean running = false;
            for (Vertex v : vertices) {
                if (!running) {
                    min = v.getDegree();
                    running = true;
                }
                if (v.getDegree() < min) {
                    min = v.getDegree();
                }
            }
        }
        
        return min;
    }
    
    /**
     * Returns max degree of the graph
     * @param graph we ask for
     * @param type DEGREE, IN_DEGREE, OUT_DEGREE
     */
    public static int getMaxDegree(Graph graph, int type) {
        Collection<Vertex> vertices = graph.getVertices();
        
        int max = 0;
        
        if (graph.isDirected()) {
            boolean running = false;
            for (Vertex v : vertices) {
                int value;
                if (type == DEGREE) {
                    value = v.getDegree() + v.getInDegree();
                } else if (type == IN_DEGREE) {
                    value = v.getInDegree();
                } else {
                    value = v.getDegree();
                }
                
                if (!running) {
                    max = value;
                    running = true;
                }
                if (value > max) {
                    max = value;
                }
            }
            
        } else {
            boolean running = false;
            for (Vertex v : vertices) {
                if (!running) {
                    max = v.getDegree();
                    running = true;
                }
                if (v.getDegree() > max) {
                    max = v.getDegree();
                }
            }
        }
        
        return max;
    }
}
