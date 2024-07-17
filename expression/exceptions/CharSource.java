package expression.exceptions;


public interface CharSource {
    boolean hasNext();
    char next();
    char prev();
    IllegalArgumentException error(String message);
}
