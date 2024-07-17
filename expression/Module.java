package expression;

import expression.exceptions.OverflowException;

public class Module<T> extends Operations<T>  {

    public Module(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c, a, b);
    }

    @Override
    protected T count(T x, T y) throws OverflowException {
        return c.module(x,y);
    }
    @Override
    protected int hash() {
        return 3;
    }

    public String toString() {
        return super.toString();
    }
    @Override
    protected String symbol() {
        return "*";
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
        return Priority.MULTIPLY.getLevel();
    }

    public int msGetPriority(){
        return Priority.MULTIPLY.ordinal();
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return (super.evaluate(x, y, z));
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
