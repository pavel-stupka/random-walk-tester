package cz.muni.fi.xstupka.rwtester.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class generates a random graph.
 * Parameters for generating the graph are number of vertices and the degree.
 * New vertex is added to the graph and is randomly connected to other m 
 * vertices. The propability the vertex will be chosen to be connected to
 * depends on the degree of the vertex.
 * If there are at least m other vertices the variable m equals the 
 * degree, otherwise it equals the number of other vertices.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class ScaleFreeGraphGenerator implements GraphFactory {
    
    private Collection<Vertex> collection;
    private Random random;
    
    /**
     * Creates a new instance of RandomGraphGenerator.
     *
     * @param vertices number of vertices that should be generated
     * @param connect initial degree of the new vertex
     * @throws IllegalArgumentException if a negative parametr is given
     */
    public ScaleFreeGraphGenerator(int vertices, int connect) {
        if (vertices < 0 || connect < 0) {
            throw new IllegalArgumentException("negative parameter");
        }

        collection = new LinkedList<Vertex>();
        random = new Random();
        random.setSeed(hashCode() + System.nanoTime());

        for (int p = 0; p < vertices; p++) {
            List<Vertex> selectedVertices = selectVertices(connect);
            
            Vertex newVertex = new Vertex(p + "");
            
            for (Vertex vertex : selectedVertices) {
                try {
                    newVertex.addNeighbour(vertex);
                    vertex.addNeighbour(newVertex);
                } catch (VertexException ex) {
                    // will never be thrown
                    System.out.println("Exception occured: " + ex.getMessage());
                }
            }
            
            collection.add(newVertex);
        }
    }

    /**
     * Returns a generated graph.
     * @return generated graph
     */
    public Graph getGraph() {
        return new GraphImpl(collection, false, false);
    }

    /**
     * Returns the list of vertices that are chosen randomly
     * each with a probality according to scale-free model of Barabasi.
     * @param connect number of total vertices if possible
     * @return list of randomly chosen vertices
     */
    private List<Vertex> selectVertices(int connect) {
        List<Vertex> result = new LinkedList<Vertex>();
       
        if (collection.size() < connect) {
            connect = collection.size();
        }
        
        for (int p = 0; p < connect; p++) {
            Vertex v;
            do {
                v = getRandomVertex();
            } while (result.contains(v));
            result.add(v);
        }

        return result;
    }
    
    /**
     * Returns a random vertex. The propability that the vertex will be chosen
     * depends on the degree of the vertex i.e. higher degree = higher propability.
     */
    private Vertex getRandomVertex() {
        if (collection.size() == 1) {
            return (Vertex) collection.toArray()[0];
        }
        
        int degreeSum = 0;
        for (Vertex vertex : collection) {
            degreeSum += vertex.getDegree();
        }
        
        int rand = random.nextInt(degreeSum);

        int current = 0;
        for (Vertex vertex : collection) {
            current += vertex.getDegree();
            if (rand < current) {
                return vertex;
            }
        }

        return null;
    }
}

