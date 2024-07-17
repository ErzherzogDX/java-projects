package expression;

import expression.exceptions.ArithmeticExceptions;

public class Subtract <T> extends Operations<T> {

    public Subtract(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c, a, b);
    }
    protected T count(T x, T y) throws ArithmeticExceptions {
        return c.subtract(x,y);
    }


    @Override
    protected int hash() {
        return 2;
    }

    public String toString() {
        return super.toString();
    }
    @Override
    protected String symbol() {
        return "-";
    }

    @Override
    public boolean equals(Object e){
        return super.equals(e);
    }
    @Override
    public String toMiniString() {
        return super.toMiniString();
    }

    public int msGetLevel(){
        return Priority.SUBTRACTION.getLevel();
    }

    public int msGetPriority(){
        return Priority.ADDSUB.ordinal();
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return super.evaluate(x, y, z);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
