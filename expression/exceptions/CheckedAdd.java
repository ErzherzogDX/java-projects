package expression.exceptions;

import expression.*;

public class CheckedAdd<T> extends Add<T> {
    public CheckedAdd(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b ) {
        super(c, a, b);
    }

    protected T count(T x, T y) throws ArithmeticExceptions {
        return c.add(x,y);
    }
}
