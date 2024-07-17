package expression;

import expression.exceptions.ArithmeticExceptions;

public abstract class UnaryOperations <T> implements BothEvaluations<T> {
    public final BothEvaluations<T> a;
    protected OperationList<T> uop;

    protected UnaryOperations(OperationList<T> uop, BothEvaluations <T> a) {
        this.uop = uop;
        this.a = a;
    }

    public BothEvaluations<T> getA(){
        return a;
    }

    protected abstract T count(T left) throws ArithmeticExceptions;

    public T evaluate(T x, T y, T z) {
        return count(a.evaluate(x,y,z));
    }

}
