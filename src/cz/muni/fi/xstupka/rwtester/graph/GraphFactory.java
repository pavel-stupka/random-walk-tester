package cz.muni.fi.xstupka.rwtester.graph;

/**
 * Interface for building graphs.
 * This is interface for all classes generating graphs.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public interface GraphFactory {
    
    /**
     * Returns the graph.
     * @return new graph
     */
    public Graph getGraph();
}
