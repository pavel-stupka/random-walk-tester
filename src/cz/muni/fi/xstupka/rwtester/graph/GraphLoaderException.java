package cz.muni.fi.xstupka.rwtester.graph;

/**
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphLoaderException extends GraphFactoryException {

    public GraphLoaderException() {
    }

    public GraphLoaderException(String message) {
        super(message);
    }

    public GraphLoaderException(Throwable cause) {
        super(cause);
    }

    public GraphLoaderException(String message, Throwable cause) {
        super(message, cause);
    }    
}
