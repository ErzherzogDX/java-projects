package expression;


import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.IllegalCalculationException;

import java.util.Objects;

public class Log<T> extends UnaryOperations<T> {

    public Log(OperationList<T> uop, BothEvaluations<T> a) {
        super(uop, a);
    }

    @Override
    protected T count(T left) throws ArithmeticExceptions {
        return null;
    }

    private int loging(int x) throws IllegalCalculationException {
        if(x <= 0){
            throw new IllegalCalculationException("Logarithm of non-positive numbers");
        }
        String toS = Integer.toString(x);
        int size = toS.length();

        return size - 1;
    }

    public T evaluate(T x, T y, T z) {
        return super.evaluate(x,y,z);
    }

    public int count(int x) throws IllegalCalculationException {
        return loging(x);
    }
    public String toString() {
        return "log10(" + (a.toString()) + ")";
    }

    public String toMiniString() {
        if(a instanceof Const || a instanceof Variable || a instanceof Negate || a instanceof Reverse || a instanceof Log || a instanceof Pow){
            return "log10 " + a.toMiniString();
        }
        return "log10(" + a.toMiniString() + ")";

    }

    public int hashCode() {
        return Objects.hash(a, 19);
    }

    @Override
    public int msGetPriority() {
        return Priority.NEGATE.getLevel();
    }

    @Override
    public int msGetLevel() {
        return Priority.NEGATE.ordinal();
    }

}

