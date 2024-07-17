package expression.exceptions;

import expression.TripleExpression;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface TripleParser<T> {
    TripleExpression<T> parse(String expression) throws Exception;
}
