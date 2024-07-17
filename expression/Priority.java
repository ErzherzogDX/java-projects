package expression;

public enum Priority {
    MULTIPLY(3), DIVISION(3), ADDSUB(1), CONSTVAR(999), SUBTRACTION(2), NEGATE(998), GCD(0), LCM(0);

    private final int level;
    Priority(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
