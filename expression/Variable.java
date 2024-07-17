package expression;

import java.util.Objects;

public class Variable<T> implements BothEvaluations<T> {
    final String rm;
    public Variable(String x) {
        rm = x;
    }

    public T evaluate(T x, T y, T z) {
        switch (rm) {
            case "x": {
                return x;
            }
            case "y": {
                return y;
            }
            case "z": {
                return z;
            }
            default: return null;
        }
    }

    public String getVar(){
        return rm;
    }

    public String toString() {
        return rm;
    }

    public String toMiniString() {
        return rm;
    }


    public int msGetLevel(){
        return Priority.CONSTVAR.getLevel();
    }

    public int msGetPriority(){
        return Priority.CONSTVAR.ordinal();
    }

    @Override
    public boolean equals(Object e){
        if(!(e instanceof Variable)){
            return false;
        } else{
            return rm.equals(((Variable) e).getVar());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(rm) * 13;
    }

}
