
package expression;

//import expression.parser.BigFloppa;
//import expression.parser.BigFloppaParser;

import expression.exceptions.ArithmeticExceptions;
import expression.exceptions.TokenizationException;

//import static expression.parser.Json.*;

public class Main {
    public static void main(String[] args) throws ArithmeticExceptions, TokenizationException {

       // BothEvaluations tx = new LCM(new Const(4), new Const( 2147483647));
        //BothEvaluations ty = new LCM(new Const(2), new Const(-3));
      //  int x3 = tx.evaluate(2, 2, 4);
        //int x4 = ty.evaluate(2, 5, 8);

        String ps = "321 + 4 * 2";
        expression.exceptions.ExpressionParser parser1 = new expression.exceptions.ExpressionParser();
        BothEvaluations rx = parser1.parse(ps);

       // String t1 = rx.toMiniString();
      //  String t2 = rx.toMiniString();

       //int res = rx.evaluate(3,6,8);


        // parse(ps);

        //System.out.println(res);
       // System.out.println(res);


    }
}
