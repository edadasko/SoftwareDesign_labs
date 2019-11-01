package com.example.calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

class RPNSolver {

    interface Operation{
        double calculate(double x, double y);
    }

    interface Function{
        double calculate(double x);
    }

    private Map<String, Operation> operationsDict;
    private Map<String, Function> functionsDict;
    private Map<String, Double> constantsDict;

    private String uMinus = "±";

    private List<String> operators = Arrays.asList("+", "-", "*", "/", "^", uMinus);
    private List<String> brackets = Arrays.asList("(", ")");
    private List<String> rightAssociativeOperators = Arrays.asList("^");
    static List<String> functions = Arrays.asList("sin", "cos", "tg", "ctg", "ln", "exp", "log2", "log10");
    static List<String> constants = Arrays.asList("π", "e");

    RPNSolver() {
        initOperationsAndFunctions();
    }

    private List<String> toPostfix(String infix) {
        String delimiters = "+-*/^()";
        List<String> postfix = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = null;
        String curr;

        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();

            if (!tokenizer.hasMoreTokens() && isOperator(curr))
                throw new InputMismatchException();

            if (isFunction(curr)) stack.push(curr);
            else if (isDelimiter(curr))
                processDelimiter(curr, prev, stack, postfix);
            else
                postfix.add(curr);

            prev = curr;
        }

        while (!stack.isEmpty()) {
            if (isOperator(stack.peek()))
                postfix.add(stack.pop());
            else
                throw new InputMismatchException();
        }
        return postfix;
    }

    private void processDelimiter(String curr, String prev, Stack<String> stack, List<String> postfix) {
        if (curr.equals("("))
            stack.push(curr);
        else if (curr.equals(")")) {
            if (stack.isEmpty())
                throw new InputMismatchException();
            while (!stack.peek().equals("(")) {
                postfix.add(stack.pop());
                if (stack.isEmpty())
                    throw new InputMismatchException();
            }
            stack.pop();
            if (!stack.isEmpty() && isFunction(stack.peek()))
                postfix.add(stack.pop());
        }
        else {
            if (curr.equals("-") && (prev == null || prev.equals("(")))
                curr = uMinus;
            else
                while (!stack.isEmpty() &&
                        ((!isRightAssociative(curr) && priority(curr) <= priority(stack.peek()))
                                || (isRightAssociative(curr) && priority(curr) < priority(stack.peek()))))
                    postfix.add(stack.pop());
            stack.push(curr);
        }
    }

    private boolean isDelimiter(String token) {
        if (operators.contains(token)) return true;
        return (brackets.contains(token));
    }

    private boolean isOperator(String token) {
        return  operators.contains(token);
    }

    private boolean isFunction(String token) {
        return functions.contains(token);
    }

    private boolean isRightAssociative(String token) {
        return rightAssociativeOperators.contains(token);
    }

    private int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals(uMinus)) return 3;
        if (token.equals("*") || token.equals("/")) return 4;
        if (token.equals("^")) return 5;
        return 6;
    }


    double calculate(String infix) {
        List<String> postfix = toPostfix(infix);
        Stack<Double> stack = new Stack<>();
        try {
            for (String x : postfix) {
                if (x.equals(uMinus)) stack.push(-stack.pop());
                else if (operators.contains(x)) {
                    double a = stack.pop();
                    double b = stack.pop();
                    stack.push(operationsDict.get(x).calculate(b, a));
                } else if (functions.contains(x))
                    stack.push(functionsDict.get(x).calculate(stack.pop()));
                else if (constants.contains(x))
                    stack.push(constantsDict.get(x));
                else stack.push(Double.valueOf(x));
            }
        }
        catch (Exception exp) {
            throw new InputMismatchException();
        }
        if (!stack.empty())
            return stack.pop();
        return 0;
    }

    private void initOperationsAndFunctions() {
        operationsDict = new HashMap<>();
        functionsDict = new HashMap<>();
        constantsDict = new HashMap<>();

        operationsDict.put("+", Double::sum);
        operationsDict.put("-", (a, b) -> a - b);
        operationsDict.put("*", (a, b) -> a * b);
        operationsDict.put("/", (a, b) -> a / b);
        operationsDict.put("^", Math::pow);

        functionsDict.put("sin", Math::sin);
        functionsDict.put("cos", Math::cos);
        functionsDict.put("tg", Math::tan);
        functionsDict.put("ctg", a -> 1 / Math.tan(a));
        functionsDict.put("ln", Math::log);
        functionsDict.put("exp", Math::exp);
        functionsDict.put("log2", a -> Math.log(a) / Math.log(2));
        functionsDict.put("log10", a -> Math.log(a) / Math.log(10));

        constantsDict.put("π", Math.PI);
        constantsDict.put("e", Math.E);
    }
}