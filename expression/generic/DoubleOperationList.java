package expression.generic;

import expression.*;

import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.CommandTokenizationException;
import expression.exceptions.OverflowException;

public class DoubleOperationList implements OperationList<Double> {
    @Override
    public Double negate(Double x) throws OverflowException {
        return -x;
    }

    @Override
    public Double add(Double x, Double y) throws ArithmeticExceptions {
        return x+y;
    }

    @Override
    public Double subtract(Double x, Double y) throws ArithmeticExceptions {
        return x-y;
    }

    @Override
    public Double multiply(Double x, Double y) throws ArithmeticExceptions {
        return x*y;
    }

    @Override
    public Double divide(Double x, Double y) throws ArithmeticExceptions {
        return x/y;
    }

    @Override
    public Double parseNumber(String num) throws CommandTokenizationException {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException er) {
            throw new CommandTokenizationException("Incorrect const");
        }
    }

    @Override
    public Double abs(Double x) throws OverflowException {
        return Math.abs(x);
    }

    @Override
    public Double square(Double x) throws OverflowException {
        return x*x;
    }

    @Override
    public Double module(Double x, Double y) throws ArithmeticExceptions {
        //divideCheck(x, y);
        return x%y;
    }
}
