package expression.generic;

import expression.*;

import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.CommandTokenizationException;
import expression.exceptions.OverflowException;

public class ShortOperationList implements OperationList<Short> {
    @Override
    public Short negate(Short x)  {
        return (short) (-x);
    }

    @Override
    public Short abs(Short x) throws OverflowException {
        return (short)Math.abs(x);
    }

    @Override
    public Short square(Short x) throws OverflowException {
        return (short)(x*x);
    }

    @Override
    public Short add(Short x, Short y)  {
        return (short) (x+y);
    }

    @Override
    public Short subtract(Short x, Short y)  {
        return (short) (x-y);
    }

    @Override
    public Short multiply(Short x, Short y)  {
        return (short) (x*y);
    }

    @Override
    public Short divide(Short x, Short y)  {
        checkZero(y);
        return (short)(x/y);
    }

    @Override
    public Short module(Short x, Short y) throws ArithmeticExceptions {
        checkZero(y);
        return (short) (x%y);
    }

    @Override
    public Short parseNumber(String num) throws CommandTokenizationException {
        try {
            return (short)Integer.parseInt(num);
        } catch (NumberFormatException er) {
            throw new CommandTokenizationException("Incorrect const");
        }
    }

    private void checkZero(Short y){
        if(y == 0){
            throw new ArithmeticExceptions("division by zero");
        }
    }
}
