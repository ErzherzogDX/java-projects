package expression;


import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.IllegalCalculationException;
import expression.exceptions.OverflowException;

import java.util.Objects;

public class Pow <T> extends UnaryOperations<T> {
    public Pow(OperationList<T> uop, BothEvaluations<T> a) {
        super(uop, a);
    }

    @Override
    protected Object count(Object left) throws ArithmeticExceptions {
        return null;
    }

    private int powing(int x) throws ArithmeticExceptions {
        int res = 1;
        if(x < 0){
            throw new IllegalCalculationException("Raising a number to a negative power.");
        } else if(x > 9){
            throw new OverflowException();
        }
        for(int i = 0; i < x; i++){
            res*=10;
        }

        return res;
    }

    public T evaluate(T x, T y, T z) {
        return super.evaluate(x,y,z);
    }

    public int count(int x) throws ArithmeticExceptions {
        return powing(x);
    }
    public String toString() {
        return "pow10(" + (a.toString()) + ")";
    }

    public String toMiniString() {
        if(a instanceof Const || a instanceof Variable || a instanceof Negate || a instanceof Reverse || a instanceof Pow || a instanceof Log){
            return "pow10 " + a.toMiniString();
        }
        return "pow10(" + a.toMiniString() + ")";
    }

    public int hashCode() {
        return Objects.hash(a, 17);
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

