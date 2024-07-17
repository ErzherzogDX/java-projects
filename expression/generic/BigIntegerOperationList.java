package expression.generic;

import expression.*;

import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.CommandTokenizationException;
import expression.exceptions.IllegalCalculationException;
import expression.exceptions.OverflowException;

import java.math.BigInteger;

public class BigIntegerOperationList implements OperationList<BigInteger>{
    @Override
    public BigInteger negate(BigInteger x) throws OverflowException {
        return x.negate();
    }

    @Override
    public BigInteger add(BigInteger x, BigInteger y) throws ArithmeticExceptions {
        return x.add(y);
    }

    @Override
    public BigInteger subtract(BigInteger x, BigInteger y) throws ArithmeticExceptions {
        return x.subtract(y);
    }

    @Override
    public BigInteger multiply(BigInteger x, BigInteger y) throws ArithmeticExceptions {
        return x.multiply(y);
    }

    @Override
    public BigInteger divide(BigInteger x, BigInteger y) throws ArithmeticExceptions {
        isZero(y);
        return x.divide(y);
    }

    @Override
    public BigInteger parseNumber(String num) throws CommandTokenizationException {
        try {
            return new BigInteger(num);
        } catch (NumberFormatException er) {
            throw new CommandTokenizationException("Incorrect const");
        }
    }
    private void isZero(final BigInteger x) throws IllegalArgumentException {
        if (x.equals(BigInteger.ZERO)) {
            throw new ArithmeticExceptions("division by zero");
        }
    }

    @Override
    public BigInteger abs(BigInteger x) throws OverflowException {
        return x.abs();
    }

    @Override
    public BigInteger square(BigInteger x) throws OverflowException {
        return x.multiply(x);
    }

    @Override
    public BigInteger module(BigInteger x, BigInteger y) throws ArithmeticExceptions {
        isZero(y); isNeg(y);
        return x.mod(y);
    }

    private void isNeg(BigInteger y){
        if(y.compareTo(BigInteger.ZERO) < 0)
        throw new IllegalCalculationException("neg");
    }

}
