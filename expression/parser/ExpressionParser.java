package expression.parser;

import expression.*;

import java.util.ArrayList;

public final class ExpressionParser implements TripleParser {
    public BothEvaluations parse(final String source) {
        //System.out.println(source);
        return parse(new StringSource(source));
    }

    public static BothEvaluations parse(final CharSource source) {
        return new TripleParser(source).parseLine();
    }

    public static class TripleParser extends BaseParser {
    //    HashMap<String, Integer> types = new HashMap<>();
        ArrayList<String> expr = new ArrayList<>();

        protected TripleParser(CharSource source) {
            super(source);
        }
        public BothEvaluations parseLine() {
            final BothEvaluations result = parseElement();
            if (eof()) {
                return result;
            }
            throw error("End of the Expression expected");
        }

        private BothEvaluations parseElement() {
            skipWhitespace();
            final BothEvaluations result = parseValue();
            skipWhitespace();
            return result;
        }

        private BothEvaluations parseValue() {
            char[] sym = new char[]{'(', '+', '*', '-', '/', ')', 'x', 'y', 'z', 'r', 'p', 'l', 'g'};
            boolean isChar = false;
            boolean isNegate = false;
            int ix = 0;

            while (!eof()) {
                for (char c : sym) {
                    if (take(c)) {
                        if(c == '-'){
                            boolean unary = false;
                            if(ix==0){
                                unary = true;
                            }
                            else if(expr.get(expr.size() - 1).equals("+") || expr.get(expr.size() - 1).equals("-") || expr.get(expr.size() - 1).equals("*") || expr.get(expr.size() - 1).equals("/") || expr.get(expr.size() - 1).equals("(") || expr.get(expr.size() - 1).equals("lcm")|| expr.get(expr.size() - 1).equals("gcd")|| expr.get(expr.size() - 1).equals("reverse")){
                                unary = true;
                            }

                            if(unary) {
                                boolean isConst = false;
                                char[] nums = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                                for (char d : nums) {
                                    if (d == current()) {
                                        getPrev();
                                        isConst = true;
                                        break;
                                    }
                                }
                                if (isConst) {
                                    break;
                                }
                            }
                        }

                        expr.add(String.valueOf(c));
                        boolean co = false;
                        String command = "";
                        if(c == 'r'){
                            command = "reverse"; co = true;
                        }
                        else if(c == 'p'){
                            command = "pow10"; co = true;
                        }
                        else if(c == 'l'){
                            if(take('o')){
                                expr.set(ix, "log10");
                                take('g'); take('1'); take('0');
                            }else if (take('c')){
                                expr.set(ix, "lcm");
                                take('m');
                            } else throw error("Incorrect command.");
                        }
                        else if(c == 'g'){
                            command = "gcd"; co = true;
                        }
                        if(co){
                            expr.set(ix, command);
                            for(int h = 1; h < command.length(); h++){
                                if(!(take(command.charAt(h)))){
                                    throw error("Incorrect command.");
                                }
                            }
                        }
                        isChar = true;
                        break;
                    }
                }
                if(!isChar){
                    expr.add(buildNumber());
                    if(expr.get(expr.size() - 1).equals("2147483648")){
                        expr.remove((expr.size() - 1)); expr.remove((expr.size() - 1));
                        expr.add("-2147483648");
                    }

                }
                skipWhitespace();
                ix++;
                isChar = false;
            }


            expr.add("end");
            BothEvaluations val = null;
            return constructLevel(expr);
        }
        int pos = 0;

        private String buildNumber() {
            StringBuilder sb = new StringBuilder();
            if (take('-')) {
                sb.append('-');
            }
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                while (between('0', '9')) {
                    sb.append(take());
                }
            } else {
                throw error("Invalid number");
            }

            return sb.toString();
        }

        private static boolean isNumber(String str) {
            try {
                Integer.parseInt(str);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }

        private BothEvaluations baseLevel(ArrayList<String> a) {
            BothEvaluations res;
            if(expr.get(pos).equals("(")){
                pos++;
                res = gcdlcmLevel(a);

                String close = "";
                if(pos < a.size()){
                    close = a.get(pos);
                }

                if (pos < a.size() && close.equals(")")) {
                    pos++;
                    return res;
                }
            }
            if(isNumber(expr.get(pos))){
                Const a2 = new Const(Integer.parseInt(expr.get(pos)));
                pos++;
                return a2;
            } else if (expr.get(pos).equals("x") || expr.get(pos).equals("y") || expr.get(pos).equals("z")) {
                Variable b2 = new Variable(expr.get(pos));
                pos++;
                return b2;
            } else if (expr.get(pos).equals("-")){
                //BothEvaluations res_pre = multdivLevel(a);
                pos++;
                res = baseLevel(a);
                    Negate d2 = new Negate(res);
                    return d2;
                //Negate d2 = new Negate(res);
                //return d2;
            } else if (expr.get(pos).equals("reverse")){
                pos++;
                res = baseLevel(a);
                Reverse e2 = new Reverse(res);
                return e2;
            } else if (expr.get(pos).equals("pow10")){
                pos++;
                res = baseLevel(a);

                Pow g2 = new Pow(res);
                return g2;
            } else if (expr.get(pos).equals("log10")){
                pos++;
                res = baseLevel(a);

                Log f2 = new Log(res);
                return f2;
            }
            else {
                return null;
            }
        }

        private BothEvaluations multdivLevel(ArrayList<String> a) {
            BothEvaluations val = baseLevel(a);
            while (pos < a.size() - 1) {
                String operator = a.get(pos);
                if (!operator.equals("*") && !operator.equals("/")) {
                    break;
                } else {
                    pos++;
                }
                BothEvaluations val2 = baseLevel(a);
                if (operator.equals("*")) {
                    val = new Multiply(val, val2);
                } else {
                    val = new Divide(val, val2);
                }
            }
                return val;
        }

        private BothEvaluations addsubLevel(ArrayList<String> a) {
            BothEvaluations val = multdivLevel(a);

            while (pos < a.size() - 1) {
                String operator = a.get(pos);
                if (!operator.equals("+") && !operator.equals("-")) {
                    break;
                } else {
                    pos++;
                }

                BothEvaluations val2 = multdivLevel(a);
                if (operator.equals("+")) {
                    val = new Add(val, val2);
                } else {
                    val = new Subtract(val, val2);
                }
            }
                return val;
        }

        private BothEvaluations gcdlcmLevel(ArrayList<String> a) {
            BothEvaluations val = addsubLevel(a);

            while (pos < a.size() - 1) {
                String operator = a.get(pos);
                if (!operator.equals("gcd") && !operator.equals("lcm")) {
                    break;
                } else {
                    pos++;
                }

                BothEvaluations val2 = addsubLevel(a);
                if (operator.equals("gcd")) {
                    val = new GCD(val, val2);
                } else {
                    val = new LCM(val, val2);
                }
            }
            return val;
        }


        private BothEvaluations constructLevel(ArrayList<String> a) {
            if(expr.size() == 0){
                return null;
            } else{
                return gcdlcmLevel(a);
            }
        }
    }
}