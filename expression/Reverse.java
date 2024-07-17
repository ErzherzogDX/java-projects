package expression;


import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.OverflowException;

import java.util.Objects;

public class Reverse<T> extends UnaryOperations<T> {
    public Reverse(OperationList<T> uop, BothEvaluations<T> a) {
        super(uop,a);
    }

    @Override
    protected T count(T left) throws ArithmeticExceptions {
        return null;
    }

    private int reversing(int x) throws OverflowException {
        boolean isNeg = false; int start = 0;
        String toRev = Integer.toString(x);
        if(x < 0) {
            isNeg = true;
            toRev = toRev.substring(1, toRev.length());
        }

        StringBuilder sb = new StringBuilder(toRev);
        sb.reverse(); String newS = sb.toString();

        for(int i = start; i < toRev.length(); i++){
            if(toRev.charAt(i) != newS.charAt(toRev.length() - 1 - i)){
                throw new OverflowException();
            }
        }

        if(isNeg){
            return Integer.parseInt("-" + newS);
        }
        else return Integer.parseInt(newS);
    }

    public T evaluate(T x, T y, T z) {
        return super.evaluate(x,y,z);
    }

    public int count(int x) throws OverflowException {
        return reversing(x);
    }
    public String toString() {
        return "reverse(" + (a.toString()) + ")";
    }

    public String toMiniString() {
        if(a instanceof Const || a instanceof Variable || a instanceof Negate || a instanceof Reverse){
            return "reverse " + a.toMiniString();
        }
        return "reverse(" + a.toMiniString() + ")";
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

