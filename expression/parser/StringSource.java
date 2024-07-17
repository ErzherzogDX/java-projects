package expression.parser;

public class StringSource implements CharSource {
    private final String data;
    private int post;


    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return post < data.length();
    }

    @Override
    public char next() {
        return data.charAt(post++);
    }

    @Override
    public char prev() {
        post-=2;
        if(post < 0){
            post++;
        }
        return data.charAt(post++);
    }

    @Override
    public IllegalArgumentException error(final String message) {
        return new IllegalArgumentException(post + ": " + message);
    }
}
