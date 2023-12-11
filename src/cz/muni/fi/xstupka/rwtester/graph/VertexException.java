package cz.muni.fi.xstupka.rwtester.graph;

/**
 * @author Pavel Stupka &lt;xstupka@fi.muni.cz&gt;
 */
public class VertexException extends GraphException {

    public VertexException() {
    }

    public VertexException(String message) {
        super(message);
    }

    public VertexException(Throwable cause) {
        super(cause);
    }

    public VertexException(String message, Throwable cause) {
        super(message, cause);
    }
}
