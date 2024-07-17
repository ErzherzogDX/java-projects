package expression;

import expression.exceptions.IllegalCalculationException;
import expression.exceptions.OverflowException;

public class Divide <T> extends Operations<T> {
    public Divide(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c, a, b);
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return super.evaluate(x, y, z);
    }

    protected T count(T x, T y) throws OverflowException, IllegalCalculationException {
        return c.divide(x, y);
    }

    @Override
    protected int hash() {
        return 4;
    }

    public String toString() {
        return super.toString();
    }

    public String toMiniString() {
        return super.toMiniString();
    }

    public int msGetLevel(){
        return Priority.DIVISION.getLevel();
    }

    public int msGetPriority(){
        return Priority.DIVISION.ordinal();
    }

    @Override
    protected String symbol() {
        return "/";
    }

    @Override
    public boolean equals(Object e){
        return super.equals(e);
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
