package expression.generic;

import expression.OperationList;
import expression.TripleExpression;
/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface TripleParser<T> {
    TripleExpression<T> parse(String expression, final OperationList<T> x) throws Exception;
}
