package expression;

public interface  BothEvaluations <T> extends TripleExpression<T> {
    int msGetPriority();
    int msGetLevel();
    String toMiniString();

}
