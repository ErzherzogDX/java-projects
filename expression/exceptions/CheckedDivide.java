package expression.exceptions;
import expression.*;

public class CheckedDivide<T> extends Divide<T>{
    public CheckedDivide(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c, a, b);
    }

    protected T count(T x, T y) throws IllegalCalculationException, OverflowException {
        return c.divide(x,y);
    }
}
