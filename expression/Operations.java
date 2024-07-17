package expression;

import expression.exceptions.ArithmeticExceptions;

import java.util.Objects;

public abstract class Operations <T> implements BothEvaluations<T> {

    public final BothEvaluations<T> a;
    public final BothEvaluations<T> b;
    protected OperationList<T> c;

    protected Operations(OperationList<T> c,BothEvaluations<T> a, BothEvaluations<T> b) {
        this.a = a;
        this.b = b;

        this.c = c;
    }
    public BothEvaluations <T> getA(){
        return a;
    }
    public BothEvaluations <T> getB(){
        return b;
    }

    protected abstract T count(T left, T right) throws ArithmeticExceptions;
    protected abstract int hash();
    protected abstract String symbol();

    public boolean equals(Object e){
        if(e == null || !(e.getClass().equals(this.getClass()))){
            return false;
        } else{
            return (((Operations) e).getA().equals(a)) && (((Operations) e).getB().equals(b));
        }
    }

    public T evaluate(T x, T y, T z) {
        return count(a.evaluate(x,y,z), b.evaluate(x,y,z));
    }

    public String toString() {
        return "(" + a.toString() + " " + symbol() + " " + b.toString() + ")";
    }

    public abstract int msGetLevel();
    public abstract int msGetPriority();

    public String toMiniString() {
        int al = a.msGetLevel();
        int bl = b.msGetLevel();
     //   int ap = a.msGetPriority();
        int bp = b.msGetPriority();

        String left = a.toMiniString();
        String right = b.toMiniString();

        boolean isCommutative = false;
        if(msGetLevel() == 1 || (msGetLevel() == 3 && msGetPriority() == 0)){
            isCommutative = true;
        }

        if(al < 3 && msGetLevel() >= 3){
            left = "(" + left + ")";
        } else if(al == 0 && msGetLevel() < 3 && msGetLevel() != 0){
            left = "(" + left + ")";
        }

        if((bl > msGetLevel())){

        }
        else{
            if(isCommutative && (bp == msGetPriority())){

            } else {
                if(bl!=0){
                right = "(" + right + ")";
            }}
        }

        if(bl == 0 && bp != msGetPriority()){
                right = "(" + right + ")";
        } else if(al==0 && bl == 0 && (bp != msGetPriority())){
            right = "(" + right + ")";
        }
        return left + " " + symbol() + " " + right;
    }

    public int hashCode() {
        return Objects.hash(getA(), getB(), hash());
    }

}
