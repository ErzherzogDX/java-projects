package expression;

import java.util.Objects;

public class Const <T> implements BothEvaluations<T> {
     private final T i;
    public Const( T i) {
        this.i = i;
    }

   @Override
    public T evaluate(T x, T y, T z) {
        return i;
    }

    public String toString() {
        return i.toString();
    }

    public String toMiniString() {
        return i.toString();
    }

    public int msGetLevel(){
        return Priority.CONSTVAR.getLevel();
    }

    public int msGetPriority(){
        return Priority.CONSTVAR.ordinal();
    }

    @Override
    public boolean equals(Object e){
        if(!(e instanceof Const)){
            return false;
        } else{
            return Objects.equals(i, ((Const<?>) e).i);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(i) * 13;
    }
}
