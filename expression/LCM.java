package expression;

import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.OverflowException;

public class LCM<T> extends Operations<T> {

    public LCM(OperationList<T> c, BothEvaluations<T> a, BothEvaluations<T> b) {
        super(c,a, b);
    }

    @Override
    protected T count(T left, T right) throws ArithmeticExceptions {
        return null;
    }

    private int getLCA(int x, int y) throws OverflowException {
        if(x == 0 && y == 0) return 0;
        int gcd = getGCD(x, y);
        int res = (x / gcd) * y;

        if(x!= -2147483648 && y!=-2147483648 && y!=0) {
            if(res/y != (x/gcd)){
                throw new OverflowException();
            }
        }
        else if(x == -2147483648 && gcd != y && y!=0) throw new OverflowException();
        else if(y == -2147483648 && gcd != x && x!=0) throw new OverflowException();
        return res;
    }


    private int getGCD(int x, int y) throws OverflowException {
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

    public int msGetPriority(){ return Priority.LCM.ordinal();
    }

    @Override
    public boolean equals(Object e){
        return super.equals(e);
    }

    protected int count(int x, int y) throws OverflowException {
        return getLCA(x, y);
    }


    @Override
    protected int hash() {
        return 37;
    }
    @Override
    protected String symbol() {
        return "lcm";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
