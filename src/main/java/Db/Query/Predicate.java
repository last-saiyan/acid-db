package Db.Query;

import Db.Query.ExpTree;
import Db.catalog.Tuple;
import Db.catalog.TupleDesc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class Predicate {

    private String infix;
    private ExpTree tree;
    private HashSet<String> symbols;

    public Predicate(String predStr, TupleDesc td){
        this.infix = predStr;
        ArrayList<String> tokens = tokenize(infix);
        System.out.println(tokens);
        ArrayList<String> postFix = infixToPostfix(tokens);
        System.out.println(postFix);

        tree = new ExpTree(postFix, td);
    }

    private String  stringAt(String s, int i){
        return s.substring(i, i+1);
    }

    private ArrayList<String> tokenize( String exp){
        ArrayList<String> tokens = new ArrayList<>();
        symbols = new HashSet();
        symbols.add("+");
        symbols.add("-");
        symbols.add("=");
        symbols.add("&");
        symbols.add("|");
        symbols.add("(");
        symbols.add(")");
        symbols.add("*");
        symbols.add("/");
        symbols.add(" ");

        int prevInd = 0;
        int nextInd = 0;

        while (nextInd < exp.length()){
            if(symbols.contains(stringAt(exp, nextInd))){
                if(prevInd != nextInd) {
                    tokens.add(exp.substring(prevInd, nextInd));
                }
                tokens.add(stringAt(exp, nextInd));
                nextInd++;
                prevInd = nextInd;
            }else{
                nextInd++;
            }
        }
        if(prevInd != nextInd) {
            tokens.add(exp.substring(prevInd, nextInd));
        }
        return tokens;
    }

    public boolean evaluatePredicate (Tuple tuple){
        return tree.evalExp(tuple);
    }

    /*
    * A utility function to return precedence of a given operator
    * Higher returned value means higher precedence
    * note:
    * && => &
    * || => |
    * != => !
    * == => =
    * */
    private static int precedence(char ch) {
        switch (ch) {

            case '|':
                return 1;

            case '&':
                return 2;

            case '<':
            case '>':
                return 3;

            case '=':
            case '!':
                return 4;

            case '+':
            case '-':
                return 5;

            case '*':
            case '/':
                return 6;
        }
        return -1;
    }

    private ArrayList<String> infixToPostfix(ArrayList<String> infix){
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (int i = 0; i<infix.size(); ++i) {
            String c = infix.get(i);
//            if scanned string is a space continue
            if(c.equals(" ") || c.equals("")){
                continue;
            }
//            If the scanned character is an operand, add it to output.
            if (!symbols.contains(c)) {
                result.add(c);
//                 If the scanned character is an '(', push it to the stack.
            }else if (c.equals("(")) {
                stack.push(c);
//                If the scanned character is an ')', pop and output from the stack
//                until an '(' is encountered.
            }else if (c.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    result.add(stack.pop());
                if (!stack.isEmpty() && !stack.peek().equals("(")) {
                    System.out.println("invalid expression");
                    return null;
//                  throw error Invalid Expression
                }else
                    stack.pop();
            } else{ // an operator is encountered{
                while (!stack.isEmpty() && precedence(c.charAt(0)) <= precedence(stack.peek().charAt(0))){
                    if(stack.peek().equals("(")) {
                        System.out.println("invalid expression");
                        return null;
//                      throw error Invalid Expression
                    }
                    result.add(stack.pop());
                }
                stack.push(c);
            }
        }
//        pop all the operators from the stack
        while (!stack.isEmpty()){
            if(stack.peek().equals("(")) {
                System.out.println("invalid expression");
                return null;
//               throw error Invalid Expression
            }
            result.add(stack.pop());
        }
        return result;
    }


}