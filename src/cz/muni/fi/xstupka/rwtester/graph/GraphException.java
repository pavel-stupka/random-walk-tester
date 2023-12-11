package cz.muni.fi.xstupka.rwtester.graph;

/**
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class GraphException extends Exception {

    public GraphException() {
    }

    public GraphException(String message) {
        super(message);
    }

    public GraphException(Throwable cause) {
        super(cause);
    }

    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }    
}