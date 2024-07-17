package expression;

import expression.exceptions.ArithmeticExceptions;

public class Add <T> extends Operations<T> {
    public Add(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c, a, b);
    }
    @Override
    public T evaluate(T x, T y, T z) {
        return super.evaluate(x, y, z);
    }

    public String toString() {
        return super.toString();
    }
    @Override
    public String toMiniString() {
        return super.toMiniString();
    }

    public int msGetLevel(){
        return Priority.ADDSUB.getLevel();
    }

    public int msGetPriority(){
        return Priority.ADDSUB.ordinal();
    }

    @Override
    public boolean equals(Object e){
        return super.equals(e);
    }

    protected T count(T x, T y) throws ArithmeticExceptions {
        return c.add(x,y);
    }

    @Override
    protected int hash() {
        return 1;
    }
    @Override
    protected String symbol() {
        return "+";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
