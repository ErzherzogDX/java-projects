package expression.generic;


public interface CharSource {
    boolean hasNext();
    char next();
    char prev();
    IllegalArgumentException error(String message);
}
