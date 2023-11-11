package net.mirwaldt.basic.records.design.patterns;


import java.util.Arrays;

import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_008_Varargs.Value.FALSE;
import static net.mirwaldt.basic.records.design.patterns.RecordDesignPattern_008_Varargs.Value.TRUE;

@SuppressWarnings("ClassEscapesDefinedScope")
public class RecordDesignPattern_008_Varargs {
    sealed interface Expression permits UnaryExpression, ManyExpression {

    }

    sealed interface UnaryExpression extends Expression permits Value, Variable, Not, Brackets {

    }

    enum Value implements UnaryExpression {
        TRUE, FALSE;
    }

    record Variable(String name) implements UnaryExpression {

    }

    record Not(Expression unnegated) implements UnaryExpression {

    }

    record Brackets(Expression withoutBrackets) implements UnaryExpression {

    }

    sealed interface ManyExpression extends Expression permits And, Or {
        Expression[] right();

        default Expression[] rightWithoutLast() {
            Expression[] rights = right();
            return Arrays.copyOf(rights, rights.length - 1);
        }

        default Expression last() {
            Expression[] rights = right();
            return rights[rights.length - 1];
        }
    }

    record And(Expression left, Expression middle, Expression...right) implements ManyExpression {

    }

    record Or(Expression left, Expression middle, Expression...right) implements ManyExpression {

    }

    public static Expression addBrackets(Expression child, Expression parent) {
        return switch (child) {
            case ManyExpression binary when parent instanceof Not -> new Brackets(addBrackets(binary, child));
            case Or or when parent instanceof And -> new Brackets(addBrackets(or, child));
            case Not not -> new Not(addBrackets(not.unnegated(), not));
            case And and when 0 < and.right().length ->
                    addBrackets((new And(new And(and.left(), and.middle(), and.rightWithoutLast()), and.last())), child);
            case Or or  when 0 < or.right().length ->
                    addBrackets((new Or(new Or(or.left(), or.middle(), or.rightWithoutLast()), or.last())), child);
            case And and -> new And(addBrackets(and.left(), child), addBrackets(and.middle(), child));
            case Or or -> new Or(addBrackets(or.left(), child), addBrackets(or.middle(), child));
            default -> child;
        };
    }

    public static String toString(Expression expression) {
        return switch (expression) {
            case Value value -> value.name();
            case Variable variable -> variable.name();
            case Not not -> "!" + toString(not.unnegated());
            case Brackets inBrackets -> "(" + toString(inBrackets.withoutBrackets()) + ")";
            case And and when 0 < and.right().length ->
                    toString(new And(new And(and.left(), and.middle(), and.rightWithoutLast()), and.last()));
            case And and -> toString(and.left()) + " && " + toString(and.middle());
            case Or or when 0 < or.right().length ->
                    toString(new Or(new Or(or.left(), or.middle(), or.rightWithoutLast()), or.last()));
            case Or or -> toString(or.left()) + " || " + toString(or.middle());
        };
    }

    public static void main(String[] args) {
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Variable C = new Variable("C");
        Variable D = new Variable("D");
        Variable E = new Variable("E");
        
        Expression expression = new And(new Or(new And(A, FALSE, new Not(B)), new Not(new And(C, D)), FALSE), TRUE);
        Expression withBrackets = addBrackets(expression, null);
        System.out.println(toString(withBrackets)); // prints out "(A || FALSE && !B || !(C && D) || FALSE) && TRUE"
    }
}
