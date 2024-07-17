package expression.generic;

import expression.*;
import expression.Module;
import expression.exceptions.*;

import java.util.*;

public final class ExpressionParser<T> implements TripleParser<T> {
    public BothEvaluations<T> parse(final String source, final OperationList<T> x) throws TokenizationException {
        return parse(new StringSource(source), x);
    }

    public BothEvaluations<T> parse(final CharSource source, final OperationList<T> x) throws TokenizationException {
        return new TripleParser<>(source, x).parseLine();
    }

    public static class TripleParser<T> extends BaseParser {
        ArrayList<String> expr = new ArrayList<>();

        char[] sym = new char[]{'(', '+', '*', '-', '/', ')', 'x', 'y', 'z', 'r', 'p', 'l', 'g', 'a', 's', 'm'};
        HashSet<String> binaryOperations = new HashSet<>(Arrays.asList("+", "*", ")", "/", "gcd", "lcm", "mod"));
        HashSet<String> unaryOperations = new HashSet<>(Arrays.asList("-", "reverse", "(", "/", "gcd", "lcm", "pow10", "log10", "abs", "square"));
        HashSet<String> variables = new HashSet<>(Arrays.asList("x", "y", "z", ")"));
        OperationList<T> ops;

        protected TripleParser(CharSource source, final OperationList<T> x) {
            super(source);
            ops = x;
        }
        public BothEvaluations<T> parseLine() throws TokenizationException {
            final BothEvaluations<T> result = parseElement();
            if (eof()) {
                return result;
            }
            throw new CommandTokenizationException("End of the expression expected.");
        }

        private BothEvaluations<T> parseElement() throws TokenizationException {
            skipWhitespace();
            final BothEvaluations<T> result = parseValue();
            skipWhitespace();
            return result;
        }

        private void whitespaceCheck() throws TokenizationException {
            char next = take();
            if(!Character.isWhitespace(next) && next != '-' && next != '('){
                throw new CommandTokenizationException("Missing whitespace after the command.");
            } else getPrev();
        }

        private BothEvaluations<T> parseValue() throws TokenizationException {
            boolean isChar = false;
            boolean isNumberRepeated = false;
            int ix = 0;
            int skobockaBalance = 0;

            while (!eof()) {
                for (char c : sym) {
                    if (take(c)) {
                        if(c == '-'){
                            boolean unary = false;
                            if(ix==0){
                                unary = true;
                            } else if(binaryOperations.contains(expr.get(expr.size() - 1))
                                    || unaryOperations.contains(expr.get(expr.size() - 1))
                                    && !expr.get(expr.size() - 1).equals(")")){
                                unary = true;
                            }

                            if(unary) {
                                if (between('0', '9')) {
                                    getPrev();
                                    break;
                                }
                            }
                        }

                        expr.add(String.valueOf(c));

                        if(c == '('){
                            skobockaBalance++;
                        } else if(c == ')'){
                            skobockaBalance--;
                        }

                        if(skobockaBalance < 0){
                            throw new BracketTokenizationExceptions("No opening parenthesis at char " + ix);
                        }

                        boolean co = false;
                        String command = switch (c){
                            case 'r' -> "reverse";
                            case 'p' -> "pow10";
                            case 'g' -> "gcd";
                            case 'a' -> "abs";
                            case 's' -> "square";
                            case 'm' -> "mod";
                            default -> "";
                        };

                        if(!command.equals("")){
                            co = true;
                        }

                        if(co){
                            expr.set(ix, command);
                            for(int h = 1; h < command.length(); h++){
                                if(!(take(command.charAt(h)))){
                                     throw new CommandTokenizationException("Incorrect command.");
                                }
                            }
                            whitespaceCheck();
                        }

                        if(expr.size() == 1){
                            if(binaryOperations.contains(expr.get(0))){
                                throw new ArgumentTokenizationException("Incorrect start symbol - it cannot be a binary operation");
                            }
                        } else if (expr.get(ix).equals(")") && expr.get(ix-1).equals("(")){
                            throw new BracketTokenizationExceptions("Empty brackets at chars " + (ix-1) + " and " + ix);
                        }
                        else if(binaryOperations.contains(expr.get(ix))){
                            if(!variables.contains(expr.get(ix-1)) && !isNumber(expr.get(ix-1))){
                                throw new ArgumentTokenizationException("Missing argument between operations: " + expr.get(ix-1) + " and " + expr.get(ix) + " (chars " + (ix-1) + " and " + (ix) + ")");
                            }
                        }
                        isChar = true;
                        isNumberRepeated = false;
                        break;
                    }
                }

                if(!isChar){
                    expr.add(buildNumber(ix));
                    if(isNumberRepeated){
                        throw new CommandTokenizationException("Missing operation between numbers " + expr.get(ix -1) + " and " + expr.get(ix) + "(chars " + (ix-1) + " and " + (ix) + ")");
                    }
                    isNumberRepeated = true;
                }
                skipWhitespace();
                ix++;
                isChar = false;

            }

            if (skobockaBalance > 0) {
                throw new BracketTokenizationExceptions("No closing parenthesis.");
            }
            if(!variables.contains(expr.get(ix-1)) && !isNumber(expr.get(expr.size() - 1))){
                throw new ArgumentTokenizationException("Incorrect end symbol - it cannot be a binary operation.");
            }

            expr.add("end");
            return constructLevel(expr);
        }
        int pos = 0;

        private String buildNumber(int ix) throws TokenizationException {
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
                if(ix == 0) throw new CommandTokenizationException("Invalid start symbol!");
                else throw new CommandTokenizationException("Invalid symbol at position " + ix);
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

        private BothEvaluations<T> chooseUnaryCommand(ArrayList<String> a){
            return switch (expr.get(pos - 1)) {
                case "-" -> new CheckedNegate<>(ops, baseLevel(a));
                case "reverse" -> new Reverse<>(ops, baseLevel(a));
                case "pow10" -> new Pow<>(ops, baseLevel(a));
                case "log10" -> new Log<>(ops, baseLevel(a));
                case "abs" -> new Abs<>(ops, baseLevel(a));
                case "square" -> new Square<>(ops, baseLevel(a));
                default -> null;
            };
        }

        private BothEvaluations<T> chooseBinaryCommand(String operator, BothEvaluations<T> val, BothEvaluations<T> val2){
            return switch (operator) {
                case "*" -> new CheckedMultiply<>(ops,val, val2);
                case "/" -> new CheckedDivide<>(ops,val, val2);
                case "mod" -> new Module<>(ops, val, val2);
                case "+" -> new CheckedAdd<>(ops, val, val2);
                case "-" -> new CheckedSubtract<>(ops, val, val2);
                case "gcd" -> new GCD<>(ops, val, val2);
                case "lcm" -> new LCM<>(ops, val, val2);

                default -> null;
            };
        }

        private BothEvaluations<T> baseLevel(ArrayList<String> a) {
            BothEvaluations<T> res;
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

            pos++;
            String token = expr.get(pos-1);
            if(isNumber(token)){
                return new Const<>(ops.parseNumber(token));
            } else if (token.equals("x") || token.equals("y") || token.equals("z")) {
                return new Variable<>(expr.get(pos-1));
            } else {
                return chooseUnaryCommand(a);
            }
        }

        private BothEvaluations<T> multdivLevel(ArrayList<String> a) {
            BothEvaluations<T> val = baseLevel(a);
            while (pos < a.size() - 1) {
                String operator = a.get(pos);
                if (!operator.equals("*") && !operator.equals("/")&& !operator.equals("mod")) {
                    break;
                } else {
                    pos++;
                }
                BothEvaluations<T> val2 = baseLevel(a);
                val = chooseBinaryCommand(operator, val, val2);
            }
            return val;
        }

        private BothEvaluations<T> addsubLevel(ArrayList<String> a) {
            BothEvaluations<T> val = multdivLevel(a);

            while (pos < a.size() - 1) {
                String operator = a.get(pos);
                if (!operator.equals("+") && !operator.equals("-")) {
                    break;
                } else {
                    pos++;
                }

                BothEvaluations<T> val2 = multdivLevel(a);
                val = chooseBinaryCommand(operator, val, val2);

            }
                return val;
        }

        private BothEvaluations<T> gcdlcmLevel(ArrayList<String> a) {
            BothEvaluations<T> val = addsubLevel(a);

            while (pos < a.size() - 1) {
                String operator = a.get(pos);
                if (!operator.equals("gcd") && !operator.equals("lcm")) {
                    break;
                } else {
                    pos++;
                }

                BothEvaluations<T> val2 = addsubLevel(a);
                val = chooseBinaryCommand(operator, val, val2);

            }
            return val;
        }

        private BothEvaluations<T> constructLevel(ArrayList<String> a) {
            if(expr.isEmpty()){
                return null;
            } else{
                return gcdlcmLevel(a);
            }
        }
    }
}