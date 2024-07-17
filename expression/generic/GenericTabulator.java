package expression.generic;

import expression.*;
import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.CommandTokenizationException;
import expression.exceptions.OverflowException;
import expression.exceptions.TokenizationException;

import java.util.HashMap;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
       // System.out.println(expression + " " + mode);
        return buildTable(getOperationList(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private OperationList<?> getOperationList(final String mode) {
        return switch (mode) {
            case "i" -> new IntOperationList("on");
            case "d" -> new DoubleOperationList();
            case "bi" -> new BigIntegerOperationList();
            case "u" -> new IntOperationList("off");
            case "s" -> new ShortOperationList();
            case "p" -> new IntOperationList("mod");
            default -> null;
        };
    }

    private <T> Object[][][] buildTable(OperationList<T> op, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        int xSize = Math.abs(x2-x1);
        int ySize = Math.abs(y2-y1);
        int zSize = Math.abs(z2-z1);
        Object[][][] res = new Object[xSize +1][ySize+1][zSize+1];
        expression.generic.ExpressionParser<T> parser1 = new expression.generic.ExpressionParser<>();
        BothEvaluations<T> rx = parser1.parse(expression, op);

        for (int i = 0; i <= xSize; i++) {
            for (int j = 0; j <= ySize; j++) {
               for (int k = 0; k <= zSize; k++) {
                    try {
                        res[i][j][k] = rx.evaluate(
                                op.parseNumber(Integer.toString(i+x1)),
                                op.parseNumber(Integer.toString(j+y1)),
                                op.parseNumber(Integer.toString(k+z1)));
                    } catch (ArithmeticExceptions | CommandTokenizationException e) {
                        res[i][j][k] = null;
                    }
                }
            }
        }
        return res;
    }
}