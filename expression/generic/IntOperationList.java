package expression.generic;

import expression.*;
import expression.exceptions.*;

public class IntOperationList implements OperationList<Integer> {

    String mode = "";
    final int module = 10079;
    public IntOperationList(final String mode) {
        this.mode = mode;
    }

    @Override
    public Integer negate(Integer x) throws ArithmeticExceptions {
        if(mode.equals("on")){
            negateCheck(x);
        }
        if(mode.equals("mod")){
            return getModule(-x);
        }
        return -x;
    }

    @Override
    public Integer abs(Integer x) throws OverflowException {
        if(mode.equals("on")){
            negateCheck(x);
        }
        return Math.abs(x);
    }

    @Override
    public Integer square(Integer x) throws OverflowException {
        if(mode.equals("on")){
            multiplyCheck(x,x);
        }
        if(mode.equals("mod")){
            return getModule(x*x);
        }
        return x*x;
    }

    @Override
    public Integer module(Integer x, Integer y) throws ArithmeticExceptions {
        if (y == 0) throw new ArithmeticExceptions("");
        if(mode.equals("on")){
           // divideCheck(x, y);
        }
        int t = x%y;
        if(mode.equals("mod")){
            return getModule(t);
        } else return x%y;
    }

    @Override
    public Integer add(Integer x, Integer y) throws ArithmeticExceptions {
        if(mode.equals("on")){
            addCheck(x,y);
        }
        if(mode.equals("mod")){
            return getModule(x+y);
        }
        return x + y;
    }

    @Override
    public Integer subtract(Integer x, Integer y) throws ArithmeticExceptions {
        if(mode.equals("on")){
            subtractCheck(x,y);
        }
        if(mode.equals("mod")){
            return getModule(x-y);
        }
        return x - y;
    }

    @Override
    public Integer multiply(Integer x, Integer y) throws ArithmeticExceptions {
        if(mode.equals("on")) {
            multiplyCheck(x, y);
        }
        if(mode.equals("mod")){
            return getModule(x*y);
        }
        return x * y;
    }

    @Override
    public Integer divide(Integer x, Integer y) throws ArithmeticExceptions {
            divideCheck(x, y);
        if(mode.equals("mod")){
            int yn = binpow(y);
            return getModule(x*yn);
        }
        return x / y;
    }
    public Integer parseNumber(final String num) throws ArithmeticExceptions {
        try {
            if(mode.equals("mod")){
                int x = Integer.parseInt(num);
                return getModule(x);
                //return (Integer.parseInt(num))%10079;
            }
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            throw new CommandTokenizationException("Incorrect Const");
        }
    }
    private void addCheck(Integer x, Integer y){
        if (x < 0 && Integer.MIN_VALUE - x > y) {
            throw new OverflowException();
        }
        if (x > 0 && Integer.MAX_VALUE - x < y) {
            throw new OverflowException();
        }
    }

    private void subtractCheck(Integer x, Integer y) throws ArithmeticExceptions{
            if ((x >= 0 && y < 0 && x - Integer.MAX_VALUE > y) ||
                    (x <= 0 && y > 0 && Integer.MIN_VALUE - x > -y)) {
                throw new OverflowException();
            }
    }

    private void multiplyCheck(int x, int y) throws ArithmeticExceptions {
        if (x > 0 && y > 0 && Integer.MAX_VALUE / x < y) {
            throw new OverflowException();
        }
        if (x > 0 && y < 0 && Integer.MIN_VALUE / x > y) {
            throw new OverflowException();
        }
        if (x < 0 && y > 0 && Integer.MIN_VALUE / y > x) {
            throw new OverflowException();
        }
        if (x < 0 && y < 0 && Integer.MAX_VALUE / x > y) {
            throw new OverflowException();
        }
    }

    private void negateCheck(int x) throws ArithmeticExceptions {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException();
        }
    }

    private void divideCheck(int x, int y) throws ArithmeticExceptions {
        if (y == 0) {
            throw new IllegalCalculationException("division by zero");
        }
        if(x == Integer.MIN_VALUE && y == -1) {
            throw new OverflowException();
        }
    }

    private int getModule(int x){
        return (x % module + module) % module;
    }
    private int binpow (int a) {
        long res = 1; long an = a;
        int md = module - 2;
        while (md != 0) {
            if ((md & 1) == 1)
                res = res * an % module;
            an = an * an % module;
            md /= 2;
        }
        return (int)(res%module);
    }

}
