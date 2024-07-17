package expression.exceptions;
import expression.*;

public class CheckedSubtract<T>  extends Subtract<T> {
    public CheckedSubtract(OperationList<T>  c,BothEvaluations<T>  a, BothEvaluations<T>  b) {
        super(c, a, b);
    }

    protected T count(T x, T y) throws ArithmeticExceptions {
        return c.subtract(x,y);
    }
}
