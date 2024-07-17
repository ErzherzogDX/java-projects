package expression.exceptions;
import expression.*;

public class CheckedNegate<T> extends Negate<T>{
    public CheckedNegate(OperationList<T> uop, BothEvaluations<T> a) {
        super(uop,a);
    }

    protected T count(T x) throws OverflowException {
        return uop.negate(x);
    }

}
