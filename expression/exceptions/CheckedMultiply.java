package expression.exceptions;
import expression.*;

public class CheckedMultiply<T> extends Multiply<T>{
    public CheckedMultiply(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c, a, b);
    }



    protected T count(T x, T y) throws OverflowException {
        return c.multiply(x,y);
    }
}
