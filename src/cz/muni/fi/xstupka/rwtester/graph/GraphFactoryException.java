package cz.muni.fi.xstupka.rwtester.graph;

/**
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphFactoryException extends GraphException {

    public GraphFactoryException() {
    }

    public GraphFactoryException(String message) {
        super(message);
    }

    public GraphFactoryException(Throwable cause) {
        super(cause);
    }

    public GraphFactoryException(String message, Throwable cause) {
        super(message, cause);
    }    
}
