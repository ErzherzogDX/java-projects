package expression;

import expression.exceptions.ArithmeticExceptions;

public class GCD<T> extends Operations <T> {

    public GCD(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c,a, b);
    }

    private int getGCD(int x, int y){
        if(x == 0 && y == 0){
            return 0;
        } else if(x == 0 || y == 0){
            if(x == 0){
                if(y > 0) return y;
                else return -y;
            }
            else {
                if(x > 0) return x;
                else return -x;
            }
        }

        if(x < 0 && x!=-2147483648) x = -x;
        if(y < 0 && y!=-2147483648) y = -y;

        while (y != 0) {
            int temp = y;
            y = x % y;
            x = temp;
        }

        if(x < 0) x = -x;
        return x;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return super.evaluate(x, y, z);
    }

    public String toString() {
        return super.toString();
    }
    @Override
    public String toMiniString() {
        return super.toMiniString();
    }

    public int msGetLevel(){
        return Priority.GCD.getLevel();
    }

    public int msGetPriority(){
        return Priority.GCD.ordinal();
    }

    @Override
    public boolean equals(Object e){
        return super.equals(e);
    }

    protected int count(int x, int y) {
        return getGCD(x, y);
    }

    @Override
    protected T count(T left, T right) throws ArithmeticExceptions {
        return null;
    }

    @Override
    protected int hash() {
        return 31;
    }
    @Override
    protected String symbol() {
        return "gcd";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
