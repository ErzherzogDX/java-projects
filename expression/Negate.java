package expression;

import expression.exceptions.OverflowException;

import java.util.Objects;

public class Negate<T> extends UnaryOperations<T>{
    public Negate(OperationList<T> uop, BothEvaluations<T> a) {
        super(uop, a);
    }

    public T evaluate(T x, T y, T z) {
        return super.evaluate(x, y, z);
    }
    protected T count(T x) throws OverflowException {
        return uop.negate(x);
    }

    public String toString() {
        return "-(" + a.toString() + ")";
    }

    public String toMiniString() {
        if(a instanceof Const || a instanceof Variable || a instanceof Negate || a instanceof Reverse || a instanceof Pow || a instanceof Log){
            return "- " + a.toMiniString();
        }
        return "-(" + a.toMiniString() + ")";
    }

    public int hashCode() {
        return Objects.hash(a, 7);
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
