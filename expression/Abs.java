package expression;


import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.IllegalCalculationException;
import expression.exceptions.OverflowException;

import java.util.Objects;

public class Abs <T> extends UnaryOperations<T> {
    public Abs(OperationList<T> uop, BothEvaluations<T> a) {
        super(uop, a);
    }

    public T evaluate(T x, T y, T z) {
        return super.evaluate(x,y,z);
    }

    public T count(T x) throws ArithmeticExceptions {
        return uop.abs(x);
    }
    public String toString() {
        return "abs(" + (a.toString()) + ")";
    }

    public String toMiniString() {
        if(a instanceof Const || a instanceof Variable || a instanceof Negate || a instanceof Reverse || a instanceof Pow || a instanceof Log){
            return "abs " + a.toMiniString();
        }
        return "abs(" + a.toMiniString() + ")";
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

