package cz.muni.fi.xstupka.rwtester.graph;

import java.io.File;
import java.io.IOException;

/**
 * Interface for saving graphs.
 * This interface is implemented by classes that save graphs in different
 * file formats.
 *
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public interface GraphWriter {
    
    /**
     * Saves a graph to the file
     * @param file file the graph should be saved to
     * @throws IOException on error when saving
     */
    public void write(File file) throws IOException;
}
