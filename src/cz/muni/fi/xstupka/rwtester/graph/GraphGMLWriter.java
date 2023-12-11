// File: GraphGMLWriter.java
// Doc language: Czech

package cz.muni.fi.xstupka.rwtester.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Trida pro zapis grafu do souboru ve formatu GML -- Graph Modelling Language
 * 
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphGMLWriter implements GraphWriter {

    private Graph graph;
    
    /**
     * Vytvori instanci s nastavenim grafu pro zapsani
     * @param graph pro zapsani do souboru
     * @throws NullPointerException pokud je parametr <i>graph</i> null
     */
    public GraphGMLWriter(Graph graph) {
        if (graph == null) {
            throw new NullPointerException("GraphGMLWriter constructor: graph is null");
        }
        this.graph = graph;
    }
    
    /**
     * Ulozi graf do souboru
     * @param file soubor, do ktereho se ma graf ulozit
     * @throws IOException pri chybe behem ukladani
     */
    public void write(File file) throws IOException {
        FileWriter out = new FileWriter(file);
        
        String head = "";
        if (graph.isDirected()) {
            head = "graph [\n    directed 1\n";
        } else {
            head = "graph [\n";
        }
        out.write(head);
        
        writeVertices(out);
        
        // vypise podle typu orientace grafu
        if (graph.isDirected()) {
            writeDirected(out);
        } else {
            writeUndirected(out);
        }
        
        out.write("]\n");
        out.close();
    }
    
    /**
     * Do zadaneho souboru vypise vrcholy
     * @file soubor, do ktereho se maji vypsat vrcholy
     */
    private void writeVertices(FileWriter out) throws IOException {
        Collection<Vertex> vertices = graph.getVertices();
        
        for (Vertex vertex : vertices) {
            String line  = "\n";
                   line += "    node [\n";
                   line += "        name \"" + vertex.getName() + "\"\n";
                   line += "        label \"" + vertex.getName() + "\"\n";
                   line += "        graphics [\n";
                   line += "            type \"ellipse\"\n";
                   line += "        ]\n";
                   line += "    ]\n";
            out.write(line);
        }
    }
    
    /**
     * Vypise orientovany graf
     * @param out soubor, do ktereho se ma vypisovat
     */
    private void writeDirected(FileWriter out) throws IOException {
        Collection<Vertex> vertices = graph.getVertices();
        
        for (Vertex vertex : vertices) {
            List<Vertex> neighbours = vertex.getNeighbours();
            List<Integer> weights = vertex.getWeights();
            
            if (neighbours == null) {
                continue;
            }
            
            // projdeme vsechny sousedy a vypiseme prislusne hrany
            for (int p = 0; p < vertex.getNumberOfNeighbours(); p++) {
                Vertex neighbour = neighbours.get(p);
                
                String line  = "\n";
                       line += "    edge [\n";
                       line += "        source \"" + vertex.getName() + "\"\n";
                       line += "        target \"" + neighbour.getName() + "\"\n";
                       
                       if (graph.isWeighted()) {
                       line += "        label \"" + weights.get(p) + "\"\n";
                       }
                       
                       line += "        graphics [\n";
                       line += "            arrow \"last\"\n";
                       line += "        ]\n";
                       line += "    ]\n";

                out.write(line);
            }
        }
    }
    
    /**
     * Vypise neorientovany graf
     * @param out soubor, do ktereho se ma vypisovat
     */
    private void writeUndirected(FileWriter out) throws IOException {
        Collection<Vertex> vertices = graph.getVertices();
        Set<String> lines = new HashSet<String>();
        
        for (Vertex vertex : vertices) {
            List<Vertex> neighbours = vertex.getNeighbours();
            List<Integer> weights = vertex.getWeights();
            
            /*
             * neorientovana hrana A--B je to same jako neorientovana hrana B--A
             */
            for (int p = 0; p < vertex.getNumberOfNeighbours(); p++) {
                Vertex neighbour = neighbours.get(p);
                String line1 = "[" + vertex.getName() + "] -- [" + neighbour.getName() + "]";
                String line2 = "[" + neighbour.getName() + "] -- [" + vertex.getName() + "]";
                if (graph.isWeighted()) {
                    line1 += " " + weights.get(p) + "\n";
                    line2 += " " + weights.get(p) + "\n";
                } else {
                    line1 += "\n";
                    line2 += "\n";
                }

                // pokud jsme jiz hranu nevypsali, vypiseme ji
                if (!lines.contains(line2)) {
                    lines.add(line1);
                    String line  = "\n";
                       line += "    edge [\n";
                       line += "        source \"" + vertex.getName() + "\"\n";
                       line += "        target \"" + neighbour.getName() + "\"\n";
                       
                       if (graph.isWeighted()) {
                       line += "        label \"" + weights.get(p) + "\"\n";
                       }

                       line += "    ]\n";

                    out.write(line);
                }
            }
        }
    }
}
