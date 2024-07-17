package expression;

import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.CommandTokenizationException;
import expression.exceptions.OverflowException;

public interface OperationList <T> {
    T negate(final T x) throws OverflowException;

    T abs(final T x) throws OverflowException;

    T square(final T x) throws OverflowException;

    T add(final T x, final T y) throws ArithmeticExceptions;

    T subtract(final T x, final T y) throws ArithmeticExceptions;

    T multiply(final T x, final T y) throws ArithmeticExceptions;

    T divide(final T x, final T y) throws ArithmeticExceptions;

    T module(final T x, final T y) throws ArithmeticExceptions;


    T parseNumber(final String num) throws CommandTokenizationException;
}
